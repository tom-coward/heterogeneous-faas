from cassandra.cluster import Cluster
import numpy
from sklearn.linear_model import LinearRegression
import pickle
import uuid


cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

workers = ["AWS", "KUBERNETES"]

def getFunctionExecutions(functionName: str, worker: str):
    # TODO: remove ALLOW FILTERING if possible
    rows = cassandraSession.execute(f"SELECT input_size, duration FROM heterogeneous_faas.function_execution WHERE function_name='{functionName}' AND worker='{worker}' AND is_success=True ALLOW FILTERING")

    data = []

    for row in rows:
        data.append([row.input_size, row.duration])

    return data

def saveModel(functionName: str, worker: str, modelBytes: bytes):
    saveModelStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.ml_model (id, function_name, worker, model) VALUES (?, ?, ?, ?)")
    cassandraSession.execute(saveModelStatement, (uuid.uuid4(), functionName, worker, modelBytes))

def train(functionName: str):
    for worker in workers:    
        # get model features (input sizes and corresponding execution times)
        data = getFunctionExecutions(functionName, worker)
        data = numpy.array(data)

        # remove outliers (using Z-score)
        durations = data[:, 1]

        mean = numpy.mean(durations)
        stdDev = numpy.std(durations)
        zScores = (durations - mean) / stdDev

        threshold = 2
        outliers = numpy.where(zScores > threshold)

        data = numpy.delete(data, outliers, axis=0)

        # create linear regression model
        x = numpy.array(data[:, 0]).reshape(-1, 1)
        y = numpy.array(data[:, 1])

        model = LinearRegression().fit(x, y)

        # save model in Cassandra database (as a string representation of model object)
        modelBytes = pickle.dumps(model)
        saveModel(functionName, worker, modelBytes)
