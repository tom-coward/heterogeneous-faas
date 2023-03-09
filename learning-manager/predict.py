from cassandra.cluster import Cluster
import pickle

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getMLModels(functionName: str):
    rows = cassandraSession.execute(f"SELECT worker_id, model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}'")

    models = []

    for row in rows:
        models.append([row.worker_id, row.model])

    return models

def getPredictions(functionName: str, inputSize: int):
    predictions = []
    
    models = getMLModels(functionName)

    for m in models:
        model = pickle.load(m[1])

        prediction = model.predict([[inputSize]])

        predictions.append([m[0], prediction])

    return predictions