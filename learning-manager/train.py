from cassandra.cluster import Cluster
import numpy as np
from sklearn.linear_model import LinearRegression

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getFunctionExecutions(function_name: str):
    rows = cassandraSession.execute(f"SELECT input_size, duration FROM heterogeneous_faas.function_execution WHERE function_name='{function_name}'")

    inputSizes = []
    durations = []

    for row in rows:
        inputSizes.append(row.column1)
        durations.append(row.column2)

    return inputSizes, durations

def train(function_name: str):
    inputSizes, durations = getFunctionExecutions(function_name)

    inputSizes = np.array(inputSizes)
    durations = np.array(durations)

    X = inputSizes.reshape(-1, 1) 
    y = durations[:, 1]

    model = LinearRegression().fit(X, y)

    print("Coefficients: ", model.coef_)
    print("Intercept: ", model.intercept_)

cassandraSession.shutdown()
cassandraCluster.shutdown()