package com.github.mkorman9.firebase.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.internal.EmulatorCredentials;
import com.google.firebase.internal.FirebaseProcessEnvironment;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;

@ApplicationScoped
@Slf4j
public class FirebaseInitializer {
    @ConfigProperty(name = "firebase.emulator.enabled", defaultValue = "false")
    boolean emulatorEnabled;

    @ConfigProperty(name = "firebase.emulator.project-id", defaultValue = "emulator-project")
    String emulatorProjectId;

    @ConfigProperty(name = "firebase.auth.emulator-url", defaultValue = "127.0.0.1:9099")
    String authEmulatorUrl;

    @ConfigProperty(name = "firebase.credentials-path", defaultValue = "firebase-credentials.json")
    String credentialsPath;

    public void onStartup(@Observes StartupEvent startupEvent) throws Exception {
        if (ProfileManager.getLaunchMode() == LaunchMode.TEST) {
            return;
        }

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
        FirebaseAuth.getInstance(firebaseApp);
    }
}
