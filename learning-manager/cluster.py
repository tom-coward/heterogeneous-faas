from cassandra.cluster import Cluster
import cProfile
import io
import pstats
from sklearn.cluster import KMeans
import pickle
import aiohttp
import json
import time
import asyncio
import numpy

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

resourceManagerUrl = "http://localhost:5001"

class InvalidClusterException(Exception):
    "Raised when the predicted cluster is too distant"
    pass


def getFunctions():
    results = cassandraSession.execute(f"SELECT name, source_code, example_inputs FROM heterogeneous_faas.function")

    return results.all()

def getFunction(functionName: str):
    result = cassandraSession.execute(f"SELECT name, source_code, example_inputs FROM heterogeneous_faas.function WHERE name = '{functionName}'")

    return result.one()

async def executeFunction(functionName: str, worker: str, exampleInputs: list, inputSize: int):
    inputs = exampleInputs[:inputSize]
    inputsString = ", ".join(inputs)
    inputsJson = json.dumps(inputsString)
    
    requestBody = "{\"function_name\": \"" + functionName + "\", \"worker\": \"" + worker + "\", \"function_payload\": " + inputsJson + "}"
    data = bytes(requestBody, encoding='utf-8')

    async with aiohttp.ClientSession() as session:
        requestStartTime = time.time()

        async with session.put(f"{resourceManagerUrl}/function/invoke", data=data) as response:
            response = await response.text()

            requestEndTime = time.time()
            requestTime = (requestEndTime - requestStartTime) * 1000 # get request duration in ms

    print(f"Request took {requestTime}ms. Response: {response}")
    return requestTime
        

def profileFunction(sourceCode: str, exampleInputs: list):
    selectedInputs = exampleInputs[0]

    profiler = cProfile.Profile()

    compiledCode = compile(f"{sourceCode}\nprofiler.enable()\nhandler({selectedInputs}, None)\nprofiler.disable()", "main.py", "exec")

    exec(compiledCode)

    profilerOutput = io.StringIO()
    profilerStats = pstats.Stats(profiler, stream=profilerOutput).sort_stats("cumulative")
    profilerStats.print_stats()

    profilerOutput.seek(0)
    stats = profilerOutput.read()

    return stats

def fitCluster(x):
    n_clusters = min(len(x), 8) # max 8 clusters - but can only have as many clusters as functions

    cluster = KMeans(n_clusters=n_clusters).fit(x)

    return cluster

def saveCluster(clusterBytes):
    saveClusterStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.cluster (id, model) VALUES (?, ?)")
    cassandraSession.execute(saveClusterStatement, ("cluster", clusterBytes))

def setFunctionClusterId(functionName: str, clusterId: int):
    updateFunctionStatement = cassandraSession.prepare(f"UPDATE heterogeneous_faas.function SET cluster_id = ? WHERE name = ?")
    cassandraSession.execute(updateFunctionStatement, (clusterId, functionName))

def getCluster():
    result = cassandraSession.execute(f"SELECT model FROM heterogeneous_faas.cluster WHERE id='cluster'")

    return result.one().model

async def cluster():
    functions = getFunctions()

    x = []

    for function in functions:
        functionName = function.name
        sourceCode = function.source_code
        exampleInputs = function.example_inputs

        #profileStats = profileFunction(sourceCode, exampleInputs)

        #print(profileStats)

        # execute once on cloud & edge to avoid cold start
        await executeFunction(functionName, 'AWS', exampleInputs, 1)
        await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)

        executionCloud1 = await executeFunction(functionName, 'AWS', exampleInputs, 1)
        executionEdge1 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)
        executionCloud100 = await executeFunction(functionName, 'AWS', exampleInputs, 100)
        executionEdge100 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 100)
        executionCloud1000 = await executeFunction(functionName, 'AWS', exampleInputs, 1000)
        executionEdge1000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000)
        executionCloud1500 = await executeFunction(functionName, 'AWS', exampleInputs, 1500)
        executionEdge1500 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1500)
        executionCloud2000 = await executeFunction(functionName, 'AWS', exampleInputs, 2000)
        executionEdge2000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 2000)

        x.append([functionName, executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud1000, executionEdge1000, executionCloud1500, executionEdge1500, executionCloud2000, executionEdge2000])

    xWithoutLabels = [array[1:] for array in x]
    
    cluster = fitCluster(xWithoutLabels) # fit x values to cluster - don't include functionName identifer

    clusterBytes = pickle.dumps(cluster)
    saveCluster(clusterBytes)

    labels = cluster.predict(xWithoutLabels) # get cluster each function belongs to

    for i in range(len(x)):
        functionName = x[i][0]
        clusterId = labels[i]

        setFunctionClusterId(functionName, clusterId)

    return cluster

def predict(x, ignore=False):
    clusterBytes = getCluster()
    cluster = pickle.loads(clusterBytes)

    predictedCluster = cluster.predict(x)

    distances = cluster.transform(x)
    distanceThreshold = 0.5

    print(distances[0][predictedCluster[0]])

    if ignore == False and distances[0][predictedCluster[0]] > distanceThreshold:
        raise InvalidClusterException("Prediction is too far from cluster")

    return predictedCluster[0]

async def predictFunction(functionName: str):
    function = getFunction(functionName)

    exampleInputs = function.example_inputs
    
    executionCloud1 = await executeFunction(functionName, 'AWS', exampleInputs, 1)
    executionEdge1 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)
    executionCloud100 = await executeFunction(functionName, 'AWS', exampleInputs, 100)
    executionEdge100 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 100)
    executionCloud1000 = await executeFunction(functionName, 'AWS', exampleInputs, 1000)
    executionEdge1000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000)
    executionCloud1500 = await executeFunction(functionName, 'AWS', exampleInputs, 1500)
    executionEdge1500 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1500)
    executionCloud2000 = await executeFunction(functionName, 'AWS', exampleInputs, 2000)
    executionEdge2000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 2000)

    x = numpy.array([executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud1000, executionEdge1000, executionCloud1500, executionEdge1500, executionCloud2000, executionEdge2000]).reshape(1, -1)

    return predict(x)


if __name__ == '__main__':
    cluster = asyncio.run(cluster())