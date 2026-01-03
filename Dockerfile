# Fixed Single-stage Dockerfile for NextCart Spring Boot Application

FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache bash

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests -B

EXPOSE 9090

# This works for ANY JAR name
ENTRYPOINT ["sh", "-c", "exec java -jar /app/target/*.jar"]

# FROM eclipse-temurin:21-jdk-alpine

# # Install necessary tools (bash for mvnw script)
# RUN apk add --no-cache bash

# # Set working directory
# WORKDIR /app

# # Copy Maven wrapper files first
# COPY pom.xml .
# COPY .mvn/ .mvn/
# COPY mvnw .

# # Make the Maven wrapper executable (this fixes "Permission denied")
# RUN chmod +x mvnw

# # Download dependencies (cached if pom.xml unchanged)
# RUN ./mvnw dependency:go-offline -B

# # Copy source code
# COPY src ./src

# # Build the application (skip tests for faster build)
# RUN ./mvnw clean package -DskipTests -B

# # Optional: Create non-root user for better security
# RUN addgroup -g 1001 -S appgroup && \
#     adduser -u 1001 -S appuser -G appgroup

# USER appuser

# # Expose port
# EXPOSE 9090

# # Run the generated JAR
# ENTRYPOINT ["java", "-jar", "/app/target/*.jar"]

# Optional: If you want to make the JAR name dynamic (handles version changes)
# ENTRYPOINT ["sh", "-c", "java -jar /app/target/*.jar"]

# WORKDIR /app

# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./
# COPY ./src ./src
# RUN ./mvnw clean install

# # Stage 2: Run the application
# FROM eclipse-temurin:21.0.8_9-jre-jammy AS final
# WORKDIR /app
# EXPOSE 9090
# COPY --from=builder /app/target/*.jar /app/*.jar
# ENTRYPOINT ["java", "-jar", "/app/*.jar"]
