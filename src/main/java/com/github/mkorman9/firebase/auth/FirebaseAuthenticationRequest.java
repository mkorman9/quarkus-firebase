package com.github.mkorman9.firebase.auth;

import io.quarkus.security.identity.request.BaseAuthenticationRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FirebaseAuthenticationRequest extends BaseAuthenticationRequest {
    private final String token;
}
