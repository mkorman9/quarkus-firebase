package com.github.mkorman9.firebase.auth;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;

@RequestScoped
public class FirebaseAuthenticationProvider {
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
