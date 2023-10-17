package com.github.mkorman9.firebase;

import com.google.firebase.auth.FirebaseToken;

import java.security.Principal;

public class FirebaseUserPrincipal implements Principal {
    private final FirebaseToken firebaseToken;

    public FirebaseUserPrincipal(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String getName() {
        return firebaseToken.getUid();
    }

    public FirebaseToken getToken() {
        return firebaseToken;
    }
}
