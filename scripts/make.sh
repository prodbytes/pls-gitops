#!/usr/bin/env bash
# Builds a native binary of pls-cli using Quarkus (GraalVM native-image).
set -euo pipefail

cd "$(dirname "$0")/../pls-cli"

# Prefer a local GraalVM so the pom's release target (25) and native-image
# are both available; otherwise fall back to a Quarkus container build.
DEFAULT_GRAALVM="$HOME/.jdks/graalvm-jdk-25"
if [ -z "${GRAALVM_HOME:-}" ] && [ -x "$DEFAULT_GRAALVM/bin/native-image" ]; then
    export GRAALVM_HOME="$DEFAULT_GRAALVM"
fi

MVN_ARGS=(package -Dnative -DskipTests)

if [ -n "${GRAALVM_HOME:-}" ]; then
    export JAVA_HOME="$GRAALVM_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
else
    echo "GraalVM not found locally; using Quarkus container build" >&2
    MVN_ARGS+=(-Dquarkus.native.container-build=true)
fi

./mvnw "${MVN_ARGS[@]}"

echo "Native binary: $(ls target/*-runner)"
