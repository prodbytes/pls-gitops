#!/usr/bin/env bash
# Publishes the native pls binary to a GitHub release with JReleaser.
# The binary is extracted from the container image built and pushed by
# make-image.sh, so run that first. Requires a GitHub token with repo
# scope (JRELEASER_GITHUB_TOKEN, or GITHUB_TOKEN on GitHub Actions, or
# the gh CLI login).
set -euo pipefail

cd "$(dirname "$0")/.."

if [ -z "${JRELEASER_GITHUB_TOKEN:-}" ]; then
    if [ -n "${GITHUB_TOKEN:-}" ]; then
        JRELEASER_GITHUB_TOKEN="$GITHUB_TOKEN"
    elif command -v gh >/dev/null 2>&1; then
        JRELEASER_GITHUB_TOKEN="$(gh auth token 2>/dev/null || true)"
    fi
fi
: "${JRELEASER_GITHUB_TOKEN:?set JRELEASER_GITHUB_TOKEN to a GitHub token with repo scope, or log in with 'gh auth login'}"
export JRELEASER_GITHUB_TOKEN

VERSION="${VERSION:-$(./scripts/version.sh)}"

# Same image resolution as make-image.sh
if [ -z "${IMAGE:-}" ]; then
    NAMESPACE="${DOCKERHUB_NAMESPACE:-$(docker system info 2>/dev/null | awk '/Username:/ {print $2}')}"
    if [ -z "$NAMESPACE" ]; then
        echo "not logged in to Docker Hub; run 'docker login' or set DOCKERHUB_NAMESPACE" >&2
        exit 1
    fi
    IMAGE="docker.io/$NAMESPACE/pls"
fi

# Extract the native binary from the image instead of rebuilding it
CONTAINER="$(docker create "$IMAGE:$VERSION")" \
    || { echo "image $IMAGE:$VERSION not found; build it first with make-image.sh" >&2; exit 1; }
trap 'docker rm -f "$CONTAINER" >/dev/null 2>&1' EXIT
mkdir -p pls-cli/target
docker cp "$CONTAINER:/opt/pls/pls" pls-cli/target/pls
chmod +x pls-cli/target/pls

JRELEASER_PROJECT_VERSION="$VERSION"
export JRELEASER_PROJECT_VERSION

# devbox installs the binary as jreleaser-cli; other installs name it jreleaser
JRELEASER="$(command -v jreleaser-cli || command -v jreleaser)" \
    || { echo "jreleaser not found; enter the devbox shell first" >&2; exit 1; }

"$JRELEASER" full-release
