package com.github.mkorman9.firebaseintegration.firebase.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirebaseAuthenticationService {
    public FirebaseAuthentication verifyToken(String token) {
        try {
            var firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return FirebaseAuthentication.from(firebaseToken);
        } catch (FirebaseAuthException e) {
            if (isClientException(e)) {
                throw new RuntimeException(e);
            } else {
                throw new AuthenticationServerException(e);
            }
        }
    }

    private boolean isClientException(FirebaseAuthException e) {
        return e.getCause() == null  // thrown on invalid/expired JWT token
            || e.getCause() instanceof IllegalArgumentException;  // thrown on malformed JWT token
    }
}
