# build cassandra DB
docker pull cassandra:latest
docker network create cassandra

# build frontend server (flask server) docker
docker build -t frontend-server ./frontend-server

# TODO: build resource manager docker
