package com.github.mkorman9.firebase.auth;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import java.security.Principal;
import java.util.Optional;

@ApplicationScoped
@UnlessBuildProfile("test")
public class AuthorizationTokenInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Inject
    FirebaseService firebaseService;

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHORIZATION)
    public Uni<Response> interceptRequest(ContainerRequestContext context) {
        var maybeToken = extractToken(context);
        if (maybeToken.isEmpty()) {
            return Uni.createFrom().nullItem();
        }

        var token = maybeToken.get();

        return Uni.createFrom()
            .completionStage(() -> firebaseService.verifyTokenAsync(token).toCompletionStage())
            .onItem().<Response>transform(firebaseAuth -> {
                context.setSecurityContext(createSecurityContext(firebaseAuth));
                return null;
            })
            .onFailure().recoverWithItem(throwable -> {
                if (throwable instanceof AuthorizationServerException) {
                    return Response.serverError().build();
                }

                return null;
            });
    }

    private Optional<String> extractToken(ContainerRequestContext context) {
        var header = context
            .getHeaders()
            .getFirst(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_TOKEN_TYPE)) {
            return Optional.empty();
        }

        var token = header.substring(BEARER_TOKEN_TYPE.length()).trim();
        return Optional.of(token);
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
