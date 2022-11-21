# run hbase db server
./hbase/start-hbase.sh

# run frontend server container
docker run -d -p 5000:5000 --name heterogeneous-faas-frontend-server frontend-server:latest

# TODO: run resource-manager container