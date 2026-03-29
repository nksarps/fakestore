# ===================================================================
# STAGE 1: base
# Shared foundation for all stages.
# Prefetches Maven dependencies and copies source code.
# Neither runs tests nor starts any server.
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS base

WORKDIR /app

# Prefetch dependencies using only pom.xml to leverage Docker layer cache.
# This layer is invalidated only when pom.xml changes.
# dependency:resolve is used over dependency:go-offline to avoid strict
# artifact resolution failures caused by transient network interruptions.
COPY pom.xml ./
RUN mvn -B dependency:resolve dependency:resolve-plugins -q || \
    mvn -B dependency:resolve dependency:resolve-plugins -q

# Copy source — this layer rebuilds only when source files change.
COPY src ./src

# ===================================================================
# STAGE 2: ci
# Jenkins CI target. Runs the full test suite and generates the
# Allure HTML report, then exits.
# Usage: docker build --target ci --tag fakestore-ci .
# The build exits with a non-zero code if tests fail, so Jenkins
# correctly marks the build as failed.
# ===================================================================
FROM base AS ci

# Run tests and generate report. Always exits 0 so Docker commits the image
# (and thus the reports). The real test exit code is written to /test-exit-code
# and read back by Jenkins to determine pass/fail.
RUN set +e; \
    mvn -B -e clean test; \
    TEST_EXIT=$?; \
    mvn -B allure:report; \
    echo $TEST_EXIT > /test-exit-code; \
    exit 0

# ===================================================================
# STAGE 3: serve
# Local report hosting target. Runs the full test suite, generates
# the Allure HTML report, then serves it via Python on port 8080.
# Usage: docker compose up --build
# The report is served even when tests fail so results remain
# inspectable at http://localhost:8080.
# ===================================================================
FROM base AS serve

RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

CMD ["bash", "-c", "set +e; \
  mvn -B -e clean test; \
  TEST_EXIT=$?; \
  echo ''; \
  echo '================================================================'; \
  echo 'Generating Allure HTML report...'; \
  mvn -B allure:report; \
  echo ''; \
  if [ $TEST_EXIT -eq 0 ]; then \
    echo 'Tests completed successfully.'; \
  else \
    echo 'Tests completed with failures. Report is still available.'; \
  fi; \
  echo 'Open: http://localhost:8080'; \
  echo '================================================================'; \
  echo ''; \
  python3 -m http.server 8080 --directory target/site/allure-maven-plugin"]
