package com.automation.api.tests.users;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.UsersData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * User PUT endpoint coverage tests.
 */
@DisplayName("PUT Users API Tests")
public class PutUsersTests extends SetUp {

    /**
     * Verifies user update endpoint is reachable.
     */
    @Test
    @DisplayName("PUT /users/{id} updates user for valid id")
    public void testUpdateUserByValidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"username\": \"" + UsersData.UPDATED_USERNAME + "\",\n" +
                    "  \"email\": \"" + UsersData.UPDATED_EMAIL + "\",\n" +
                    "  \"password\": \"" + UsersData.UPDATED_PASSWORD + "\"\n" +
                    "}")
            .when()
            .put(Endpoints.userById(UsersData.VALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_OK)
            .body("username", equalTo(UsersData.UPDATED_USERNAME))
            .body("email", equalTo(UsersData.UPDATED_EMAIL))
                .body("password", equalTo(UsersData.UPDATED_PASSWORD));
    }

    /**
     * Verifies updates against non-existent user ID are handled by the endpoint.
     */
    @Test
    @DisplayName("PUT /users/{id} handles invalid id")
    public void testUpdateUserByInvalidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"username\": \"" + UsersData.UPDATED_USERNAME + "\",\n" +
                "  \"email\": \"" + UsersData.UPDATED_EMAIL + "\",\n" +
                "  \"password\": \"" + UsersData.UPDATED_PASSWORD + "\"\n" +
                "}")
            .when()
            .put(Endpoints.userById(UsersData.INVALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_NOT_FOUND);
    }
}
