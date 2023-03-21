from cassandra.cluster import Cluster
import cProfile
import io
import pstats


cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

def getFunction(functionName: str):
    result = cassandraSession.execute(f"SELECT source_code, example_inputs FROM heterogeneous_faas.function WHERE name='{functionName}'")

    return result.one()

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

def cluster(functionName: str):
    function = getFunction(functionName)

    sourceCode = function.source_code
    exampleInputs = function.example_inputs

    print(sourceCode)
    print(exampleInputs)

    profileStats = profileFunction(sourceCode, exampleInputs)
    print(profileStats)


if __name__ == '__main__':
    functionName = input("Enter function name: ")

    print(cluster(functionName))