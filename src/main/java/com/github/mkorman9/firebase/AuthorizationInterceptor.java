package com.github.mkorman9.firebase;

import com.google.firebase.auth.FirebaseToken;
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
public class AuthorizationInterceptor {
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
            .<FirebaseToken>emitter(emitter -> {
                firebaseService.verifyTokenAsync(token, emitter);
            })
            .onItem().transform(firebaseToken -> {
                context.setSecurityContext(createSecurityContext(firebaseToken));
                return null;
            })
            .onFailure().recoverWithNull()
            .replaceWithVoid();
    }

    public static Optional<String> extractToken(ContainerRequestContext context) {
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

    private SecurityContext createSecurityContext(FirebaseToken firebaseToken) {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new FirebaseUserPrincipal(firebaseToken);
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
