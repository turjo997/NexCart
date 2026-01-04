# Docker Documentation – NexCart

This document describes all Docker-related configurations, commands, and best practices
used during the development and deployment of the **NexCart** application.

---

## Table of Contents

- [Chapter 1: Project Overview](#chapter-1-project-overview)
- [Chapter 2: How to Run (dev & prod)](#chapter-2-how-to-run-dev--prod)
- [Chapter 3: Dockerfile Design](#chapter-3-dockerfile-design)
- [Chapter 4: Image Build and Push](#chapter-4-image-build-and-push)
- [Chapter 5: Docker Hub Link](#chapter-5-docker-hub-link)
- [Chapter 6: Image Analysis (Layers and Size)](#chapter-6-image-analysis-layers-and-size)
- [Chapter 7: Docker Compose Setup](#chapter-7-docker-compose-setup)
- [Chapter 8: Security and Best Practices](#chapter-8-security-and-best-practices)
- [Chapter 9: Summary](#chapter-9-summary)

---


## Chapter 1: Project Overview

NexCart is containerized using Docker to provide a consistent, reproducible,
and production-ready runtime environment across development and deployment.

The Docker setup focuses on:
- Building a lightweight and secure Spring Boot image
- Separating build-time and runtime concerns using multi-stage builds
- Providing isolated services for the application and database
- Supporting both development and production workflows using Docker Compose profiles

The application is packaged as a single executable JAR and runs inside a
minimal Java runtime container. PostgreSQL runs as a separate container with
persistent storage managed via Docker volumes.

Docker Compose is used as the orchestration layer to:
- Define service boundaries (backend and database)
- Manage networking and service discovery
- Control startup order using health checks
- Apply resource limits and restart policies

This approach ensures:
- Environment parity between local development and production
- Faster onboarding with minimal setup
- Clear separation of concerns between application code, infrastructure, and data

## Chapter 2: How to run (dev & prod)

The NexCart application supports separate **development** and **production**
workflows using Docker Compose profiles.

### Development Mode

```bash
VERSION=latest docker compose --profile dev up -d
```
Access Application

- Application URL: http://localhost:9095/users

- Database: PostgreSQL running in a separate container

Purpose

- Live source updates without rebuilding the image

- Suitable for local development and testing

### Production Mode

```bash
VERSION=latest docker compose --profile prod up -d
``` 

Access Application

Application URL: http://localhost:9096/users

- Database: PostgreSQL running in a separate container with persistent storage

Purpose

- Uses the same image published to Docker Hub

- Applies CPU and memory limits

- Suitable for deployment and validation

## Chapter 3: Dockerfile Design

The application uses a **multi-stage Dockerfile** to clearly separate
the build process from the runtime environment.

### Build Stage
- Uses `eclipse-temurin:21-jdk-alpine`
- Contains Maven wrapper and source code
- Downloads dependencies using BuildKit cache mounts
- Compiles the application into a single executable JAR

This stage is optimized for faster rebuilds by copying `pom.xml` and
Maven wrapper files before the source code, allowing dependency layers
to be reused when application code changes.

### Runtime Stage
- Uses `eclipse-temurin:21-jre-alpine` as a minimal base image
- Copies only the generated JAR from the build stage
- Runs the application using a non-root user
- Exposes port `9090`

This design minimizes image size, reduces the attack surface, and
ensures that no build tools are present in the production image.

## Chapter 4: Image Build and Push

The Docker image is built locally using the multi-stage Dockerfile
and tagged according to its intended usage.

### Build Image
```bash
VERSION=latest docker compose --profile dev build
docker push ullash997/my-nexcart-web-app:latest
```

## Chapter 5: Docker Hub Link

The NexCart Docker image is published to Docker Hub and is publicly accessible.

**Repository URL : https://hub.docker.com/r/ullash997/my-nexcart-web-app**


The repository contains:
- Versioned image tags
- Pull instructions for end users
- A public image that can be deployed without authentication

This allows the application image to be easily shared, deployed, and
verified across different environments.

## Chapter 6: Image Analysis (Layers and Size)

The Docker image is analyzed to ensure it remains lightweight and efficient.

### Layer Analysis
```bash
docker history ullash997/my-nexcart-web-app:latest
```


| # | Instruction                                                                                      | Description | Layer Size |
|---|--------------------------------------------------------------------------------------------------|-------------|------------|
| 1 | ADD alpine-minirootfs-3.22.2-x86_64.tar.gz /                                                     | Alpine Linux minimal root filesystem (base image) | 3.63 MB |
| 2 | CMD ["/bin/sh"]                                                                                  | Default shell command | 0 B |
| 3 | ENV JAVA_HOME=/opt/java/openjdk                                                                  | Defines Java home directory | 0 B |
| 4 | ENV PATH=/opt/java/openjdk/bin:/usr/local/sbin:/usr/<br/>local/bin:/usr/sbin:/usr/bin:/sbin:/bin | Adds Java binaries to PATH | 0 B |
| 5 | ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8                                        | Locale configuration | 0 B |
| 6 | RUN /bin/sh -c set -eux;                                                                                     | Java installation and system setup (step 1) | 15.54 MB |
| 7 | ENV JAVA_VERSION=jdk-21.0.9+10                                                                   | Defines Java version | 0 B |
| 8 | RUN /bin/sh -c set -eux;                                                                                    | Java installation and cleanup (step 2) | 50.71 MB |
| 9 | RUN /bin/sh -c set -eux;                                                                                    | Certificate or minor system config | 128 B |
|10 | COPY --chmod=755 entrypoint.sh /__cacert_entrypoint.sh #                                                                               | Custom CA certificate entrypoint | 2.23 KB |
|11 | ENTRYPOINT ["/__cacert_entrypoint.sh"]                                                           | Entry point for CA handling | 0 B |
|12 | WORKDIR /app                                                                                     | Sets application working directory | 93 B |
|13 | RUN /bin/sh -c apk add                                                                                      | Installs required Alpine packages | 575.65 KB |
|14 | RUN /bin/sh -c addgroup -g                                                                                     | Creates non-root group | 954 B |
|15 | COPY --chown=appuser:appgroup /app/target/*.jar app.jar #                                                                                     | Copies Spring Boot application JAR | 49.53 MB |
|16 | COPY scripts/runner.sh /app/runner.sh # buildkit                                                                                   | Application startup script | 498 B |
|17 | RUN /bin/sh -c chmod +x                                                                                     | Makes runner script executable | 496 B |
|18 | USER appuser                                                                                     | Switches to non-root user | 0 B |
|19 | EXPOSE 9090                                                                                      | Documents application port | 0 B |
|20 | ENTRYPOINT ["java","-jar","app.jar"]                                                             | Application startup command | 0 B |


Observations:

- The final image contains only runtime layers

- Build-time layers from Maven and source compilation are excluded

- Each layer has a clear responsibility, improving cache reuse

Image Size Review
- docker images ullash997/my-nexcart-web-app:dev

Observations:

- Multi-stage builds significantly reduce the final image size

- Alpine-based runtime images help keep disk usage low

- Smaller images improve pull time and deployment speed

## Chapter 7: Docker Compose Setup

Docker Compose is used to define and orchestrate all application services.

### Services
- **backend-dev**: Development service with source code bind mounts
- **backend-prod**: Production service using the optimized Docker image
- **db**: PostgreSQL database with persistent storage

---

### Networks
- A custom bridge network (`nexcart-network`) is defined
- Enables isolated and predictable service communication

---

### Volumes
- `postgres-data` is used to persist database data across restarts
- Bind mounts are used in development for faster iteration

---

### Service Dependencies
- Backend services depend on the database service
- Startup order is enforced using health check conditions

---

### Resource Management
- CPU and memory limits are defined for production services
- Prevents a single container from exhausting host resources


## Chapter 8: Security and Best Practices

Several Docker security and operational best practices are applied.

- Multi-stage builds prevent build tools from reaching production images
- Non-root user is used to run the application container
- Secrets are managed using Docker secrets instead of environment variables
- Minimal base images reduce attack surface
- Health checks ensure early detection of unhealthy containers
- Resource limits prevent denial-of-service scenarios

These practices improve the overall security, stability, and maintainability
of the containerized application.


### Restart Policies
- Services use `unless-stopped` restart policy
- Improves resilience in case of unexpected failures

## Chapter 9: Summary

The NexCart Docker setup provides a clean, secure, and production-ready
containerization strategy.

Key highlights include:
- Optimized multi-stage Docker builds
- Clear separation between development and production environments
- Secure secret management and non-root execution
- Robust Docker Compose orchestration with health checks and resource limits

This approach ensures consistent deployments, easier maintenance,
and reliable application behavior across environments.

