# run hbase db server
docker run -p 9042:9042 cassandra:latest

# run frontend server container
docker run -d -p 5001:5001 --name heterogeneous-faas-frontend-server frontend-server:latest

# run resource-manager
cd resource-manager
gradle build
gradle run