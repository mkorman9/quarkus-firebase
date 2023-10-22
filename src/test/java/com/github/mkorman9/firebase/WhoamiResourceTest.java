package com.github.mkorman9.firebase;

import com.github.mkorman9.firebase.auth.WithFirebaseAuthentication;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class WhoamiResourceTest {
    @Test
    @WithFirebaseAuthentication(uid = "test_user")
    public void shouldAuthorizeWithProperUid() {
        // given when
        var whoami = given()
            .when().get("/secured")
            .then()
            .statusCode(200)
            .extract().body().as(WhoamiResource.WhoamiResponse.class);

        // then
        assertThat(whoami.uid()).isEqualTo("test_user");
    }

    @Test
    public void shouldDenyUnauthorizedAccess() {
        given()
            .when().get("/secured")
            .then()
            .statusCode(401);
    }

    @Test
    public void shouldAllowUnsecuredEndpointAccess() {
        // given when
        var whoami = given()
            .when().get("/unsecured")
            .then()
            .statusCode(200)
            .extract().body().as(WhoamiResource.WhoamiResponse.class);

        // then
        assertThat(whoami.uid()).isEqualTo("anonymous");
    }
}
