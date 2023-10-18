package com.github.mkorman9.firebase.auth;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@RequestScoped
public class FirebaseAuthorizationProvider {
    @Produces
    public FirebaseAuthorization provideFirebaseAuthorization(@Context SecurityContext securityContext) {
        if (securityContext == null) {
            return null;
        }

        try {
            return (FirebaseAuthorization) securityContext.getUserPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
