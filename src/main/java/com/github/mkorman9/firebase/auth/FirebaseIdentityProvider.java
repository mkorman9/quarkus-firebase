package com.github.mkorman9.firebase.auth;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FirebaseIdentityProvider implements IdentityProvider<FirebaseAuthenticationRequest> {
    @Inject
    FirebaseService firebaseService;

    @Override
    public Uni<SecurityIdentity> authenticate(
        FirebaseAuthenticationRequest request,
        AuthenticationRequestContext context
    ) {
        return Uni.createFrom()
            .completionStage(() -> firebaseService.verifyTokenAsync(request.getToken()).toCompletionStage())
            .onItem().transform(firebaseAuth ->
                QuarkusSecurityIdentity.builder()
                    .setPrincipal(firebaseAuth)
                    .build()
            );
    }

    @Override
    public Class<FirebaseAuthenticationRequest> getRequestType() {
        return FirebaseAuthenticationRequest.class;
    }
}
