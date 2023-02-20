import subprocess
import json

registryUrl = "963689541346.dkr.ecr.eu-west-1.amazonaws.com"
awsRegion = "eu-west-1"
secretName = "aws-ecr-secret"
serviceAccountName = "default"

def getEcrAuthInfo():
    command = ['aws', 'ecr', 'get-login-password', '--region', awsRegion]
    result = subprocess.run(command, stdout=subprocess.PIPE)
    return result.stdout.decode('utf-8').strip()

def createK8sSecret(authInfo):
    secretData = {
        '.dockerconfigjson': json.dumps({
            'auths': {
                registryUrl: {
                    'username': 'AWS',
                    'password': authInfo
                }
            }
        })
    }
    command = ['kubectl', 'create', 'secret', 'generic', secretName, '--from-literal=dockerconfigjson=' + json.dumps(secretData)]
    subprocess.run(command)

def addSecretToServiceAccount():
    command = ['kubectl', 'patch', 'serviceaccount', serviceAccountName, '-p', '{"imagePullSecrets":[{"name":"aws-ecr-secret"}]}']
    subprocess.run(command)

if __name__ == '__main__':
    authInfo = getEcrAuthInfo()
    createK8sSecret(authInfo)

    print(f"{secretName} secret created for {registryUrl} & added to the {serviceAccountName} service account")
