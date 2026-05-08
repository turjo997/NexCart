#!/bin/bash
set -e

# 1. Set VERSION for all commands
export VERSION=1.0.0
IMAGE_NAME=my-nexcart-web-app
DOCKERHUB_USER=ullash997

# 2. Build image
docker build -t ${IMAGE_NAME}:${VERSION} .

# 3. Tag image for Docker Hub
docker tag ${IMAGE_NAME}:${VERSION} ${DOCKERHUB_USER}/${IMAGE_NAME}:${VERSION}

# 4. Push image
docker push ${DOCKERHUB_USER}/${IMAGE_NAME}:${VERSION}

# 5. Bring down any existing containers
docker compose --profile dev down

# 6. Create secrets folder and set DB password
mkdir -p secrets
echo "root" > secrets/db_password.txt
chmod 600 secrets/db_password.txt

# 7. Start containers
docker compose --profile dev up -d

# 8. Verify
docker images
docker compose ps
