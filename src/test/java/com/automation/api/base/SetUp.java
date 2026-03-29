package com.automation.api.base;

import com.automation.api.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base test class for all REST Assured API tests.
 * Provides shared setup and teardown behavior.
 * Uses {@link TestWatcherExtension} to monitor test lifecycle events.
 */
@ExtendWith(SetUp.TestWatcherExtension.class)
public class SetUp {

    /**
     * Reusable request specification for all API requests.
     */
    protected RequestSpecification requestSpec;

    /**
     * Setup method that runs before each test.
     * Initializes REST Assured base URI and request specification.
     */
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = ApiConfig.getBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.getBaseUrl())
                .setContentType(ApiConfig.getContentType())
                .addHeader("Accept", ApiConfig.getAcceptHeader())
                .build();

        RestAssured.requestSpecification = requestSpec;
    }

    /**
     * Teardown method that runs after each test.
     * Resets REST Assured configuration to prevent test leakage.
     */
    @AfterEach
    public void tearDown() {
        RestAssured.reset();
    }

    /**
     * JUnit 5 extension that logs PASS/FAIL/SKIP test outcomes.
     */
    public static class TestWatcherExtension implements TestWatcher {

        private static final Logger LOGGER = Logger.getLogger(TestWatcherExtension.class.getName());
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * Called when a test completes successfully.
         *
         * @param context current extension context
         */
        @Override
        public void testSuccessful(ExtensionContext context) {
            String testDisplayName = context.getDisplayName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            LOGGER.log(Level.INFO, String.format("%s - [PASSED]: %s", timestamp, testDisplayName));
        }

        /**
         * Called when a test fails.
         *
         * @param context current extension context
         * @param cause root cause of failure
         */
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            String testDisplayName = context.getDisplayName();
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String errorMessage = cause != null ? cause.getMessage() : "Unknown error";

            LOGGER.log(Level.SEVERE, String.format("%s - [FAILED]: %s%n Cause: %s", timestamp, testDisplayName,
                    errorMessage));
        }

        /**
         * Called when a test is disabled.
         *
         * @param context current extension context
         * @param reason disabled reason if available
         */
        @Override
        public void testDisabled(ExtensionContext context, Optional<String> reason) {
            String testDisplayName = context.getDisplayName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            LOGGER.log(Level.INFO, String.format("%s - [SKIPPED]: %s%n Reason: %s", timestamp, testDisplayName,
                    reason.orElse("No message provided")));
        }

        /**
         * Called when a test is aborted.
         *
         * @param context current extension context
         * @param cause abort cause if present
         */
        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            String testDisplayName = context.getDisplayName();
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String message = cause != null ? cause.getMessage() : "No message provided";

            LOGGER.log(Level.INFO, String.format("%s - [SKIPPED]: %s%n Reason: %s", timestamp, testDisplayName,
                    message));
        }
    }
}
