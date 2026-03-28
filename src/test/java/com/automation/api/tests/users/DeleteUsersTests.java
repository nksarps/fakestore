package com.automation.api.tests.users;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.UsersData;
import com.automation.api.utils.Endpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * User DELETE endpoint coverage tests.
 */
@DisplayName("DELETE Users API Tests")
public class DeleteUsersTests extends SetUp {

    /**
     * Verifies user delete endpoint is reachable.
     */
    @Test
    @DisplayName("DELETE /users/{id} deletes user for valid id")
    public void testDeleteUserByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.userById(UsersData.VALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_OK);
    }

    /**
     * Verifies deletes against non-existent user ID are rejected by API contract.
     */
    @Test
    @DisplayName("DELETE /users/{id} returns not found for invalid id")
    public void testDeleteUserByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.userById(UsersData.INVALID_USER_ID))
            .then()
            .statusCode(UsersData.STATUS_NOT_FOUND);
    }
}
