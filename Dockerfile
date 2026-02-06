# syntax=docker/dockerfile:1

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

#RUN apk add --no-cache bash=5.2.21-r0
RUN apk add --no-cache bash

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

RUN chmod +x mvnw

# Download dependencies (cached if pom.xml unchanged)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B

# Stage 2
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# pin version here too
#RUN apk add --no-cache wget=1.21.4-r0

# hadolint ignore=DL3018
RUN apk add --no-cache wget

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

COPY scripts/runner.sh /app/runner.sh
RUN chmod +x /app/runner.sh  

USER appuser

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
