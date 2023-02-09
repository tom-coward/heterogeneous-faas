# Resource Manager
The Resource Manager is used to orchestrate requests, with the help of the ML Manager, between the available workers.

## Important notes
When creating a function, a valid knative service account (named "builder") with a secret associated with it containing credentials to connect to AWS ECR **must** be present. This can be fetched automatically using the AWS CLI credential helper using `../knative/auth-ecr.sh`.