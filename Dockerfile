# syntax=docker/dockerfile:1

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install bash (required by mvnw)
RUN apk add --no-cache bash

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom first (for better caching)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Make wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml unchanged)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B
    
# Copy source code
COPY src ./src

# Build the JAR with the same cache mount
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B

# Stage 2: Create minimal runtime image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Install wget (small tool for HTTP checks)
RUN apk add --no-cache wget

# Create non-root user for security (best practice)
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy only the built JAR from the builder stage
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

COPY scripts/runner.sh /app/runner.sh
RUN chmod +x /app/runner.sh  

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]