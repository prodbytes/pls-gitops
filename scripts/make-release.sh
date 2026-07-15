#!/usr/bin/env bash
# Publishes the native pls binary to a GitHub release with JReleaser.
# Requires JRELEASER_GITHUB_TOKEN (a GitHub token with repo scope) and a
# previously built native binary (`make native`).
set -euo pipefail

cd "$(dirname "$0")/.."

: "${JRELEASER_GITHUB_TOKEN:?set JRELEASER_GITHUB_TOKEN to a GitHub token with repo scope}"

if [ ! -x pls-cli/target/pls ]; then
    echo "pls-cli/target/pls not found; build it first with 'make native'" >&2
    exit 1
fi

JRELEASER_PROJECT_VERSION="$(cd pls-cli && ./mvnw -q help:evaluate -Dexpression=project.version -DforceStdout)"
export JRELEASER_PROJECT_VERSION

# devbox installs the binary as jreleaser-cli; other installs name it jreleaser
JRELEASER="$(command -v jreleaser-cli || command -v jreleaser)" \
    || { echo "jreleaser not found; enter the devbox shell first" >&2; exit 1; }

exec "$JRELEASER" full-release
