# build hbase server docker
docker pull dajobe/hbase
docker build -t dajobe/hbase ./hbase
mkdir ./hbase/data
id=$(docker run --name=hbase-docker -h hbase-docker -d -v $PWD/hbase/data:/hbase/data dajobe/hbase)

# build frontend server (flask server) docker
docker build -t frontend-server ./frontend-server

# TODO: build resource manager docker
