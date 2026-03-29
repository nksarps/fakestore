package com.automation.api.testdata;

/**
 * User-specific test constants used across user API tests.
 */
public final class UsersData {
    public static final int VALID_USER_ID = 1;
    public static final int INVALID_USER_ID = 9999;

    public static final String VALID_EMAIL = "automation.user@test.com";
    public static final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    public static final String EMPTY_JSON_PAYLOAD = "{}";
    public static final String VALID_USERNAME = "automation_user";
    public static final String VALID_PASSWORD = "pass123";

    public static final String UPDATED_EMAIL = "updated.user@test.com";
    public static final String UPDATED_USERNAME = "updated_automation_user";
    public static final String UPDATED_PASSWORD = "updated_pass123";

    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOT_FOUND = 404;

    public static final String USER_SCHEMA_PATH = "schemas/user-schema.json";
    public static final String USER_CREATE_SCHEMA_PATH = "schemas/user-create-response-schema.json";
}
