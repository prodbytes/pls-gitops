aws cloudformation delete-stack --stack-name {subject.path.stem} && aws cloudformation wait stack-delete-complete --stack-name {subject.path.stem}
