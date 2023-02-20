To grab a new authentication token for AWS ECR (base64 encoded in the format "username:password"), and then create a Kubernetes `dockerconfigjson` secret using the credentials, run `python authenticator.py`.

For the app to work (as it simply calls CLI commands), you'll need the aws-cli and kubectl installed, with an active Kubernetes cluster running.