#!/bin/bash
# Uploads the static website content to the S3 bucket created by
# aws-static-website.cform.yaml, deployed as stack "aws-static-website"
# (the template file stem). Safe to re-run; sync only uploads changes.
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

# Stack name is the script's file stem, matching the deploy convention.
script_name="$(basename -- "${BASH_SOURCE[0]}")"
stack_name="${script_name%%.*}"

site_dir="${script_dir}/../static-website"

bucket_name="$(aws cloudformation describe-stacks \
    --stack-name "${stack_name}" \
    --query "Stacks[0].Outputs[?OutputKey=='BucketName'].OutputValue" \
    --output text)"

if [[ -z "${bucket_name}" || "${bucket_name}" == "None" ]]; then
    echo "Could not resolve BucketName output from stack ${stack_name}" >&2
    exit 1
fi

echo "Uploading ${site_dir} to s3://${bucket_name}"
aws s3 sync "${site_dir}" "s3://${bucket_name}" --delete

website_url="$(aws cloudformation describe-stacks \
    --stack-name "${stack_name}" \
    --query "Stacks[0].Outputs[?OutputKey=='WebsiteURL'].OutputValue" \
    --output text)"

echo "Website available at ${website_url}"
