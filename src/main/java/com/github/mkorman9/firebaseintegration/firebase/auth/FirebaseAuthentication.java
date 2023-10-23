package com.github.mkorman9.firebaseintegration.firebase.auth;

import com.google.firebase.auth.FirebaseToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class FirebaseAuthentication implements Principal {
    private final String uid;
    private final String tenantId;
    private final String issuer;
    private final String displayName;
    private final String picture;
    private final String email;
    private final boolean isEmailVerified;
    private final Map<String, Object> claims;

    public FirebaseAuthentication(FirebaseToken firebaseToken) {
        this.uid = firebaseToken.getUid();
        this.tenantId = firebaseToken.getTenantId();
        this.issuer = firebaseToken.getIssuer();
        this.displayName = firebaseToken.getName();
        this.picture = firebaseToken.getPicture();
        this.email = firebaseToken.getEmail();
        this.isEmailVerified = firebaseToken.isEmailVerified();
        this.claims = firebaseToken.getClaims();
    }

    @Override
    public String getName() {
        return getUid();
    }
}
