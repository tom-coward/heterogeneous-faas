import os
import docker
import boto3
import base64

dockerClient = docker.from_env()

ecrClient = boto3.client('ecr')

ecrRepoName = "heterogeneous-faas"
awsAccountId = "963689541346" 
awsRegion = "eu-west-1"

containerDirectory = input("Enter path of Docker container directory: ")

# Create new ECR repo to house image in
ecrRepoName = os.path.basename(containerDirectory)

ecrClient.create_repository(
    repositoryName = ecrRepoName
)

# Build the Docker image
image, logs = dockerClient.images.build(path = containerDirectory)

# Tag the image for AWS ECR
tag = f"{awsAccountId}.dkr.ecr.{awsRegion}.amazonaws.com/{ecrRepoName}:latest"
dockerClient.api.tag(image.id, tag)

# Authenticate with AWS ECR
authToken = ecrClient.get_authorization_token()['authorizationData'][0]['authorizationToken']
authUsername, authPassword = base64.b64decode(authToken).decode().split(':')
dockerClient.login(username = authUsername, password = authPassword, registry = f"{awsAccountId}.dkr.ecr.{awsRegion}.amazonaws.com")

# Push the image to AWS ECR
response = dockerClient.images.push(tag)

print(response)
