# Run Cassandra server docker image
docker run --name heterogeneous_faas_db -d -p 9042:9042 cassandra:latest

# To start the cql shell:
# docker exec -it heterogeneous_faas_db cqlsh