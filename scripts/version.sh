#!/usr/bin/env bash
# Prints the release version X.Y.Z: X from version.x.txt, Y from
# version.y.txt, Z generated from the current timestamp (YYYYMMDDHHMM).
set -euo pipefail

cd "$(dirname "$0")/.."

X="$(tr -d '[:space:]' < version.x.txt)"
Y="$(tr -d '[:space:]' < version.y.txt)"
Z="$(date +%Y%m%d%H%M)"

echo "$X.$Y.$Z"
