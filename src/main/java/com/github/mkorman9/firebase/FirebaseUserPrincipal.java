package com.github.mkorman9.firebase;

import com.google.firebase.auth.FirebaseToken;

import java.security.Principal;
import java.util.Map;

public class FirebaseUserPrincipal implements Principal {
    private final FirebaseToken firebaseToken;

    public FirebaseUserPrincipal(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String getName() {
        return firebaseToken.getUid();
    }

    public String getTenantId() {
        return firebaseToken.getTenantId();
    }

    public String getIssuer() {
        return firebaseToken.getIssuer();
    }

    public String getDisplayName() {
        return firebaseToken.getName();
    }

    public String getPicture() {
        return firebaseToken.getPicture();
    }

    public String getEmail() {
        return firebaseToken.getEmail();
    }

    public boolean isEmailVerified() {
        return firebaseToken.isEmailVerified();
    }

    public Map<String, Object> getClaims() {
        return firebaseToken.getClaims();
    }
}
