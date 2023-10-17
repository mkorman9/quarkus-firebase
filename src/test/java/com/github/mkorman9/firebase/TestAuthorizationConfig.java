package com.github.mkorman9.firebase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.mockito.Mockito;

import java.security.Principal;

@ApplicationScoped
public class TestAuthorizationConfig {
    private static final FirebaseUserPrincipal MOCK_PRINCIPAL = Mockito.mock(FirebaseUserPrincipal.class);
    private static boolean isAuthorized = false;

    public static void mockAuthorization(String uid) {
        Mockito.when(MOCK_PRINCIPAL.getName()).thenReturn(uid);
        isAuthorized = true;
    }

    public static void resetAuthorization() {
        Mockito.reset(MOCK_PRINCIPAL);
        isAuthorized = false;
    }

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public void interceptRequest(ContainerRequestContext context) {
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
}
