from cassandra.cluster import Cluster
import cProfile
import pstats
from sklearn.cluster import KMeans
import pickle
import aiohttp
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
    inputsJson = f"[{inputsString}]"
    
    requestBody = "{\"function_name\": \"" + functionName + "\", \"worker\": \"" + worker + "\", \"function_payload\": " + inputsJson + "}"
    data = bytes(requestBody, encoding='utf-8')

    async with aiohttp.ClientSession() as session:
        requestStartTime = time.time()

        async with session.put(f"{resourceManagerUrl}/function/invoke", data=data) as response:
            await response.text()

            requestEndTime = time.time()
            requestTime = (requestEndTime - requestStartTime) * 1000 # get request duration in ms

    print(f"Request took {requestTime}ms")
    return requestTime
        

def profileFunction(sourceCode: str, exampleInputs: list):
    selectedInputs = f"[{exampleInputs[0]}]"

    code = f"{sourceCode}\nhandler({selectedInputs}, None)"
    compiledCode = compile(code, "main.py", "exec")

    # profile module source code
    profile = cProfile.Profile()
    profile.run(compiledCode)

    # get stats from profile
    stats = pstats.Stats(profile)
    stats.strip_dirs()
    statsValues = stats.stats.values()

    # get execution time, average memory and CPU usage throughout lifetime of function
    numCalls = 0
    memoryUsage = 0
    cpuUsage = 0
    
    for stat in statsValues:
        numCalls += stat[0]
        memoryUsage += stat[3]
        cpuUsage += stat[2]

    averageMemoryUsage = numpy.round(memoryUsage / numCalls, decimals=5)
    averageCpuUsage = numpy.round(cpuUsage / numCalls, decimals=5)

    print(averageMemoryUsage)
    print(averageCpuUsage)

    return averageMemoryUsage, averageCpuUsage

def saveCluster(clusterBytes):
    saveClusterStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.cluster (id, model) VALUES (?, ?)")
    cassandraSession.execute(saveClusterStatement, ("cluster", clusterBytes))

def setFunctionClusterId(functionName: str, clusterId: int):
    updateFunctionStatement = cassandraSession.prepare(f"UPDATE heterogeneous_faas.function SET cluster_id = ? WHERE name = ?")
    cassandraSession.execute(updateFunctionStatement, (clusterId, functionName))

def getCluster():
    result = cassandraSession.execute(f"SELECT model FROM heterogeneous_faas.cluster WHERE id='cluster'")

    return result.one().model

def fitCluster(x):
    n_clusters = 8 # max 8 clusters - but can only have as many clusters as functions
    
    if (len(x)) < n_clusters:
        raise Exception(f"There must be at least {n_clusters} functions to cluster")

    cluster = KMeans(n_clusters=n_clusters).fit(x)

    return cluster

async def cluster():
    functions = getFunctions()

    x = []

    for function in functions:
        functionName = function.name
        sourceCode = function.source_code
        exampleInputs = function.example_inputs

        # get memory and CPU usage from profiler
        profileStats = profileFunction(sourceCode, exampleInputs)
        averageMemoryUsage = profileStats[0]
        averageCpuUsage = profileStats[1]

        # execute once on cloud & edge to avoid cold start
        await executeFunction(functionName, 'AWS', exampleInputs, 1)
        await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)

        # get execution times with different input sizes (1, 100, 200, 500, 750 and 1000) in cloud & edge
        executionCloud1 = await executeFunction(functionName, 'AWS', exampleInputs, 1)
        executionEdge1 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)
        executionCloud100 = await executeFunction(functionName, 'AWS', exampleInputs, 100)
        executionEdge100 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 100)
        executionCloud200 = await executeFunction(functionName, 'AWS', exampleInputs, 200)
        executionEdge200 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 200)
        executionCloud500 = await executeFunction(functionName, 'AWS', exampleInputs, 500)
        executionEdge500 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 500)
        executionCloud750 = await executeFunction(functionName, 'AWS', exampleInputs, 750)
        executionEdge750 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 750)
        executionCloud1000 = await executeFunction(functionName, 'AWS', exampleInputs, 1000)
        executionEdge1000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000)

        x.append([functionName, averageMemoryUsage, averageCpuUsage, executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud200, executionEdge200, executionCloud500, executionEdge500, executionCloud750, executionEdge750, executionCloud1000, executionEdge1000])

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
    distanceThreshold = 30

    if ignore == False and distances[0][predictedCluster[0]] > distanceThreshold:
        raise InvalidClusterException("Prediction is too far from cluster")

    return predictedCluster[0]
    
async def predictFunction(functionName: str):
    function = getFunction(functionName)

    sourceCode = function.source_code
    exampleInputs = function.example_inputs

    # get memory and CPU usage from profiler
    profileStats = profileFunction(sourceCode, exampleInputs)
    averageMemoryUsage = profileStats[0]
    averageCpuUsage = profileStats[1]
    
     # execute once on cloud & edge to avoid cold start
    await executeFunction(functionName, 'AWS', exampleInputs, 1)
    await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)

    # get execution times with different input sizes (1, 100, 200, 500, 750 and 1000) in cloud & edge
    executionCloud1 = await executeFunction(functionName, 'AWS', exampleInputs, 1)
    executionEdge1 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)
    executionCloud100 = await executeFunction(functionName, 'AWS', exampleInputs, 100)
    executionEdge100 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 100)
    executionCloud200 = await executeFunction(functionName, 'AWS', exampleInputs, 200)
    executionEdge200 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 200)
    executionCloud500 = await executeFunction(functionName, 'AWS', exampleInputs, 500)
    executionEdge500 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 500)
    executionCloud750 = await executeFunction(functionName, 'AWS', exampleInputs, 750)
    executionEdge750 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 750)
    executionCloud1000 = await executeFunction(functionName, 'AWS', exampleInputs, 1000)
    executionEdge1000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000)

    x = numpy.array([averageMemoryUsage, averageCpuUsage, executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud200, executionEdge200, executionCloud500, executionEdge500, executionCloud750, executionEdge750, executionCloud1000, executionEdge1000])

    return predict(x)


if __name__ == '__main__':
    cluster = asyncio.run(cluster())