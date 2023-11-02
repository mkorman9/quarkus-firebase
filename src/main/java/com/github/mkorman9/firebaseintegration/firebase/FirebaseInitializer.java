package com.github.mkorman9.firebaseintegration.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.internal.EmulatorCredentials;
import com.google.firebase.internal.FirebaseProcessEnvironment;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@ApplicationScoped
@UnlessBuildProfile("test")
public class FirebaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(FirebaseInitializer.class);

    @ConfigProperty(name = "firebase.emulator.enabled", defaultValue = "false")
    boolean emulatorEnabled;

    @ConfigProperty(name = "firebase.emulator.project-id", defaultValue = "emulator-project")
    String emulatorProjectId;

    @ConfigProperty(name = "firebase.auth.emulator-url", defaultValue = "127.0.0.1:9099")
    String authEmulatorUrl;

    @ConfigProperty(name = "firebase.credentials", defaultValue = "{}")
    String credentialsContent;

    @ConfigProperty(name = "firebase.credentials.path", defaultValue = "serviceAccountKey.json")
    String credentialsPath;

    public void onStartup(@Observes StartupEvent startupEvent) {
        // delete default app instance to prevent problems with hot reloads
        try {
            FirebaseApp.getInstance().delete();
        } catch (IllegalStateException e) {
            // ignore
        }

        var firebaseOptions = createFirebaseOptions();
        var firebaseApp = FirebaseApp.initializeApp(firebaseOptions);

        // initialize services
        FirebaseAuth.getInstance(firebaseApp);
    }

    private FirebaseOptions createFirebaseOptions() {
        if (emulatorEnabled) {
            FirebaseProcessEnvironment.setenv("FIREBASE_AUTH_EMULATOR_HOST", authEmulatorUrl);
            log.info("Firebase integration is running in emulator mode");

            return FirebaseOptions.builder()
                .setProjectId(emulatorProjectId)
                .setCredentials(new EmulatorCredentials())
                .build();
        } else {
            log.info("Firebase integration is running in production mode");

            var credentials = resolveCredentials();
            return FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        }
    }

    private GoogleCredentials resolveCredentials() {
        // try environment variable
        try (var credentialsStream = new ByteArrayInputStream(credentialsContent.getBytes())) {
            return GoogleCredentials.fromStream(credentialsStream);
        } catch (IOException e) {
            // ignore
        }

        // try file
        try (var credentialsStream = new FileInputStream(credentialsPath)) {
            return GoogleCredentials.fromStream(credentialsStream);
        } catch (IOException e) {
            // ignore
        }

        // try platform-default credentials
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            // ignore
        }

        throw new IllegalStateException("Unable to resolve Firebase credentials");
    }
}
