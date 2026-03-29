package com.automation.api.testdata;

/**
 * Auth-specific test constants used across authentication tests.
 */
public final class AuthData {

    public static final String VALID_USERNAME = "mor_2314";
    public static final String VALID_PASSWORD = "83r5^_";

    public static final String INVALID_USERNAME = "invalid_user";
    public static final String INVALID_PASSWORD = "invalid_password";

    public static final String EMPTY_USERNAME = "";
    public static final String EMPTY_PASSWORD = "";

    public static final int STATUS_OK = 200;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_BAD_REQUEST = 400;

    public static final String AUTH_LOGIN_SCHEMA_PATH = "schemas/auth-login-response-schema.json";

    private AuthData() {
        // Constants holder
    }
}
