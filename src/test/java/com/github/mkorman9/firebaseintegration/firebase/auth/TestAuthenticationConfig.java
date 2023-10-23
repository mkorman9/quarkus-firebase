package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.BaseAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class TestAuthenticationConfig implements
    HttpAuthenticationMechanism,
    IdentityProvider<TestAuthenticationConfig.FakeFirebaseAuthenticationRequest> {
    private static FirebaseAuthentication mockAuthentication;

    public static void setup(FirebaseAuthentication authentication) {
        mockAuthentication = authentication;
    }

    public static void reset() {
        mockAuthentication = null;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
        RoutingContext context,
        IdentityProviderManager identityProviderManager
    ) {
        if (mockAuthentication == null) {
            return Uni.createFrom().nullItem();
        }

        return identityProviderManager.authenticate(new FakeFirebaseAuthenticationRequest());
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
        FakeFirebaseAuthenticationRequest request,
        AuthenticationRequestContext context
    ) {
        return Uni.createFrom().item(() ->
            QuarkusSecurityIdentity.builder()
                .setPrincipal(mockAuthentication)
                .build()
        );
    }

    @Override
    public Class<FakeFirebaseAuthenticationRequest> getRequestType() {
        return FakeFirebaseAuthenticationRequest.class;
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Set.of(FakeFirebaseAuthenticationRequest.class);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Boolean> sendChallenge(RoutingContext context) {
        return Uni.createFrom().item(false);
    }

    public static class FakeFirebaseAuthenticationRequest extends BaseAuthenticationRequest {
    }
}
