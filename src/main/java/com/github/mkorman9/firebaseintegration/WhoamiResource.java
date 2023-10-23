package com.github.mkorman9.firebaseintegration;

import com.github.mkorman9.firebaseintegration.firebase.auth.FirebaseAuthentication;
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
    public WhoamiResponse getWhoamiSecured(@Context FirebaseAuthentication authentication) {
        return new WhoamiResponse(authentication.uid());
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
