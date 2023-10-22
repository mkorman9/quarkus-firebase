package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;

@RequestScoped
class FirebaseAuthenticationProvider {
    @Produces
    public FirebaseAuthentication provideFirebaseAuthentication(@Context SecurityIdentity securityIdentity) {
        if (securityIdentity == null) {
            return null;
        }

        try {
            return (FirebaseAuthentication) securityIdentity.getPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
