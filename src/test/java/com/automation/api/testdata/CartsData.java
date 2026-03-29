package com.automation.api.testdata;

/**
 * Cart-specific test constants used across cart API tests.
 */
public final class CartsData {
    public static final int VALID_CART_ID = 1;
    public static final int INVALID_CART_ID = 9999;
    public static final int VALID_USER_ID = 1;

    public static final String VALID_CART_DATE = "2020-02-03";

    public static final int VALID_PRODUCT_ID = 1;
    public static final int VALID_QUANTITY = 2;
    public static final int UPDATED_QUANTITY = 4;

    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOT_FOUND = 404;

    public static final String CART_SCHEMA_PATH = "schemas/cart-schema.json";
    public static final String CART_CREATE_SCHEMA_PATH = "schemas/cart-create-response-schema.json";
}
