package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@UnlessBuildProfile("test")
class FirebaseIdentityProvider implements IdentityProvider<FirebaseAuthenticationRequest> {
    @Inject
    FirebaseAuthenticationService firebaseAuthenticationService;

    @Override
    public Uni<SecurityIdentity> authenticate(
        FirebaseAuthenticationRequest request,
        AuthenticationRequestContext context
    ) {
        return Uni.createFrom()
            .completionStage(() ->
                firebaseAuthenticationService.verifyTokenAsync(request.getToken()).toCompletionStage()
            )
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
