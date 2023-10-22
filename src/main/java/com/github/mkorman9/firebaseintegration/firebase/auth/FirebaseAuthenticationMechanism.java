package com.github.mkorman9.firebaseintegration.firebase.auth;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
@UnlessBuildProfile("test")
class FirebaseAuthenticationMechanism implements HttpAuthenticationMechanism {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final int BEARER_TOKEN_TYPE_LENGTH = BEARER_TOKEN_TYPE.length();

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        var header = context.request().getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_TOKEN_TYPE)) {
            return Uni.createFrom().nullItem();
        }

        var token = header.substring(BEARER_TOKEN_TYPE_LENGTH).trim();
        return identityProviderManager.authenticate(new FirebaseAuthenticationRequest(token))
            .onFailure().recoverWithItem(throwable -> {
                if (throwable instanceof AuthenticationServerException) {
                    context.fail(throwable);
                }

                return null;
            });
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Set.of(FirebaseAuthenticationRequest.class);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Boolean> sendChallenge(RoutingContext context) {
        return Uni.createFrom().item(false);
    }
}
