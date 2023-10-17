package com.github.mkorman9.firebase;

import com.github.mkorman9.firebase.auth.FirebaseUserPrincipal;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/secured")
@Produces(MediaType.APPLICATION_JSON)
public class SecuredResource {
    @GET
    @Authenticated
    public WhoamiResponse getWhoami(@Context SecurityContext securityContext) {
        var user = (FirebaseUserPrincipal) securityContext.getUserPrincipal();
        return new WhoamiResponse(user.getName());
    }

    public record WhoamiResponse(
        String uid
    ) {
    }
}
