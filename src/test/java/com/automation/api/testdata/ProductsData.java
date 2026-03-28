package com.automation.api.testdata;

/**
 * Product-specific test constants used across product API tests.
 */
public final class ProductsData {

    public static final int VALID_PRODUCT_ID = 1;
    public static final int INVALID_PRODUCT_ID = 9999;

    public static final String VALID_TITLE = "Automation Product";
    public static final double VALID_PRICE = 29.99;
    public static final String VALID_DESCRIPTION = "A product created by API tests";
    public static final String VALID_IMAGE = "https://i.pravatar.cc";
    public static final String VALID_CATEGORY = "electronics";

    public static final String UPDATED_TITLE = "Updated Automation Product";
    public static final double UPDATED_PRICE = 35.50;

    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOT_FOUND = 404;

    public static final int LIMIT = 10;


    public static final String PRODUCT_SCHEMA_PATH = "schemas/product-schema.json";
    public static final String PRODUCT_CREATE_SCHEMA_PATH = "schemas/product-create-response-schema.json";

    private ProductsData() {
        // Constants holder
    }
}
