from cassandra.cluster import Cluster
import numpy as np
from sklearn.linear_model import LinearRegression
import pickle
import uuid

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getWorkers():
    rows = cassandraSession.execute("SELECT id FROM heterogeneous_faas.worker")

    workers = []

    for row in rows:
        workers.append(row.id)

    return workers

def getFunctionExecutions(functionName: str, workerId: str):
    # TODO: remove ALLOW FILTERING if possible
    rows = cassandraSession.execute(f"SELECT input_size, duration FROM heterogeneous_faas.function_execution WHERE function_name='{functionName}' AND worker_id={workerId} AND is_success=True ALLOW FILTERING")

    inputSizes = []
    durations = []

    for row in rows:
        inputSizes.append(row.input_size)
        durations.append(row.duration)

    return inputSizes, durations

def saveModel(functionName: str, workerId: str, modelString: str):
    cassandraSession.execute(f"INSERT INTO heterogeneous_faas.ml_model (id, function_name, worker_id, model) VALUES ({uuid.uuid4()}, '{functionName}', {workerId}, '{modelString}')")

def train(functionName: str):
    # get workers to train function on
    workers = getWorkers()

    for workerId in workers:    
        # get model features (input sizes and corresponding execution times)
        inputSizes, durations = getFunctionExecutions(functionName, workerId)

        # create linear regression model
        x = np.array(inputSizes).reshape(-1, 1)
        y = np.array(durations)

        model = LinearRegression().fit(x, y)

        print("Coefficients: ", model.coef_)
        print("Intercept: ", model.intercept_)

        # save model in Cassandra database (as a string representation of model object)
        modelString = pickle.dumps(model)

        saveModel(functionName, workerId, modelString)
