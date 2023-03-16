from cassandra.cluster import Cluster
import numpy
from sklearn.pipeline import make_pipeline
from sklearn.linear_model import SGDRegressor
from sklearn.preprocessing import StandardScaler
import pickle
import uuid
import matplotlib.pyplot as plt


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
    result = cassandraSession.execute(f"SELECT model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}' AND worker = '{worker}' ALLOW FILTERING")

    return result.one().model

def saveModel(functionName: str, worker: str, modelBytes: bytes):
    saveModelStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.ml_model (id, function_name, worker, model) VALUES (?, ?, ?, ?)")
    cassandraSession.execute(saveModelStatement, (uuid.uuid4(), functionName, worker, modelBytes))

def train(functionName: str):
    for worker in workers:    
        print(worker)
        
        # get model features (input sizes and corresponding execution times)
        data = getFunctionExecutions(functionName, worker)
        data = numpy.array(data)

        numpy.set_printoptions(threshold=numpy.inf)
        print(data)

        # remove outliers (using Z-score)
        durations = data[:, 1]

        mean = numpy.mean(durations)
        stdDev = numpy.std(durations)
        zScores = (durations - mean) / stdDev

        threshold = 3
        outliers = numpy.where(zScores > threshold)

        data = numpy.delete(data, outliers, axis=0)

        # create linear regression model
        regressor = make_pipeline(
            StandardScaler(),
            SGDRegressor(eta0=0.01, learning_rate="constant", random_state=42)
        )

        x = numpy.array(data[:, 0]).reshape(-1, 1)
        y = numpy.array(data[:, 1])

        regressor.fit(x, y)

        # plot original model as graph
        plt.scatter(x, y)
        plt.plot(x, regressor.predict(x), color='red')
        plt.title(f'{worker} Model')
        plt.xlabel('Input size')
        plt.ylabel('Duration')
        plt.show()

        # save model in Cassandra database (as a string representation of model object)
        modelBytes = pickle.dumps(regressor)
        saveModel(functionName, worker, modelBytes)

def incrementalTrain(functionName: str, worker: str, inputSize: int, duration: float):
    # get existing model
    mlModel = getMlModel(functionName, worker)
    regressor = pickle.loads(mlModel)
    
    # fit new data (input size & duration) to model
    regressor.named_steps['sgdregressor'].partial_fit(inputSize, duration)


if __name__ == '__main__':
    functionName = input("Enter name of function to train: ")
    train(functionName)
