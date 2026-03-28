package com.automation.api.tests.carts;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.CartsData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Cart POST endpoint coverage tests.
 */
@DisplayName("POST Carts API Tests")
public class PostCartsTests extends SetUp {

    /**
     * Verifies cart creation endpoint accepts a valid payload.
     */
    @Test
    @DisplayName("POST /carts creates cart with valid payload")
    public void testCreateCart() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"userId\": " + CartsData.VALID_USER_ID + ",\n" +
                    "  \"date\": \"" + CartsData.VALID_CART_DATE + "\",\n" +
                    "  \"products\": [\n" +
                    "    {\n" +
                    "      \"productId\": " + CartsData.VALID_PRODUCT_ID + ",\n" +
                    "      \"quantity\": " + CartsData.VALID_QUANTITY + "\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}")
            .when()
            .post(Endpoints.CARTS)
            .then()
            .statusCode(CartsData.STATUS_CREATED)
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath(CartsData.CART_CREATE_SCHEMA_PATH));
    }

    /**
     * Verifies payload without products array is rejected by API contract.
     */
    @Test
    @DisplayName("POST /carts rejects payload with missing products array")
    public void testCreateCartWithMissingProductsArray() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"userId\": " + CartsData.VALID_USER_ID + ",\n" +
                "  \"date\": \"" + CartsData.VALID_CART_DATE + "\"\n" +
                "}")
            .when()
            .post(Endpoints.CARTS)
            .then()
            .statusCode(CartsData.STATUS_BAD_REQUEST);
    }

    /**
     * Verifies empty payload is rejected by API contract.
     */
    @Test
    @DisplayName("POST /carts rejects empty payload")
    public void testCreateCartWithEmptyPayload() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post(Endpoints.CARTS)
            .then()
            .statusCode(CartsData.STATUS_BAD_REQUEST);
    }
}
