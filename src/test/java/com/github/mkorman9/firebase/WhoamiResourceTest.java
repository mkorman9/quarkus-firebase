package com.github.mkorman9.firebase;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class WhoamiResourceTest {
    @AfterEach
    public void tearDown() {
        TestAuthorizationConfig.reset();
    }

    @Test
    public void shouldAuthorizeWithProperUid() {
        // given
        var uid = "test_user";
        TestAuthorizationConfig.mockPrincipal(uid);

        // when
        var whoami = given()
            .when().get("/secured")
            .then()
            .statusCode(200)
            .extract().body().as(WhoamiResource.WhoamiResponse.class);

        // then
        assertThat(whoami.uid()).isEqualTo(uid);
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
