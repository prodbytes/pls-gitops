#!/usr/bin/env bash
# Builds the pls-cli container image with Docker and pushes it to Docker Hub.
# Requires a prior `docker login`. The Docker Hub namespace defaults to the
# logged-in username; override with DOCKERHUB_NAMESPACE, or set IMAGE to the
# full repository name (e.g. IMAGE=docker.io/acme/pls).
set -euo pipefail

cd "$(dirname "$0")/.."

VERSION="${VERSION:-$(./scripts/version.sh)}"

if [ -z "${IMAGE:-}" ]; then
    NAMESPACE="${DOCKERHUB_NAMESPACE:-$(docker system info 2>/dev/null | awk '/Username:/ {print $2}')}"
    if [ -z "$NAMESPACE" ]; then
        echo "not logged in to Docker Hub; run 'docker login' or set DOCKERHUB_NAMESPACE" >&2
        exit 1
    fi
    IMAGE="docker.io/$NAMESPACE/pls"
fi

docker build -t "$IMAGE:$VERSION" -t "$IMAGE:latest" -f pls-cli/Containerfile pls-cli

docker push "$IMAGE:$VERSION"
docker push "$IMAGE:latest"

echo "Pushed $IMAGE:$VERSION and $IMAGE:latest"
