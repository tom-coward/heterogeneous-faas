from cassandra.cluster import Cluster
import numpy
from sklearn.linear_model import SGDRegressor
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
import pickle
import uuid
import matplotlib.pyplot as plt
import asyncio


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

def getMlModel(functionName: str, worker: str):
    # TODO: remove ALLOW FILTERING if possible
    result = cassandraSession.execute(f"SELECT id, model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}' AND worker = '{worker}' ALLOW FILTERING")

    return result.one()

def saveModel(functionName: str, worker: str, modelBytes: bytes):
    saveModelStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.ml_model (id, function_name, worker, model) VALUES (?, ?, ?, ?)")
    cassandraSession.execute(saveModelStatement, (uuid.uuid4(), functionName, worker, modelBytes))

def updateModel(id, functionName: str, worker: str, modelBytes: bytes):
    saveModelStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.ml_model (id, function_name, worker, model) VALUES (?, ?, ?, ?)")
    cassandraSession.execute(saveModelStatement, (id, functionName, worker, modelBytes))

def getOutliers(durations):    
    # remove outliers (using IQR method)
    q1 = numpy.percentile(durations, 25)
    q3 = numpy.percentile(durations, 75)
    iqr = q3 - q1

    threshold = 1
    lowerBound = q1 - threshold * iqr
    upperBound = q3 + threshold * iqr

    outliers = numpy.where((durations < lowerBound) | (durations > upperBound))

    return outliers

def fitModel(functionName, worker):
    # get model features (input sizes and corresponding execution times)
    data = getFunctionExecutions(functionName, worker)
    data = numpy.array(data)

    # remove outliers
    durations = data[:, 1]
    outliers = getOutliers(durations)
    data = numpy.delete(data, outliers, axis=0)

    x = numpy.array(data[:, 0]).reshape(-1, 1)
    y = numpy.array(data[:, 1])

    # create linear regression model
    regressor = make_pipeline(
        StandardScaler(),
        SGDRegressor(loss="squared_error", alpha=0.0001)
    )

    # fit x and y to model
    regressor.fit(x, y)

    return regressor, x, y

async def train(functionName: str):
    for worker in workers:            
        # fit model
        regressor, x, y = fitModel(functionName, worker)

        # save model in Cassandra database (as a string representation of model object)
        modelBytes = pickle.dumps(regressor)
        saveModel(functionName, worker, modelBytes)

        # plot model and get coefficient score
        plotModel(x, y, regressor, worker)
        print(f"{worker} model coefficient score: {regressor.score(x, y)}")

async def incrementalTrain(functionName: str, worker: str):
    # re-fit model
    regressor, x, y = fitModel(functionName, worker)

    # update model in Cassandra database (as a string representation of model object)
    mlModel = getMlModel(functionName, worker)
    modelBytes = pickle.dumps(regressor)
    updateModel(mlModel.id, functionName, worker, modelBytes)

    # plot updated model and get updated coefficient score
    plotModel(x, y, regressor, worker)
    print(f"Coefficient score: {regressor.score(x, y)}")

def plotModel(x, y, regressor, worker):
    if __name__ != "__main__":
        return

    # plot original model as graph (if not running through server due to thread issues)
    plt.scatter(x, y)
    plt.plot(x, regressor.predict(x), color='red')
    plt.title(f'{worker} Model')
    plt.xlabel('Input size (n)')
    plt.ylabel('Duration (ms)')
    plt.show()


if __name__ == '__main__':
    mode = input("Enter mode (T = train or I = incremental train): ")

    functionName = input("Enter name of function to train: ")

    if mode == "T":
        asyncio.run(train(functionName))

    if mode == "I":
        worker = input("Enter worker: ")

        asyncio.run(incrementalTrain(functionName, worker))
