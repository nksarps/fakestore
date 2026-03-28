package com.automation.api.tests.products;

import com.automation.api.base.SetUp;
import com.automation.api.testdata.ProductsData;
import com.automation.api.utils.Endpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Product DELETE endpoint coverage tests.
 */
@DisplayName("DELETE Products API Tests")
public class DeleteProductsTests extends SetUp {

    /**
     * Verifies product delete endpoint is reachable.
     */
    @Test
    @DisplayName("DELETE /products/{id} deletes product for valid id")
    public void testDeleteProductByValidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.productById(ProductsData.VALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_OK);
    }

    /**
     * Verifies endpoint behavior when deleting a non-existent product ID.
     */
    @Test
    @DisplayName("DELETE /products/{id} returns not found for invalid id")
    public void testDeleteProductByInvalidId() {
        given()
            .spec(requestSpec)
            .when()
            .delete(Endpoints.productById(ProductsData.INVALID_PRODUCT_ID))
            .then()
            .statusCode(ProductsData.STATUS_NOT_FOUND);
    }
}
