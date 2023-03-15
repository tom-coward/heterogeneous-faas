import boto3
import docker
import base64
import cProfile
import pstats

awsSession = boto3.Session()
ecr = awsSession.client('ecr')
registryUrl = "963689541346.dkr.ecr.eu-west-1.amazonaws.com"

dockerClient = docker.from_env()

def getFunctionImage(functionName: str):
    response = ecr.get_authorization_token()
    authToken = response['authorizationData'][0]['authorizationToken']
    username, password = base64.b64decode(authToken).decode().split(':')

    image = dockerClient.images.pull(f"{registryUrl}/{functionName}:latest", auth_config={'username': username, 'password': password})

    return image

def profileFunction(functionName: str):
    image = getFunctionImage(functionName)
    
    container = dockerClient.containers.run(
        image, 
        command='python main.handler -m cProfile -o /tmp/cprofiler.prof'
    )
    container.wait()

    cProfileFile = container.get_archive('/tmp/cprofile.prof')[0][1]
    profileStats = pstats.Stats(cProfileFile)
    profileStats.sort_stats('cumulative').print_stats(10)


def transfer(functionName: str):
    functionProfile = profileFunction(functionName)
    print(functionProfile)
