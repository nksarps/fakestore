# Simple Docker image for running API tests, generating Allure report,
# and serving the report locally on port 8080.
FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Install Python for static report hosting.
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    && rm -rf /var/lib/apt/lists/*

# Copy project files.
COPY pom.xml ./
COPY src ./src

# Prefetch dependencies to speed up subsequent builds.
RUN mvn -B dependency:go-offline

EXPOSE 8080

CMD ["bash", "-c", "mvn -B -e test; \
echo ''; \
echo '================================================================'; \
echo 'Generating Allure HTML report...'; \
mvn -B allure:report; \
echo ''; \
echo 'Tests and report complete.'; \
echo 'Open: http://localhost:8080'; \
echo 'To stop, stop the container.'; \
echo '================================================================'; \
echo ''; \
python3 -m http.server 8080 --directory target/site/allure-maven-plugin"]
