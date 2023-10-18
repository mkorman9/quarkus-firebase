package com.github.mkorman9.firebase.auth;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import java.security.Principal;
import java.util.Optional;

@ApplicationScoped
@UnlessBuildProfile("test")
public class AuthorizationTokenInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Inject
    FirebaseService firebaseService;

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Void> interceptRequest(ContainerRequestContext context) {
        var maybeToken = extractToken(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        var token = maybeToken.get();

        return Uni.createFrom()
            .completionStage(() -> firebaseService.verifyTokenAsync(token).toCompletionStage())
            .onItem().invoke(firebaseAuth -> context.setSecurityContext(createSecurityContext(firebaseAuth)))
            .onFailure().recoverWithNull()
            .replaceWithVoid();
    }

    private Optional<String> extractToken(ContainerRequestContext context) {
        var headerValue = context
            .getHeaders()
            .getFirst(AUTHORIZATION_HEADER);
        if (headerValue == null) {
            return Optional.empty();
        }

        var headerParts = headerValue.split("\\s+");
        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase(TOKEN_TYPE)) {
            return Optional.empty();
        }

        return Optional.of(headerParts[1]);
    }

    private SecurityContext createSecurityContext(FirebaseAuthorization authorization) {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return authorization;
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
