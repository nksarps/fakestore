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
- Docker and Docker Compose (local report hosting and CI builds)
- Jenkins (CI/CD pipeline with Slack and email notifications)

Dependency and plugin versions are centralized in pom.xml.

## 3. Repository Structure Diagram

```
fakestore/
│
├─ pom.xml                          # Maven build configuration & dependencies
├─ Dockerfile                        # Multi-stage container image (ci + serve stages)
├─ docker-compose.yml                # One-command container orchestration (serve stage)
├─ Jenkinsfile                       # Declarative Jenkins pipeline definition
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

The Dockerfile uses a multi-stage build with three stages:

- `base` — shared foundation, prefetches Maven dependencies and copies source. Used by both stages below.
- `ci` — used by Jenkins. Runs tests and generates the Allure report, then exits. Build fails if tests fail.
- `serve` — used by docker compose. Runs tests, generates the Allure report, then hosts it via Python on port 8080.

Start local report hosting flow:

		docker compose up --build

Open report:

- http://localhost:8080

Important behavior:
- The `serve` stage hosts the report even when tests fail so results remain inspectable.
- The `ci` stage exits with a non-zero code on test failure so Jenkins marks the build correctly.

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

If Jenkins pipeline fails at the Docker build step:
- Ensure the Jenkins container has Docker socket access (-v /var/run/docker.sock:/var/run/docker.sock)
- Ensure Docker CLI is installed inside the Jenkins container
- Check that the jenkins user is in the docker group

## 12. Extending The Framework

Recommended approach for new endpoints:
1. Add constants and expected statuses in the relevant test data class
2. Add route builders in Endpoints if a new route pattern is introduced
3. Create a dedicated test class under the proper domain package
4. Add JSON schema files for new response contracts when applicable
5. Keep positive and negative tests contract-first with explicit expected status assertions

## 13. Jenkins CI/CD Pipeline

### 13.1 How the Pipeline Works

The pipeline uses the `ci` stage of the multi-stage `Dockerfile` to run tests inside Docker:

1. Jenkins checks out the code from the configured Git branches (main, develop, feature/*)
2. Docker builds the `ci` stage — runs `mvn clean test` and `mvn allure:report` inside the image
3. A temporary container is created to copy out test artifacts (surefire XML, Allure results, Allure HTML)
4. JUnit plugin publishes surefire XML results
5. HTML Publisher plugin publishes the Allure HTML report
6. Slack and email notifications fire on success or failure
7. The CI image is cleaned up after each build

### 13.2 Jenkins Job Setup

1. Create a new Pipeline job in Jenkins
2. Under General, check `GitHub project` and enter your repo URL
3. Under Build Triggers, check `GitHub hook trigger for GITScm polling`
4. Under Pipeline, set Definition to `Pipeline script from SCM`
5. Set SCM to Git and provide your repository URL
6. Add the following Branch Specifiers (click `Add Branch` for each):
   - `*/main`
   - `*/develop`
   - `*/feature/*`
7. Set Script Path to `Jenkinsfile`
8. Save

### 13.3 Webhook Setup (GitHub)

To trigger the pipeline automatically on push:

1. In Jenkins job, check `GitHub hook trigger for GITScm polling` under Build Triggers
2. In your GitHub repository go to Settings > Webhooks > Add webhook
3. Set Payload URL to: `http://<your-jenkins-host>:8080/github-webhook/`
4. Set Content type to `application/json`
5. Select `Just the push event`
6. Save

For local Jenkins exposed via ngrok:

		ngrok http 8080

Use the generated ngrok URL as the webhook payload URL.

### 13.4 Slack Notifications Setup

1. Go to api.slack.com/apps and create a new app from scratch
2. Under OAuth & Permissions add the `chat:write.public` bot scope
3. Install the app to your workspace and copy the `xoxb-...` Bot User OAuth Token
4. In Jenkins > Manage Jenkins > Credentials add a Secret Text credential with the token, ID: `slack-bot-token`
5. Install the Slack Notification plugin in Jenkins
6. In Jenkins > Manage Jenkins > System, configure the Slack section:
   - Workspace: your Slack workspace subdomain
   - Credential: select `slack-bot-token`
   - Default channel: `#ci-notifications`
7. Click `Test Connection` to verify
8. Update `SLACK_CHANNEL` in the Jenkinsfile if using a different channel

### 13.5 Email Notifications Setup

1. In Jenkins > Manage Jenkins > System, configure the `Extended E-mail Notification` section:
   - SMTP server: `smtp.gmail.com`
   - SMTP port: `587` with TLS, or `465` with SSL
   - Add a Username/Password credential with your Gmail address and a Gmail App Password
2. Generate a Gmail App Password at myaccount.google.com/apppasswords
3. Update the `to` address in the Jenkinsfile `mail()` step with your real email

### 13.7 Jenkins Container Requirements

The Jenkins container must be started with Docker socket access so the pipeline can run `docker build`:

		docker run -d \
		  --name jenkins \
		  -p 8080:8080 \
		  -p 50000:50000 \
		  -v jenkins_home:/var/jenkins_home \
		  -v /var/run/docker.sock:/var/run/docker.sock \
		  jenkins/jenkins:lts

Docker CLI must also be installed inside the container:

		docker exec -u root jenkins bash -c "apt-get update && apt-get install -y docker.io && usermod -aG docker jenkins"
		docker restart jenkins

### 13.8 Required Jenkins Plugins

- Git
- Pipeline
- HTML Publisher
- JUnit
- Slack Notification
- Email Extension (or Mailer)

## 14. Current Scope Summary

- API domains covered: Auth, Products, Carts, Users
- Test classes: 13
- Test methods: 37
- Schema files: 7
- Execution modes: Local Maven, Docker Compose hosted report, Jenkins CI pipeline
