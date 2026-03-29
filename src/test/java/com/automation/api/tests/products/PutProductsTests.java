package com.automation.api.tests.products;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.ProductsData;
import com.automation.api.utils.Endpoints;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

/**
 * Product PUT endpoint coverage tests.
 */
@DisplayName("PUT Products API Tests")
public class PutProductsTests extends SetUp {

    /**
     * Verifies product update endpoint is reachable.
     */
    @Test
    @DisplayName("PUT /products/{id} updates product for valid id")
    public void testUpdateProductByValidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"title\": \"" + ProductsData.UPDATED_TITLE + "\",\n" +
                    "  \"price\": " + ProductsData.UPDATED_PRICE + ",\n" +
                    "  \"description\": \"" + ProductsData.VALID_DESCRIPTION + "\",\n" +
                    "  \"image\": \"" + ProductsData.VALID_IMAGE + "\",\n" +
                    "  \"category\": \"" + ProductsData.VALID_CATEGORY + "\"\n" +
                    "}")
            .when()
            .put(Endpoints.productById(ProductsData.VALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .body("id", equalTo(ProductsData.VALID_PRODUCT_ID))
            .body("title", equalTo(ProductsData.UPDATED_TITLE))
            .body("price", equalTo((float) ProductsData.UPDATED_PRICE))
            .body(matchesJsonSchemaInClasspath(ProductsData.PRODUCT_SCHEMA_PATH));
    }

    /**
     * Verifies updates against non-existent product ID are rejected by API contract.
     */
    @Test
    @DisplayName("PUT /products/{id} returns not found for invalid id")
    public void testUpdateProductByInvalidId() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "  \"title\": \"" + ProductsData.UPDATED_TITLE + "\",\n" +
                    "  \"price\": " + ProductsData.UPDATED_PRICE + ",\n" +
                    "  \"description\": \"" + ProductsData.VALID_DESCRIPTION + "\",\n" +
                    "  \"image\": \"" + ProductsData.VALID_IMAGE + "\",\n" +
                    "  \"category\": \"" + ProductsData.VALID_CATEGORY + "\"\n" +
                    "}")
            .when()
            .put(Endpoints.productById(ProductsData.INVALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_NOT_FOUND);
    }
}
