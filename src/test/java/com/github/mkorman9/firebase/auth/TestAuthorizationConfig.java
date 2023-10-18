package com.github.mkorman9.firebase.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.Map;

@ApplicationScoped
public class TestAuthorizationConfig {
    private static final FirebaseAuthorization MOCK_AUTHORIZATION = Mockito.mock(FirebaseAuthorization.class);
    private static boolean isAuthorized = false;

    public static void mockUid(String uid) {
        Mockito.when(MOCK_AUTHORIZATION.getUid()).thenReturn(uid);
        Mockito.when(MOCK_AUTHORIZATION.getName()).thenReturn(uid);
        isAuthorized = true;
    }

    public static void mockTenantId(String tenantId) {
        Mockito.when(MOCK_AUTHORIZATION.getTenantId()).thenReturn(tenantId);
    }

    public static void mockIssuer(String issuer) {
        Mockito.when(MOCK_AUTHORIZATION.getIssuer()).thenReturn(issuer);
    }

    public static void mockDisplayName(String displayName) {
        Mockito.when(MOCK_AUTHORIZATION.getDisplayName()).thenReturn(displayName);
    }

    public static void mockPicture(String picture) {
        Mockito.when(MOCK_AUTHORIZATION.getPicture()).thenReturn(picture);
    }

    public static void mockEmail(String email) {
        Mockito.when(MOCK_AUTHORIZATION.getEmail()).thenReturn(email);
    }

    public static void mockEmailVerified(boolean isEmailVerified) {
        Mockito.when(MOCK_AUTHORIZATION.isEmailVerified()).thenReturn(isEmailVerified);
    }

    public static void mockClaims(Map<String, Object> claims) {
        Mockito.when(MOCK_AUTHORIZATION.getClaims()).thenReturn(claims);
    }

    public static void reset() {
        Mockito.reset(MOCK_AUTHORIZATION);
        isAuthorized = false;
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public void enhanceRequest(ContainerRequestContext context) {
        if (isAuthorized) {
            context.setSecurityContext(createMockSecurityContext());
        }
    }

    private SecurityContext createMockSecurityContext() {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return MOCK_AUTHORIZATION;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.DIGEST_AUTH;
            }
        };
    }
}
