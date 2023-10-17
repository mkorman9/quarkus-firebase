package com.github.mkorman9.firebase;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.smallrye.mutiny.subscription.UniEmitter;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.io.IOException;

@ApplicationScoped
public class FirebaseService {
    private FirebaseApp firebaseApp;
    private FirebaseAuth firebaseAuth;

    @Inject
    ManagedExecutor managedExecutor;

    @PostConstruct
    public void setup() throws IOException {
        this.firebaseApp = FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
        );
        this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    }

    public void verifyTokenAsync(String token, UniEmitter<? super FirebaseToken> emitter) {
        var future = firebaseAuth.verifyIdTokenAsync(token);
        ApiFutures.addCallback(
            future,
            new ApiFutureCallback<>() {
                @Override
                public void onSuccess(FirebaseToken firebaseToken) {
                    emitter.complete(firebaseToken);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    emitter.fail(throwable);
                }
            },
            managedExecutor
        );
    }
}
