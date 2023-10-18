package com.github.mkorman9.firebase.auth;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@RequestScoped
public class FirebaseUserPrincipalProvider {
    @Produces
    public FirebaseUserPrincipal provideFirebaseUserPrincipal(@Context SecurityContext securityContext) {
        if (securityContext == null) {
            return null;
        }

        try {
            return (FirebaseUserPrincipal) securityContext.getUserPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
