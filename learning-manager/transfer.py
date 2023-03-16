import cProfile
from cassandra.cluster import Cluster


cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

inputSize = 100

def getFunction(functionName: str):
    result = cassandraSession.execute(f"SELECT source_code, example_inputs FROM heterogeneous_faas.function WHERE name='{functionName}'")

    return result.one().source_code, result.one().example_inputs

def runSourceCode(sourceCode: str, inputs: list):
    program = compile(sourceCode, "", "exec")
    return exec(program, {}, {"data": inputs})

def profileFunction(sourceCode: str, inputs: list):
    selectedInputs = inputs[:inputSize]
    
    cProfile.runctx(f"runSourceCode({sourceCode}, {selectedInputs})", globals=globals(), locals=locals())

def transfer(functionName: str):
    functionSourceCode, functionExampleInputs = getFunction(functionName)
    
    functionProfile = profileFunction(functionSourceCode, functionExampleInputs)
    print(functionProfile)


if __name__ == "__main__":
    functionName = input("Enter name of function to attempt to transfer learn: ")
    
    functionSourceCode, functionExampleInputs = getFunction(functionName)

    print(functionSourceCode)

    print(runSourceCode(functionSourceCode, functionExampleInputs))
