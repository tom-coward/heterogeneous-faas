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
        inputSizes.append(row.input_size)
        durations.append(row.duration)

    return inputSizes, durations

def train(function_name: str):
    inputSizes, durations = getFunctionExecutions(function_name)

    x = np.array(inputSizes).reshape(-1, 1)
    y = np.array(durations)

    model = LinearRegression().fit(x, y)

    print("Coefficients: ", model.coef_)
    print("Intercept: ", model.intercept_)