package com.automation.api.tests.users;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.UsersData;
import com.automation.api.utils.Endpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * User GET endpoint coverage tests.
 */
@DisplayName("GET Users API Tests")
public class GetUsersTests extends SetUp {

    /**
     * Verifies users collection endpoint is reachable.
     */
    @Test
    @DisplayName("GET /users returns all users")
    public void testGetAllUsers() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.USERS)
            .then()
            .statusCode(UsersData.STATUS_OK)
            .header("Content-Type", containsString("application/json"))
            .body("size()", greaterThan(0));
    }

    /**
     * Verifies getting a user by valid ID returns a schema-compliant object.
     */
    @Test
    @DisplayName("GET /users/{id} returns user for valid id")
    public void testGetUserByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.userById(UsersData.VALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_OK)
            .header("Content-Type", containsString("application/json"))
            .body("id", equalTo(UsersData.VALID_USER_ID))
            .body(matchesJsonSchemaInClasspath(UsersData.USER_SCHEMA_PATH));
    }

    /**
     * Verifies non-existent user IDs are rejected by API contract.
     */
    @Test
    @DisplayName("GET /users/{id} returns not found for invalid id")
    public void testGetUserByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.userById(UsersData.INVALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_NOT_FOUND);
    }
}
