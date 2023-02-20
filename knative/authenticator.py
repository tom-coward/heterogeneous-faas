import subprocess
import json

def get_ecr_auth_info():
    command = ['aws', 'ecr', 'get-login-password']
    result = subprocess.run(command, stdout=subprocess.PIPE)
    return result.stdout.decode('utf-8').strip()

def create_k8s_secret(auth_info):
    secret_data = {
        '.dockerconfigjson': json.dumps({
            'auths': {
                'my-registry-url': {
                    'username': 'AWS',
                    'password': auth_info
                }
            }
        })
    }
    command = ['kubectl', 'create', 'secret', 'generic', 'aws-ecr-secret', '--from-literal=dockerconfigjson=' + json.dumps(secret_data)]
    subprocess.run(command)

if __name__ == '__main__':
    auth_info = get_ecr_auth_info()
    create_k8s_secret(auth_info)
