# Fake Store API Test Automation Framework

Comprehensive API test automation framework for https://fakestoreapi.com built with Java, JUnit 5, REST Assured, Hamcrest, and Allure Reporting.

This project validates endpoint behavior across product, cart, user, and auth resources using:
- Contract-first status code checks for positive and negative scenarios
- JSON schema validation for key response payloads
- Reusable request configuration and centralized endpoint routing
- Rich HTML reporting through Allure

## 1. Project Objectives

The framework is designed to provide:
- Reliable regression coverage for core Fake Store API endpoints
- Fast, readable API tests that are easy to maintain
- A clear structure for adding new endpoint test suites
- Transparent test results through structured Allure output

## 2. Technology Stack

- Java 17
- Maven
- JUnit Jupiter 5
- REST Assured 5.5.1
- Hamcrest 2.2
- Allure JUnit5 and Allure Maven plugins
- Docker and Docker Compose (optional report hosting flow)

Dependency and plugin versions are centralized in pom.xml.

## 3. Repository Structure Diagram

```
fakestore/
│
├─ pom.xml                          # Maven build configuration & dependencies
├─ Dockerfile                        # Container image for test execution
├─ docker-compose.yml                # One-command container orchestration
├─ README.md                         # Project documentation
├─ .dockerignore                     # Docker build exclusions
├─ .gitignore                        # Git version control exclusions
│
├─ .allure/                          # Allure installation (generated)
├─ .git/                             # Git repository metadata
│
├─ src/test/
│   ├─ java/com/automation/api/
│   │  │
│   │  ├─ base/
│   │  │  └─ SetUp.java              # Base test class + TestWatcher extension
│   │  │
│   │  ├─ config/
│   │  │  └─ ApiConfig.java          # API configuration (base URL, headers)
│   │  │
│   │  ├─ utils/
│   │  │  ├─ Endpoints.java          # Centralized endpoint routes
│   │  │  └─ AuthTokenProvider.java  # Token fetching utility
│   │  │
│   │  ├─ testdata/
│   │  │  ├─ AuthData.java           # Auth test constants
│   │  │  ├─ ProductsData.java       # Product test constants
│   │  │  ├─ CartsData.java          # Cart test constants
│   │  │  └─ UsersData.java          # User test constants
│   │  │
│   │  └─ tests/                     # Test suites grouped by domain
│   │     ├─ auth/
│   │     │  └─ PostAuthTests.java
│   │     ├─ products/
│   │     │  ├─ GetProductsTests.java
│   │     │  ├─ PostProductsTests.java
│   │     │  ├─ PutProductsTests.java
│   │     │  └─ DeleteProductsTests.java
│   │     ├─ carts/
│   │     │  ├─ GetCartsTests.java
│   │     │  ├─ PostCartsTests.java
│   │     │  ├─ PutCartsTests.java
│   │     │  └─ DeleteCartsTests.java
│   │     └─ users/
│   │        ├─ GetUsersTests.java
│   │        ├─ PostUsersTests.java
│   │        ├─ PutUsersTests.java
│   │        └─ DeleteUsersTests.java
│   │
│   └─ resources/schemas/            # JSON schema validation contracts
│      ├─ auth-login-response-schema.json
│      ├─ product-schema.json
│      ├─ product-create-response-schema.json
│      ├─ cart-schema.json
│      ├─ cart-create-response-schema.json
│      ├─ user-schema.json
│      └─ user-create-response-schema.json
│
├─ allure-results/                   # Raw Allure result files (generated after test run)
│
└─ target/                           # Maven build output (generated)
   ├─ allure-results/                # Test results for Allure
   └─ site/allure-maven-plugin/      # Generated HTML report (after mvn allure:report)
```

## 4. Test Design and Architecture

### 4.1 Base Test Setup

The SetUp base class provides:
- Per-test initialization of REST Assured base URI and shared request specification
- Standardized headers and content type across tests
- Per-test cleanup with RestAssured.reset()
- JUnit 5 TestWatcher extension that logs:
	- PASS
	- FAIL (with cause)
	- SKIPPED / ABORTED (with reason)

### 4.2 Configuration Strategy

ApiConfig centralizes common request settings:
- BASE_URL: https://fakestoreapi.com
- Content-Type: application/json
- Accept: application/json
- Charset: UTF-8

Credential handling:
- Optional environment variables are supported for username/password retrieval:
	- FAKESTORE_USERNAME
	- FAKESTORE_PASSWORD
- Most auth tests use static test data constants; token utility methods support either explicit credentials or env-based credentials.

### 4.3 Endpoint Management

Endpoints are centralized to avoid hardcoded strings in tests:
- Base routes: /products, /carts, /users, /auth/login
- Builder helpers for:
	- Resource-by-id routes
	- Query-driven routes (limit/sort/date range)
	- Category routes

### 4.4 Data and Assertion Conventions

- Test data classes keep payload values and expected statuses in one place.
- JSON bodies are built explicitly in tests for readability and control.
- Negative tests assert expected contract behavior directly (for example 400, 401, 404), rather than relaxing expectations.
- Selected endpoints include JSON schema assertions to validate response contract shape.

## 5. Endpoint Coverage Matrix

### Auth

