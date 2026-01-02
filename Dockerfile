#Stage 1: Build the application
FROM openjdk:27-ea-slim-trixie as build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY ./src ./src
RUN ./mvnw clean install

# Stage 2: Run the application
FROM eclipse-temurin:21.0.8_9-jre-jammy AS final
WORKDIR /app
EXPOSE 9090
COPY --from=builder /app/target/*.jar /app/*.jar
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
