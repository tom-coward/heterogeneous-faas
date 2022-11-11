# build frontend server image (flask server)
docker build -t frontend-server ./frontend-server
# run frontend server container
docker run -d -p 5000:5000 --name heterogeneous-faas-frontend-server frontend-server:latest

# run resource-manager container

# run cassandra DB container
docker run --name cass_cluster cassandra:latest