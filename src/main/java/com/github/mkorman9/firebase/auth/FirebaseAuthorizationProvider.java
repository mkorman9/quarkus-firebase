package com.github.mkorman9.firebase.auth;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;

@RequestScoped
public class FirebaseAuthorizationProvider {
    @Produces
    public FirebaseAuthorization provideFirebaseAuthorization(@Context SecurityIdentity securityIdentity) {
        if (securityIdentity == null) {
            return null;
        }

        try {
            return (FirebaseAuthorization) securityIdentity.getPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
