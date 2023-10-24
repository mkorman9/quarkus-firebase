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

import java.io.FileInputStream;
import java.io.IOException;

@ApplicationScoped
@UnlessBuildProfile("test")
public class FirebaseInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseInitializer.class);

    @ConfigProperty(name = "firebase.emulator.enabled", defaultValue = "false")
    boolean emulatorEnabled;

    @ConfigProperty(name = "firebase.emulator.project-id", defaultValue = "emulator-project")
    String emulatorProjectId;

    @ConfigProperty(name = "firebase.auth.emulator-url", defaultValue = "127.0.0.1:9099")
    String authEmulatorUrl;

    @ConfigProperty(name = "firebase.credentials-path", defaultValue = "firebase-credentials.json")
    String credentialsPath;

    public void onStartup(@Observes StartupEvent startupEvent) throws Exception {
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

    private FirebaseOptions createFirebaseOptions() throws IOException {
        if (emulatorEnabled) {
            FirebaseProcessEnvironment.setenv("FIREBASE_AUTH_EMULATOR_HOST", authEmulatorUrl);
            LOG.info("Firebase integration is running in emulator mode");

            return FirebaseOptions.builder()
                .setProjectId(emulatorProjectId)
                .setCredentials(new EmulatorCredentials())
                .build();
        } else {
            var credentialsStream = new FileInputStream(credentialsPath);
            LOG.info("Firebase integration is running in production mode");

            return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();
        }
    }
}
