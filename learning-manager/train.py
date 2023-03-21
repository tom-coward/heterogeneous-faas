from cassandra.cluster import Cluster
import numpy
from sklearn.pipeline import make_pipeline
from sklearn.linear_model import LinearRegression
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
    rows = cassandraSession.execute(f"SELECT input_size, duration FROM heterogeneous_faas.function_execution WHERE function_name='{functionName}' AND worker='{worker}' AND is_success=True AND input_size < 1000 ALLOW FILTERING")

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

async def train(functionName: str):
    for worker in workers:            
        # get model features (input sizes and corresponding execution times)
        data = getFunctionExecutions(functionName, worker)
        data = numpy.array(data)

        # remove outliers (using IQR)
        durations = data[:, 1]
        
        q1 = numpy.percentile(durations, 25)
        q3 = numpy.percentile(durations, 75)
        iqr = q3 - q1

        threshold = 1
        lowerBound = q1 - threshold * iqr
        upperBound = q3 + threshold * iqr

        outliers = numpy.where((durations < lowerBound) | (durations > upperBound))
        data = numpy.delete(data, outliers, axis=0)

        # transform data
        x = numpy.array(data[:, 0]).reshape(-1, 1)
        y = numpy.array(data[:, 1])

        # create linear regression model
        regressor = make_pipeline(
            StandardScaler(),
            LinearRegression()
        )

        regressor.fit(x, y)

        # plot original model as graph (if not running through server due to thread issues)
        if __name__ == "__main__":
            plt.scatter(x, y)
            #plt.plot(x, regressor.predict(x), color='red')
            plt.title(f'{worker} Model')
            plt.xlabel('Input size')
            plt.ylabel('Duration')
            plt.show()

        # save model in Cassandra database (as a string representation of model object)
        modelBytes = pickle.dumps(regressor)
        saveModel(functionName, worker, modelBytes)

async def incrementalTrain(functionName: str, worker: str, inputSize: int, duration: float):
    # get existing model
    mlModel = getMlModel(functionName, worker)
    regressor = pickle.loads(mlModel.model)

    newX = numpy.array([inputSize]).reshape(-1, 1)
    newY = numpy.array([duration])
    
    # fit new data (input size & duration) to model
    regressor.named_steps['sgdregressor'].partial_fit(newX, newY)

    # save model in Cassandra database (as a string representation of model object)
    modelBytes = pickle.dumps(regressor)
    updateModel(mlModel.id, functionName, worker, modelBytes)



if __name__ == '__main__':
    mode = input("Enter mode (T = train or I = incremental train): ")

    functionName = input("Enter name of function to train: ")

    if mode == "T":
        asyncio.run(train(functionName))

    if mode == "I":
        worker = input("Enter worker: ")
        inputSize = int(input("Enter input size: "))
        duration = float(input("Enter duration: "))

        asyncio.run(incrementalTrain(functionName, worker, inputSize, duration))
