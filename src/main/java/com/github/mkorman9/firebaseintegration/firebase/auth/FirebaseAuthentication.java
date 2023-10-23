package com.github.mkorman9.firebaseintegration.firebase.auth;

import com.google.firebase.auth.FirebaseToken;

import java.security.Principal;
import java.util.Map;

public record FirebaseAuthentication(
    String uid,
    String tenantId,
    String issuer,
    String displayName,
    String picture,
    String email,
    boolean isEmailVerified,
    Map<String, Object> claims
) implements Principal {
    public static FirebaseAuthentication from(FirebaseToken firebaseToken) {
        return new FirebaseAuthentication(
            firebaseToken.getUid(),
            firebaseToken.getTenantId(),
            firebaseToken.getIssuer(),
            firebaseToken.getName(),
            firebaseToken.getPicture(),
            firebaseToken.getEmail(),
            firebaseToken.isEmailVerified(),
            firebaseToken.getClaims()
        );
    }

    @Override
    public String getName() {
        return uid();
    }
}
