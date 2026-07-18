#!/usr/bin/env bash
# Releases pls everywhere: builds and pushes the container image to
# Docker Hub (make-image.sh), then publishes the native binary extracted
# from that image to a GitHub release with JReleaser (make-jreleaser.sh).
# See each script for its prerequisites.
set -euo pipefail

cd "$(dirname "$0")"

# Pin the version once so the image tag and the GitHub release match
# (version.sh regenerates the Z timestamp component on every call).
VERSION="$(./version.sh)"
export VERSION

./make-image.sh
./make-jreleaser.sh

echo "Released $VERSION in $((SECONDS / 60))m$((SECONDS % 60))s"
