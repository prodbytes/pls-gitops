#!/bin/bash
# Empties the S3 bucket created by aws-static-website.cform.yaml, deployed as
# stack "aws-static-website" (the template file stem), so CloudFormation can
# delete the bucket when the stack is destroyed; non-empty buckets make the
# stack deletion fail.
set -euo pipefail

# Stack name is the script's file stem, matching the deploy convention.
script_name="$(basename -- "${BASH_SOURCE[0]}")"
stack_name="${script_name%%.*}"

bucket_name="$(aws cloudformation describe-stacks \
    --stack-name "${stack_name}" \
    --query "Stacks[0].Outputs[?OutputKey=='BucketName'].OutputValue" \
    --output text 2>/dev/null || true)"

if [[ -z "${bucket_name}" || "${bucket_name}" == "None" ]]; then
    echo "No BucketName output found for stack ${stack_name}; nothing to empty."
    exit 0
fi

echo "Emptying s3://${bucket_name}"
aws s3 rm "s3://${bucket_name}" --recursive
