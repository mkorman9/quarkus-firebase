package com.github.mkorman9.firebase;

import com.github.mkorman9.firebase.auth.FirebaseUserPrincipal;
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
    private static final FirebaseUserPrincipal MOCK_PRINCIPAL = Mockito.mock(FirebaseUserPrincipal.class);
    private static boolean isAuthorized = false;

    public static PrincipalFieldsBuilder mockPrincipal(String uid) {
        Mockito.when(MOCK_PRINCIPAL.getName()).thenReturn(uid);
        isAuthorized = true;

        return new PrincipalFieldsBuilder();
    }

    public static void reset() {
        Mockito.reset(MOCK_PRINCIPAL);
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
                return MOCK_PRINCIPAL;
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

    public static class PrincipalFieldsBuilder {
        private PrincipalFieldsBuilder() {
        }

        public PrincipalFieldsBuilder withTenantId(String tenantId) {
            Mockito.when(MOCK_PRINCIPAL.getTenantId()).thenReturn(tenantId);
            return this;
        }

        public PrincipalFieldsBuilder withIssuer(String issuer) {
            Mockito.when(MOCK_PRINCIPAL.getIssuer()).thenReturn(issuer);
            return this;
        }

        public PrincipalFieldsBuilder withDisplayName(String displayName) {
            Mockito.when(MOCK_PRINCIPAL.getDisplayName()).thenReturn(displayName);
            return this;
        }

        public PrincipalFieldsBuilder withPicture(String picture) {
            Mockito.when(MOCK_PRINCIPAL.getPicture()).thenReturn(picture);
            return this;
        }

        public PrincipalFieldsBuilder withEmail(String email) {
            Mockito.when(MOCK_PRINCIPAL.getEmail()).thenReturn(email);
            return this;
        }

        public PrincipalFieldsBuilder withEmailVerified(boolean isEmailVerified) {
            Mockito.when(MOCK_PRINCIPAL.isEmailVerified()).thenReturn(isEmailVerified);
            return this;
        }

        public PrincipalFieldsBuilder withClaims(Map<String, Object> claims) {
            Mockito.when(MOCK_PRINCIPAL.getClaims()).thenReturn(claims);
            return this;
        }
    }
}
