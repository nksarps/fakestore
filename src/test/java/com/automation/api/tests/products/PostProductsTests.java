package com.automation.api.tests.products;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.ProductsData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Product POST endpoint coverage tests.
 */
@DisplayName("POST Products API Tests")
public class PostProductsTests extends SetUp {

    /**
     * Verifies product creation endpoint accepts a valid payload.
     */
    @Test
    @DisplayName("POST /products creates product with valid payload")
    public void testCreateProduct() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"title\": \"" + ProductsData.VALID_TITLE + "\",\n" +
                    "  \"price\": " + ProductsData.VALID_PRICE + ",\n" +
                    "  \"description\": \"" + ProductsData.VALID_DESCRIPTION + "\",\n" +
                    "  \"image\": \"" + ProductsData.VALID_IMAGE + "\",\n" +
                    "  \"category\": \"" + ProductsData.VALID_CATEGORY + "\"\n" +
                    "}")
            .when()
            .post(Endpoints.PRODUCTS)
            .then()
            .statusCode(ProductsData.STATUS_CREATED)
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath(ProductsData.PRODUCT_CREATE_SCHEMA_PATH));
    }

    /**
     * Verifies empty payload is rejected by API contract.
     */
    @Test
    @DisplayName("POST /products rejects empty payload")
    public void testCreateProductWithEmptyPayload() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post(Endpoints.PRODUCTS)
            .then()
            .statusCode(ProductsData.STATUS_BAD_REQUEST);
    }

    /**
     * Verifies payload with missing required fields is rejected by API contract.
     */
    @Test
    @DisplayName("POST /products rejects payload with missing fields")
    public void testCreateProductWithMissingFields() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"title\": \"" + ProductsData.VALID_TITLE + "\"\n" +
                    "}")
            .when()
            .post(Endpoints.PRODUCTS)
            .then()
            .statusCode(ProductsData.STATUS_BAD_REQUEST);
    }
}
