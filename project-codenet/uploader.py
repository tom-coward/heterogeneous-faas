import os
import docker
import boto3
import base64

dockerClient = docker.from_env()
ecrClient = boto3.client('ecr')

def upload(containerDirectory, awsAccountId, awsRegion):
    # create new ECR repo to house image in
    ecrRepoName = os.path.basename(containerDirectory)

    # check if repo already exists
    try:
        ecrClient.describe_repositories(repositoryNames = [ecrRepoName])
    except ecrClient.exceptions.RepositoryNotFoundException:
        # if it doesn't, create it...
        ecrClient.create_repository(
            repositoryName = ecrRepoName
        )

    # build the Docker image
    image, logs = dockerClient.images.build(path = containerDirectory)

    # tag the image for AWS ECR
    tag = f"{awsAccountId}.dkr.ecr.{awsRegion}.amazonaws.com/{ecrRepoName}:latest"
    dockerClient.api.tag(image.id, tag)

    # authenticate with AWS ECR
    authToken = ecrClient.get_authorization_token(registryIds = [awsAccountId])['authorizationData'][0]['authorizationToken']
    authUsername, authPassword = base64.b64decode(authToken).decode().split(':')

    dockerClient.login(username = authUsername, password = authPassword, registry = f"{awsAccountId}.dkr.ecr.{awsRegion}.amazonaws.com")
    print("Docker AWS ECR login successful")

    # push the image to AWS ECR
    dockerClient.images.push(tag)
    print(f"Image successfully pushed to {tag}")