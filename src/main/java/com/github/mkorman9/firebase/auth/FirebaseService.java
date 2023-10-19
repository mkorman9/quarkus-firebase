package com.github.mkorman9.firebase.auth;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.internal.EmulatorCredentials;
import com.google.firebase.internal.FirebaseProcessEnvironment;
import io.quarkus.runtime.ExecutorRecorder;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;

@ApplicationScoped
@Slf4j
public class FirebaseService {
    private FirebaseAuth firebaseAuth;

    @ConfigProperty(name = "firebase.emulator.enabled", defaultValue = "false")
    boolean emulatorEnabled;

    @ConfigProperty(name = "firebase.emulator.project-id", defaultValue = "emulator-project")
    String emulatorProjectId;

    @ConfigProperty(name = "firebase.auth.emulator-url", defaultValue = "127.0.0.1:9099")
    String authEmulatorUrl;

    @ConfigProperty(name = "firebase.credentials-path", defaultValue = "firebase-credentials.json")
    String credentialsPath;

    public void onStartup(@Observes StartupEvent startupEvent) {
        if (ProfileManager.getLaunchMode() == LaunchMode.TEST) {
            return;
        }

        try {
            // delete default app instance to prevent problems with hot reloads
            try {
                FirebaseApp.getInstance().delete();
            } catch (IllegalStateException e) {
                // ignore
            }

            FirebaseOptions firebaseOptions;
            if (emulatorEnabled) {
                FirebaseProcessEnvironment.setenv("FIREBASE_AUTH_EMULATOR_HOST", authEmulatorUrl);
                firebaseOptions = FirebaseOptions.builder()
                    .setProjectId(emulatorProjectId)
                    .setCredentials(new EmulatorCredentials())
                    .build();

                log.info("Firebase integration is running in emulator mode");
            } else {
                firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                    .build();

                log.info("Firebase integration is running in production mode");
            }

            var firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
            this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
        } catch (Exception e) {
            log.error("Failed to initialize Firebase", e);
            Quarkus.asyncExit(1);
        }
    }

    public Future<FirebaseAuthorization> verifyTokenAsync(String token) {
        var promise = Promise.<FirebaseAuthorization>promise();
        var future = firebaseAuth.verifyIdTokenAsync(token);
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
