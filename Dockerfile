# syntax=docker/dockerfile:1

### Build stage ###
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Leverage Docker layer caching for Gradle dependencies
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew \
    && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# Copy sources and build the application
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar

### Runtime stage ###
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
COPY --from=build /workspace/build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

# Default gRPC server port exposed by spring-boot-starter-grpc-server
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
