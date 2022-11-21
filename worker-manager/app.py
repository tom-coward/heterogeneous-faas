from flask import Flask
import docker

# setup flask server (to take http requests from Resource Manager)
app = Flask(__name__)


@app.route('/')
def invoke():
    run_container()


# setup Docker Engine API SDK
def run_container():
    client: docker.DockerClient = docker.from_env()
    client.login()
    print(client.containers.list())


if __name__ == '__main__':
    app.debug = True
    app.run()  # start flask server
