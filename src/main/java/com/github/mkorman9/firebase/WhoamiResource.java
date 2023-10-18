package com.github.mkorman9.firebase;

import com.github.mkorman9.firebase.auth.FirebaseAuthorization;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WhoamiResource {
    @GET
    @Path("/secured")
    @Authenticated
    public WhoamiResponse getWhoamiSecured(@Context FirebaseAuthorization authorization) {
        return new WhoamiResponse(authorization.getUid());
    }

    @GET
    @Path("/unsecured")
    public WhoamiResponse getWhoamiUnsecured() {
        return new WhoamiResponse("anonymous");
    }

    public record WhoamiResponse(
        String uid
    ) {
    }
}
