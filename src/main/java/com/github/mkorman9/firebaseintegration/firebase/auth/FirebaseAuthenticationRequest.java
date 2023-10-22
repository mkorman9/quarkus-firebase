package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.security.identity.request.BaseAuthenticationRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class FirebaseAuthenticationRequest extends BaseAuthenticationRequest {
    private final String token;
}