POST /auth/login:
- Valid credentials returns token (200 + schema check)
- Invalid credentials rejected (401)
- Empty credentials rejected (400)
- AuthTokenProvider utility path validated (non-empty token for valid credentials)

### Products

GET /products:
- Retrieve all products (200, JSON content type)

GET /products/{id}:
- Valid ID returns resource (200 + schema)
- Invalid ID returns not found (404)

GET /products?limit=n:
- Result size bounded by limit

GET /products?sort=desc:
- IDs returned in descending order

GET /products/categories:
- Category list is non-empty and values are non-blank

POST /products:
- Valid payload creates product (201 + create-schema)
- Empty payload rejected (400)
- Missing fields rejected (400)

PUT /products/{id}:
- Valid ID updates product (200 + field assertions + schema)
- Invalid ID rejected (404)

DELETE /products/{id}:
- Valid ID accepted (200)
- Invalid ID rejected (404)

### Carts

GET /carts:
- Retrieve all carts (200)

GET /carts/{id}:
- Valid ID returns cart (200 + schema)
- Invalid ID rejected (404)

POST /carts:
- Valid payload creates cart (201 + create-schema)
- Missing products array rejected (400)
- Empty payload rejected (400)

PUT /carts/{id}:
- Valid ID updates cart (200 + key field assertions)
- Invalid ID rejected (404)

DELETE /carts/{id}:
- Valid ID accepted (200)
- Invalid ID rejected (404)

### Users

GET /users:
- Retrieve all users (200, JSON content type)

GET /users/{id}:
- Valid ID returns user (200 + schema)
- Invalid ID rejected (404)

POST /users:
- Valid payload creates user (201 + create-schema)
- Empty payload rejected (400)
- Invalid email format rejected (400)

PUT /users/{id}:
- Valid ID updates user fields (200)
- Invalid ID rejected (404)

DELETE /users/{id}:
- Valid ID accepted (200)
- Invalid ID rejected (404)

Total test methods: 37

## 6. Prerequisites

Install:
- JDK 17+
- Maven 3.8+
- Docker Desktop (only if using containerized run/report flow)

Verify locally:

		java -version
		mvn -version
		docker --version
		docker compose version

## 7. How To Run Tests

### 7.1 Run Entire Test Suite

		mvn clean test

### 7.2 Run By Domain

Products tests:

		mvn -Dtest=com.automation.api.tests.products.* test

Carts tests:

		mvn -Dtest=com.automation.api.tests.carts.* test

Users tests:

		mvn -Dtest=com.automation.api.tests.users.* test

Auth tests:

		mvn -Dtest=com.automation.api.tests.auth.* test

### 7.3 Run Specific Test Class

Example:

		mvn -Dtest=com.automation.api.tests.products.GetProductsTests test

## 8. Allure Reporting

### 8.1 Generate and Open Report Locally

Run tests first:

		mvn clean test

Generate report:

		mvn allure:report

Open static report files from:

- target/site/allure-maven-plugin/index.html

Or serve interactively:

		mvn allure:serve

### 8.2 Raw Result Artifacts

Surefire is configured to write Allure result files into:

- target/allure-results

## 9. Dockerized Test + Report Hosting Flow

The container flow:
1. Builds from Maven + Temurin 17 base image
2. Runs full test suite
3. Generates Allure HTML report
4. Serves report via Python HTTP server on port 8080

Start containerized flow:

		docker compose up --build

Open report:

- http://localhost:8080

Important behavior:
- Report hosting continues even when tests fail, so failures remain inspectable in the generated report.

## 10. Configuration and Credentials

Base URL:
- Fixed in ApiConfig as Fake Store API URL

Optional credential environment variables:
- FAKESTORE_USERNAME
- FAKESTORE_PASSWORD

PowerShell example:

		$env:FAKESTORE_USERNAME = "your_username"
		$env:FAKESTORE_PASSWORD = "your_password"

Command Prompt example:

		set FAKESTORE_USERNAME=your_username
		set FAKESTORE_PASSWORD=your_password

## 11. Troubleshooting Guide

If tests fail with connectivity errors:
- Verify internet access and Fake Store API availability
- Confirm no local proxy/firewall is blocking outbound requests

If schema validation fails:
- Compare response payload with schema files under src/test/resources/schemas
- Validate that endpoint response contract has not changed upstream

If report directory is empty:
- Ensure tests were executed before running allure:report
- Check for build interruption before surefire completed

If Docker report page is unavailable:
- Ensure port 8080 is not in use
- Rebuild image with docker compose up --build

## 12. Extending The Framework

Recommended approach for new endpoints:
1. Add constants and expected statuses in the relevant test data class
2. Add route builders in Endpoints if a new route pattern is introduced
3. Create a dedicated test class under the proper domain package
4. Add JSON schema files for new response contracts when applicable
5. Keep positive and negative tests contract-first with explicit expected status assertions

## 13. CI/CD Integration Notes

Typical CI pipeline steps:
1. Checkout repository
2. Setup JDK 17
3. Run mvn clean test
4. Run mvn allure:report
5. Publish target/site/allure-maven-plugin as build artifact

This enables historical report inspection and easier debugging of pipeline failures.

## 14. Current Scope Summary

- API domains covered: Auth, Products, Carts, Users
- Test classes: 13
- Test methods: 37
- Schema files: 7
- Execution modes: Local Maven and Docker Compose hosted report
