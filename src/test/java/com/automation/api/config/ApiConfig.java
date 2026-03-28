package com.automation.api.config;

/**
 * Centralized API configuration values used by test setup and utilities.
 */
public final class ApiConfig {

    private static final String BASE_URL = "https://fakestoreapi.com";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ACCEPT_HEADER = "application/json";
    private static final String CHARSET = "UTF-8";

    /**
     * @return base URL for the Fake Store API.
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * @return default request content type.
     */
    public static String getContentType() {
        return CONTENT_TYPE;
    }

    /**
     * @return default accept header value.
     */
    public static String getAcceptHeader() {
        return ACCEPT_HEADER;
    }

    /**
     * @return default charset used for request content type.
     */
    public static String getCharset() {
        return CHARSET;
    }

    /**
     * @return optional username from environment variable FAKESTORE_USERNAME.
     */
    public static String getUsername() {
        return System.getenv("FAKESTORE_USERNAME");
    }

    /**
     * @return optional password from environment variable FAKESTORE_PASSWORD.
     */
    public static String getPassword() {
        return System.getenv("FAKESTORE_PASSWORD");
    }
}
