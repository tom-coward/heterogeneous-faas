aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 963689541346.dkr.ecr.eu-west-1.amazonaws.com

kubectl create secret generic regcred \
    --from-file=.dockerconfigjson=/Users/tom/.docker/config.json \
    --type=kubernetes.io/dockerconfigjson