
# Build stage

FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/

COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw --batch-mode dependency:go-offline

COPY src/ src/

RUN ./mvnw --batch-mode clean package -DskipTests


# Runtime stage

FROM eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root application user

RUN groupadd --system appgroup \
    && useradd --system \
    --gid appgroup \
    --home-dir /app \
    --shell /usr/sbin/nologin \
    appuser

# Copy the packaged Spring Boot application

COPY --from=build --chown=appuser:appgroup \
    /app/target/*.jar /app/application.jar

RUN chown appuser:appgroup /app

# Run the application without root privileges

USER appuser

# Application network configuration

EXPOSE 8080

# JVM memory configuration

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Start the Spring Boot application

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
