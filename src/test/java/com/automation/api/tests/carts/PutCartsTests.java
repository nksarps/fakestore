package com.automation.api.tests.carts;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.CartsData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Cart PUT endpoint coverage tests.
 */
@DisplayName("PUT Carts API Tests")
public class PutCartsTests extends SetUp {

    /**
     * Verifies cart update endpoint is reachable.
     */
    @Test
    @DisplayName("PUT /carts/{id} updates cart for valid id")
    public void testUpdateCartByValidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"userId\": " + CartsData.VALID_USER_ID + ",\n" +
                    "  \"date\": \"" + CartsData.VALID_CART_DATE + "\",\n" +
                    "  \"products\": [\n" +
                    "    {\n" +
                    "      \"productId\": " + CartsData.VALID_PRODUCT_ID + ",\n" +
                    "      \"quantity\": " + CartsData.UPDATED_QUANTITY + "\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}")
            .when()
            .put(Endpoints.cartById(CartsData.VALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_OK)
            .body("id", equalTo(CartsData.VALID_CART_ID))
            .body("userId", equalTo(CartsData.VALID_USER_ID));
    }

    /**
     * Verifies updates against non-existent cart ID are rejected by API contract.
     */
    @Test
    @DisplayName("PUT /carts/{id} returns not found for invalid id")
    public void testUpdateCartByInvalidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"userId\": " + CartsData.VALID_USER_ID + ",\n" +
                "  \"date\": \"" + CartsData.VALID_CART_DATE + "\",\n" +
                "  \"products\": [\n" +
                "    {\n" +
                "      \"productId\": " + CartsData.VALID_PRODUCT_ID + ",\n" +
                "      \"quantity\": " + CartsData.UPDATED_QUANTITY + "\n" +
                "    }\n" +
                "  ]\n" +
                "}")
            .when()
            .put(Endpoints.cartById(CartsData.INVALID_CART_ID))
            .then()
            .statusCode(CartsData.STATUS_NOT_FOUND);
    }
}
