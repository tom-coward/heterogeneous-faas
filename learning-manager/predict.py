from cassandra.cluster import Cluster
import pickle

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getMLModels(functionName: str):
    rows = cassandraSession.execute(f"SELECT worker, model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}'")

    models = []

    for row in rows:
        models.append([row.worker, row.model])

    return models

def getPredictions(functionName: str, inputSize: int):
    models = getMLModels(functionName)

    predictions = []

    for m in models:
        model = pickle.loads(m[1])

        prediction = model.predict([[inputSize]])

        predictions.append([m[0], prediction[0]])

    return predictions