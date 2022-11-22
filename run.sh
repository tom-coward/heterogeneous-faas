# run hbase db server
docker run --rm -d --name cassandra --hostname cassandra --network cassandra cassandra

# run frontend server container
docker run -d -p 5001:5001 --name heterogeneous-faas-frontend-server frontend-server:latest

# TODO: run resource-manager container