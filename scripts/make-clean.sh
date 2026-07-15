#!/usr/bin/env bash
# Cleans pls-cli build artifacts (mvn clean).
set -euo pipefail

cd "$(dirname "$0")/../pls-cli"

# Use the same local GraalVM as make.sh when available so mvnw has a JDK.
DEFAULT_GRAALVM="$HOME/.jdks/graalvm-jdk-25"
if [ -z "${JAVA_HOME:-}" ] && [ -x "$DEFAULT_GRAALVM/bin/java" ]; then
    export JAVA_HOME="$DEFAULT_GRAALVM"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

./mvnw clean
