package com.automation.api.tests.carts;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.CartsData;
import com.automation.api.utils.Endpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;

/**
 * Cart GET endpoint coverage tests.
 */
@DisplayName("GET Carts API Tests")
public class GetCartsTests extends SetUp {

    /**
     * Verifies carts collection endpoint is reachable.
     */
    @Test
    @DisplayName("GET /carts returns all carts")
    public void testGetAllCarts() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.CARTS)
            .then()
            .statusCode(CartsData.STATUS_OK)
            .body("size()", greaterThan(0));
    }

    /**
     * Verifies getting a cart by valid ID returns a schema-compliant object.
     */
    @Test
    @DisplayName("GET /carts/{id} returns cart for valid id")
    public void testGetCartByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.cartById(CartsData.VALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_OK)
            .body("id", equalTo(CartsData.VALID_CART_ID))
            .body(matchesJsonSchemaInClasspath(CartsData.CART_SCHEMA_PATH));
    }

    /**
     * Verifies non-existent cart IDs are rejected by API contract.
     */
    @Test
    @DisplayName("GET /carts/{id} returns not found for invalid id")
    public void testGetCartByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.cartById(CartsData.INVALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_NOT_FOUND);
    }
}
