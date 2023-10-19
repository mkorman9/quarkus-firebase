package com.github.mkorman9.firebase.auth;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.quarkus.runtime.ExecutorRecorder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class FirebaseService {
    public Future<FirebaseAuthorization> verifyTokenAsync(String token) {
        var promise = Promise.<FirebaseAuthorization>promise();
        var future = FirebaseAuth.getInstance().verifyIdTokenAsync(token);
        ApiFutures.addCallback(
            future,
            new ApiFutureCallback<>() {
                @Override
                public void onSuccess(FirebaseToken firebaseToken) {
                    promise.complete(new FirebaseAuthorization(firebaseToken));
                }

                @Override
                public void onFailure(Throwable throwable) {
                    promise.fail(throwable);

                    if (throwable.getCause() != null  // thrown on invalid/expired JWT token
                        && !(throwable.getCause() instanceof IllegalArgumentException)  // thrown on malformed JWT token
                    ) {
                        log.error("Error while authorizing Firebase token", throwable);
                    }
                }
            },
            ExecutorRecorder.getCurrent()
        );

        return promise.future();
    }
}
