from cassandra.cluster import Cluster

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getFunctionExecutions(function_name: str):
    functionExecutions = cassandraSession.execute(f"SELECT input_size, duration FROM heterogeneous_faas.function_execution WHERE function_name='{function_name}'")

    return functionExecutions

def train(function_name: str):
    functionExecutions = getFunctionExecutions(function_name)

    print(functionExecutions)
