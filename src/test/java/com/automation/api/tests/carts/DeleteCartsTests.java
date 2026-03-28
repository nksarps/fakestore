package com.automation.api.tests.carts;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.CartsData;
import com.automation.api.utils.Endpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Cart DELETE endpoint coverage tests.
 */
@DisplayName("DELETE Carts API Tests")
public class DeleteCartsTests extends SetUp {

    /**
     * Verifies cart delete endpoint is reachable.
     */
    @Test
    @DisplayName("DELETE /carts/{id} deletes cart for valid id")
    public void testDeleteCartByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.cartById(CartsData.VALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_OK);
    }

    /**
     * Verifies deletes against non-existent cart ID are rejected by API contract.
     */
    @Test
    @DisplayName("DELETE /carts/{id} returns not found for invalid id")
    public void testDeleteCartByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.cartById(CartsData.INVALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_NOT_FOUND);
    }
}
