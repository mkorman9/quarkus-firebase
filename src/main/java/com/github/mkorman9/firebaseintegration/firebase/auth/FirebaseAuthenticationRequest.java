package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.security.identity.request.BaseAuthenticationRequest;

class FirebaseAuthenticationRequest extends BaseAuthenticationRequest {
    private final String token;

    public FirebaseAuthenticationRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
