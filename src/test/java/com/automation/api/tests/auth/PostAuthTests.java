package com.automation.api.tests.auth;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.AuthData;
import com.automation.api.utils.Endpoints;
import com.automation.api.utils.AuthTokenProvider;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Auth login endpoint coverage tests.
 */
@DisplayName("POST Auth API Tests")
public class PostAuthTests extends SetUp {

    /**
     * Verifies valid login returns a token.
     */
    @Test
    @DisplayName("POST /auth/login returns token for valid credentials")
    public void testLoginSuccessfully() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"username\": \"" + AuthData.VALID_USERNAME + "\",\n" +
                    "  \"password\": \"" + AuthData.VALID_PASSWORD + "\"\n" +
                    "}")
            .when()
            .post(Endpoints.AUTH_LOGIN)
            .then()
            .statusCode(AuthData.STATUS_OK)
            .body("token", notNullValue())
            .body(matchesJsonSchemaInClasspath(AuthData.AUTH_LOGIN_SCHEMA_PATH));
    }

    /**
     * Verifies invalid credentials are rejected by API contract.
     */
    @Test
    @DisplayName("POST /auth/login rejects invalid credentials")
    public void testLoginWithInvalidCredentials() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"username\": \"" + AuthData.INVALID_USERNAME + "\",\n" +
                "  \"password\": \"" + AuthData.INVALID_PASSWORD + "\"\n" +
                "}")
            .when()
            .post(Endpoints.AUTH_LOGIN)
            .then()
            .statusCode(AuthData.STATUS_UNAUTHORIZED);
    }

    /**
     * Verifies empty credentials are rejected by API contract.
     */
    @Test
    @DisplayName("POST /auth/login rejects empty credentials")
    public void testLoginWithEmptyCredentials() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"username\": \"" + AuthData.EMPTY_USERNAME + "\",\n" +
                "  \"password\": \"" + AuthData.EMPTY_PASSWORD + "\"\n" +
                "}")
            .when()
            .post(Endpoints.AUTH_LOGIN)
            .then()
            .statusCode(AuthData.STATUS_BAD_REQUEST);
    }

    /**
     * Verifies AuthTokenProvider utility returns a non-empty token for valid credentials.
     */
    @Test
    @DisplayName("AuthTokenProvider returns non-empty token")
    public void testAuthTokenProviderFetchToken() {
        String token = AuthTokenProvider.fetchToken(AuthData.VALID_USERNAME, AuthData.VALID_PASSWORD);
        assertFalse(token == null || token.isBlank(), "Expected non-empty token from AuthTokenProvider.");
    }
}
