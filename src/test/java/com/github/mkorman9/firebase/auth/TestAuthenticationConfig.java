package com.github.mkorman9.firebase.auth;

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
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TestAuthenticationConfig implements
    HttpAuthenticationMechanism,
    IdentityProvider<TestAuthenticationConfig.FakeFirebaseAuthenticationRequest> {
    private static final FirebaseAuthentication MOCK_AUTHENTICATION = Mockito.mock(FirebaseAuthentication.class);
    private static boolean isAuthenticated = false;

    public static void mockUid(String uid) {
        Mockito.when(MOCK_AUTHENTICATION.getUid()).thenReturn(uid);
        Mockito.when(MOCK_AUTHENTICATION.getName()).thenReturn(uid);
        isAuthenticated = true;
    }

    public static void mockTenantId(String tenantId) {
        Mockito.when(MOCK_AUTHENTICATION.getTenantId()).thenReturn(tenantId);
    }

    public static void mockIssuer(String issuer) {
        Mockito.when(MOCK_AUTHENTICATION.getIssuer()).thenReturn(issuer);
    }

    public static void mockDisplayName(String displayName) {
        Mockito.when(MOCK_AUTHENTICATION.getDisplayName()).thenReturn(displayName);
    }

    public static void mockPicture(String picture) {
        Mockito.when(MOCK_AUTHENTICATION.getPicture()).thenReturn(picture);
    }

    public static void mockEmail(String email) {
        Mockito.when(MOCK_AUTHENTICATION.getEmail()).thenReturn(email);
    }

    public static void mockEmailVerified(boolean isEmailVerified) {
        Mockito.when(MOCK_AUTHENTICATION.isEmailVerified()).thenReturn(isEmailVerified);
    }

    public static void mockClaims(Map<String, Object> claims) {
        Mockito.when(MOCK_AUTHENTICATION.getClaims()).thenReturn(claims);
    }

    public static void reset() {
        Mockito.reset(MOCK_AUTHENTICATION);
        isAuthenticated = false;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        if (!isAuthenticated) {
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
                .setPrincipal(MOCK_AUTHENTICATION)
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
