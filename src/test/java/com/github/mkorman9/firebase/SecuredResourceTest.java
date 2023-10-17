package com.github.mkorman9.firebase;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class SecuredResourceTest {
    @AfterEach
    public void tearDown() {
        TestAuthorizationConfig.resetAuthorization();
    }

    @Test
    public void shouldAuthorizeWithProperUid() {
        // given
        var uid = "test_user";
        TestAuthorizationConfig.mockAuthorization(uid);

        // when
        var whoami = given()
            .when().get("/secured")
            .then()
            .statusCode(200)
            .extract().body().as(SecuredResource.WhoamiResponse.class);

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
}
