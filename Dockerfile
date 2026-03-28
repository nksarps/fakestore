FROM maven:3.9.6-eclipse-temurin-17

# Install only what is needed to host the generated Allure HTML report.
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy project files required for build and test execution.
COPY pom.xml ./
COPY src ./src

# Prefetch dependencies to speed up later container starts.
RUN mvn -B -q dependency:go-offline

EXPOSE 8080

# Run tests, generate Allure report, then host the HTML report on port 8080.
# The report is hosted even when tests fail so results remain inspectable.
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