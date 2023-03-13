import subprocess
import json

# AWS ECR Repository URI
repository_uri = "https://963689541346.dkr.ecr.eu-west-1.amazonaws.com/s103878717"

# Run the AWS CLI command to get the ECR Docker credentials
getCredsCmd = f"aws ecr get-login-password --region eu-west-1"
output = subprocess.check_output(getCredsCmd, shell=True)

dockerUsername = "AWS"
dockerPassword = output.decode("utf-8").strip()
dockerEmail = "tom@tomcoward.me"

try:
    # Try to delete the Kubernetes secret if it already exists
    deleteKnSecretCmd = "kubectl delete secret awsecr"
    subprocess.check_call(deleteKnSecretCmd, shell=True)
except:
    # ignore error
    pass

createKnSecretCmd = f"kubectl create secret docker-registry awsecr --docker-server={repository_uri} --docker-username={dockerUsername} --docker-password={dockerPassword} --docker-email={dockerEmail}"
subprocess.check_call(createKnSecretCmd, shell=True)

patchKnServiceAccountCmd = "kubectl patch serviceaccount default -p '{\"imagePullSecrets\": [{\"name\": \"awsecr\"}]}'"
subprocess.check_call(patchKnServiceAccountCmd, shell=True)

print("\nKubernetes secret for AWS ECR created")
