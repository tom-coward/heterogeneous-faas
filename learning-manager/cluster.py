from cassandra.cluster import Cluster
import cProfile
import io
import pstats
from sklearn.cluster import KMeans


cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()


def getFunctions():
    results = cassandraSession.execute(f"SELECT name, source_code, example_inputs FROM heterogeneous_faas.function")

    return results.all()

def getFunctionExecution(functionName: str, worker: str, inputSize: int):
    result = cassandraSession.execute(f"SELECT duration FROM heterogeneous_faas.function_execution WHERE function_name='{functionName}' AND worker='{worker}' AND input_size={inputSize} AND is_success=True ALLOW FILTERING")

    return result.one().duration

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
    cluster = KMeans(n_clusters=5).fit(x)

    return cluster

def cluster():
    functions = getFunctions()

    x = []

    for function in functions:
        functionName = function.name
        sourceCode = function.source_code
        exampleInputs = function.example_inputs

        #profileStats = profileFunction(sourceCode, exampleInputs)

        #print(profileStats)

        executionCloud1 = getFunctionExecution(functionName, 'AWS', 1)
        executionEdge1 = getFunctionExecution(functionName, 'KUBERNETES', 1)
        executionCloud100 = getFunctionExecution(functionName, 'AWS', 100)
        executionEdge100 = getFunctionExecution(functionName, 'KUBERNETES', 100)
        executionCloud500 = getFunctionExecution(functionName, 'AWS', 500)
        executionEdge500 = getFunctionExecution(functionName, 'KUBERNETES', 500)
        executionCloud1000 = getFunctionExecution(functionName, 'AWS', 1000)
        executionEdge1000 = getFunctionExecution(functionName, 'KUBERNETES', 1000)

        x.append([executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud500, executionEdge500, executionCloud1000, executionEdge1000])

    cluster = fitCluster(x)

    return cluster


if __name__ == '__main__':
    cluster = cluster()

    functionName = "s995239498"

    executionCloud1 = getFunctionExecution(functionName, 'AWS', 1)
    executionEdge1 = getFunctionExecution(functionName, 'KUBERNETES', 1)
    executionCloud100 = getFunctionExecution(functionName, 'AWS', 100)
    executionEdge100 = getFunctionExecution(functionName, 'KUBERNETES', 100)
    executionCloud500 = getFunctionExecution(functionName, 'AWS', 500)
    executionEdge500 = getFunctionExecution(functionName, 'KUBERNETES', 500)
    executionCloud1000 = getFunctionExecution(functionName, 'AWS', 1000)
    executionEdge1000 = getFunctionExecution(functionName, 'KUBERNETES', 1000)

    print(cluster.predict([[executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud500, executionEdge500, executionCloud1000, executionEdge1000]]))