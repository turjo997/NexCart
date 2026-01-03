#!/bin/sh
set -e

echo "Starting NextCart Application..."
echo "Waiting for PostgreSQL to be ready..."

# Wait for DB (host "db" from docker-compose network)
while ! nc -z db 5432; do
  echo "$(date) - PostgreSQL not ready yet, waiting..."
  sleep 3
done

echo "PostgreSQL is up - starting Spring Boot application"

# Run migrations if you use Flyway/Liquibase (optional)
# java -jar app.jar --spring.flyway.enabled=true

# Start the app with any JVM options
exec java \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp \
  -jar /app/app.jar \
  "$@"