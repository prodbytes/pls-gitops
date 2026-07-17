#!/usr/bin/env bash
# Builds a native binary of pls-cli using Quarkus (GraalVM native-image).
set -euo pipefail

cd "$(dirname "$0")/../pls-cli"

# Nix packages (zlib from devbox.json) are not on the default linker search
# path; native-image's final gcc link needs libz, so point LIBRARY_PATH at
# the devbox profile. native-image sanitizes the builder's environment, so
# the variable must also be forwarded explicitly with -E (see MVN_ARGS).
DEVBOX_LIB="$(cd .. && pwd)/.devbox/nix/profile/default/lib"
if [ -d "$DEVBOX_LIB" ]; then
    export LIBRARY_PATH="$DEVBOX_LIB${LIBRARY_PATH:+:$LIBRARY_PATH}"
fi

# Prefer a local GraalVM so the pom's release target (25) and native-image
# are both available; otherwise fall back to a Quarkus container build.
DEFAULT_GRAALVM="$HOME/.jdks/graalvm-jdk-25"
if [ -z "${GRAALVM_HOME:-}" ] && [ -x "$DEFAULT_GRAALVM/bin/native-image" ]; then
    export GRAALVM_HOME="$DEFAULT_GRAALVM"
fi

MVN_ARGS=(package -Dnative -DskipTests)

# Forward LIBRARY_PATH into the (env-sanitized) native-image builder so the
# final gcc link finds libz; -append keeps application.properties' args.
if [ -n "${LIBRARY_PATH:-}" ]; then
    MVN_ARGS+=("-Dquarkus.native.additional-build-args-append=-ELIBRARY_PATH")
fi

if [ -n "${GRAALVM_HOME:-}" ]; then
    export JAVA_HOME="$GRAALVM_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
else
    echo "GraalVM not found locally; using Quarkus container build" >&2
    MVN_ARGS+=(-Dquarkus.native.container-build=true)
fi

./mvnw "${MVN_ARGS[@]}"

echo "Native binary: $(ls target/pls)"
