package com.automation.api.utils;

import com.automation.api.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Utility for requesting and extracting authentication tokens.
 */
public final class AuthTokenProvider {
    /**
     * Requests a token using credentials from {@link ApiConfig}.
     *
     * @return authentication token string
     */
    public static String fetchTokenFromConfigCredentials() {
        return fetchToken(ApiConfig.getUsername(), ApiConfig.getPassword());
    }

    /**
     * Requests a token from the auth endpoint using provided credentials.
     *
     * @param username login username
     * @param password login password
     * @return authentication token string
     */
    public static String fetchToken(String username, String password) {
        validateCredentials(username, password);

        RestAssured.baseURI = ApiConfig.getBaseUrl();

        Response response = given()
                .contentType(ApiConfig.getContentType())
                .accept(ApiConfig.getAcceptHeader())
                .body("{\n" +
                        "  \"username\": \"" + username + "\",\n" +
                        "  \"password\": \"" + password + "\"\n" +
                        "}")
                .when()
                .post(Endpoints.AUTH_LOGIN)
                .then()
                .extract()
                .response();

        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Auth request failed with status code: " + response.statusCode());
        }

        return extractTokenFromResponseBody(response.asString());
    }

    /**
     * Extracts token value from auth response body.
     *
     * @param responseBody raw JSON response body
     * @return authentication token string
     */
    public static String extractTokenFromResponseBody(String responseBody) {
        String token = JsonPath.from(responseBody).getString("token");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Token was not found in auth response body.");
        }
        return token;
    }

    private static void validateCredentials(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password must be provided to request an auth token.");
        }
    }
}
