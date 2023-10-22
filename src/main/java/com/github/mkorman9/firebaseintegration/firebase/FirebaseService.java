package com.github.mkorman9.firebaseintegration.firebase;

import com.github.mkorman9.firebaseintegration.firebase.auth.AuthenticationServerException;
import com.github.mkorman9.firebaseintegration.firebase.auth.FirebaseAuthentication;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.quarkus.runtime.ExecutorRecorder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirebaseService {
    public Future<FirebaseAuthentication> verifyTokenAsync(String token) {
        var promise = Promise.<FirebaseAuthentication>promise();
        var future = FirebaseAuth.getInstance().verifyIdTokenAsync(token);
        ApiFutures.addCallback(
            future,
            new ApiFutureCallback<>() {
                @Override
                public void onSuccess(FirebaseToken firebaseToken) {
                    promise.complete(new FirebaseAuthentication(firebaseToken));
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (throwable.getCause() == null  // thrown on invalid/expired JWT token
                        || throwable.getCause() instanceof IllegalArgumentException  // thrown on malformed JWT token
                    ) {
                        promise.fail(throwable);
                    } else {
                        promise.fail(new AuthenticationServerException(throwable));
                    }
                }
            },
            ExecutorRecorder.getCurrent()
        );

        return promise.future();
    }
}
