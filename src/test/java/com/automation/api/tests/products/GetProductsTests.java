package com.automation.api.tests.products;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.ProductsData;
import com.automation.api.utils.Endpoints;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;

/**
 * Product GET endpoint coverage tests.
 */
@DisplayName("GET Products API Tests")
public class GetProductsTests extends SetUp {

    /**
     * Verifies products collection endpoint is reachable.
     */
    @Test
    @DisplayName("GET /products returns all products with key headers")
    public void testGetAllProducts() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.PRODUCTS)
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .header("Content-Type", containsString("application/json"))
            .body("size()", greaterThan(0));
    }

    /**
     * Verifies getting a product by valid ID returns a schema-compliant object.
     */
    @Test
    @DisplayName("GET /products/{id} returns product for valid id")
    public void testGetProductByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.productById(ProductsData.VALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .header("Content-Type", containsString("application/json"))
            .body("id", equalTo(ProductsData.VALID_PRODUCT_ID))
            .body(matchesJsonSchemaInClasspath(ProductsData.PRODUCT_SCHEMA_PATH));
    }

    /**
     * Verifies invalid product IDs are handled without server failure.
     */
    @Test
    @DisplayName("GET /products/{id} handles invalid id")
    public void testGetProductByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.productById(ProductsData.INVALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_NOT_FOUND);
    }

    /**
     * Verifies the limit query parameter bounds response size.
     */
    @Test
    @DisplayName("GET /products with limit returns bounded list")
    public void testGetProductsWithLimit() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.productsWithLimit(ProductsData.LIMIT))
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .body("size()", lessThanOrEqualTo(ProductsData.LIMIT))
            .body("size()", greaterThan(0));
    }

    /**
     * Verifies sorting parameter returns products in descending id order.
     */
    @Test
    @DisplayName("GET /products with sort returns sorted list")
    public void testGetProductsWithSort() {
        Response response = given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.productsWithSort("desc"))
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .extract()
            .response();

        List<Integer> ids = response.jsonPath().getList("id", Integer.class);
        assertFalse(ids.isEmpty(), "Expected sorted products list to be non-empty.");
        assertTrue(ids.get(0) >= ids.get(ids.size() - 1), "Expected descending sort to place larger IDs first.");
    }

    /**
     * Verifies categories endpoint returns category names.
     */
    @Test
    @DisplayName("GET /products/categories returns categories")
    public void testGetCategories() {
        given()
            .spec(requestSpec)
            .when()
            .get(Endpoints.productCategories())
            .then()
            .statusCode(ProductsData.STATUS_OK)
            .body("size()", greaterThan(0))
            .body("", everyItem(not(emptyOrNullString())));
    }
}
