package com.automation.api.tests.users;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.UsersData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

/**
 * User POST endpoint coverage tests.
 */
@DisplayName("POST Users API Tests")
public class PostUsersTests extends SetUp {

    /**
     * Verifies user creation endpoint accepts a valid payload.
     */
    @Test
    @DisplayName("POST /users creates user with valid payload")
    public void testCreateUser() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"username\": \"" + UsersData.VALID_USERNAME + "\",\n" +
                    "  \"email\": \"" + UsersData.VALID_EMAIL + "\",\n" +
                    "  \"password\": \"" + UsersData.VALID_PASSWORD + "\"\n" +
                    "}")
            .when()
            .post(Endpoints.USERS)
            .then()
            .statusCode(UsersData.STATUS_CREATED)
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath(UsersData.USER_CREATE_SCHEMA_PATH));
    }

    /**
     * Verifies endpoint behavior for empty payload.
     */
    @Test
    @DisplayName("POST /users rejects empty payload")
    public void testCreateUserWithEmptyPayload() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body(UsersData.EMPTY_JSON_PAYLOAD)
            .when()
            .post(Endpoints.USERS)
            .then()
            .statusCode(UsersData.STATUS_BAD_REQUEST);
    }

    /**
     * Verifies endpoint behavior for invalid email format payload.
     */
    @Test
    @DisplayName("POST /users rejects invalid email format")
    public void testCreateUserWithInvalidEmailFormat() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"username\": \"" + UsersData.VALID_USERNAME + "\",\n" +
                    "  \"email\": \"" + UsersData.INVALID_EMAIL_FORMAT + "\",\n" +
                    "  \"password\": \"" + UsersData.VALID_PASSWORD + "\"\n" +
                    "}")
            .when()
            .post(Endpoints.USERS)
            .then()
            .statusCode(UsersData.STATUS_BAD_REQUEST);
    }
}
