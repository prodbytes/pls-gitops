#!/usr/bin/env bash
# Deploys the aws-static-website sample like DeployAWSStaticWebsiteCform, but
# with the pls container image instead of running from source. The whole
# sample directory is mounted at /docker-entrypoint.d so both the IaC and the
# website content are in context, and the cform prefix limits scanning to the
# IaC; the after-deploy hook still reaches ../static-website inside the mount.
#
# The image defaults to the published docker.io/prodbytes/pls; override with
# IMAGE (e.g. IMAGE=pls-local-test after building pls-cli/Containerfile).
# AWS credentials are passed through from the host environment, plus ~/.aws
# when present.
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "${script_dir}/../../../../../../../.." && pwd)"
sample_dir="${repo_root}/samples/aws-static-website"

IMAGE="${IMAGE:-docker.io/prodbytes/pls}"

docker image inspect "${IMAGE}" >/dev/null 2>&1 || docker pull "${IMAGE}"

# The image entrypoint bakes in the gitops goal (e.g. ["/pls","gitops"]), so
# read the binary path from the image and override the entrypoint to run the
# deploy goal, matching the other samples in this directory.
pls_bin="$(docker image inspect -f '{{index .Config.Entrypoint 0}}' "${IMAGE}")"

docker_args=(
    --rm
    -v "${sample_dir}:/docker-entrypoint.d"
    -e AWS_ACCESS_KEY_ID
    -e AWS_SECRET_ACCESS_KEY
    -e AWS_SESSION_TOKEN
    -e AWS_PROFILE
    -e AWS_REGION
    -e AWS_DEFAULT_REGION
)

if [ -d "${HOME}/.aws" ]; then
    docker_args+=(-v "${HOME}/.aws:/.aws:ro")
fi

docker run "${docker_args[@]}" --entrypoint "${pls_bin}" "${IMAGE}" \
    deploy /docker-entrypoint.d cform
