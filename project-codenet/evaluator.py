from cassandra.cluster import Cluster
import matplotlib.pyplot as plt

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()


seenInputSizes = [10, 50, 500, 1000]
unseenInputSizes = [1001, 1100, 1250, 2000]


def getFunctionExecutions(inputSize: int):
    results = cassandraSession.execute(f"SELECT predicted_duration, duration, function_name, worker FROM heterogeneous_faas.function_execution WHERE predicted_duration > 0 AND input_size = {inputSize} AND is_success = True ALLOW FILTERING")

    return results

def evaluateML():
    seenFunctionExecutions = []
    for inputSize in seenInputSizes:
        functionExecutions = getFunctionExecutions(inputSize)
        seenFunctionExecutions.extend(functionExecutions.all())

    unseenFunctionExecutions = []
    for inputSize in unseenInputSizes:
        functionExecutions = getFunctionExecutions(inputSize)
        unseenFunctionExecutions.extend(functionExecutions.all())
    
    seenData = {}

    for execution in seenFunctionExecutions:
        worker = execution.worker
        function_name = execution.function_name

        if worker not in seenData:
            seenData[worker] = {}

        if function_name not in seenData[worker]:
            seenData[worker][function_name] = {'x': [], 'y': []}

        seenData[worker][function_name]['x'].append(execution.predicted_duration)
        seenData[worker][function_name]['y'].append(execution.duration)

    for worker in seenData:
        for function_name in seenData[worker]:
            plt.scatter(seenData[worker][function_name]['x'], seenData[worker][function_name]['y'], label=function_name)

        plt.xlabel('Predicted Duration')
        plt.ylabel('Actual Duration')
        plt.title(f"{worker} Executions (seen input sizes)")
        plt.legend()
        plt.show()

    unseenFunctionExecutions = []
    
    for inputSize in unseenInputSizes:
        unseenFunctionExecutions.extend(getFunctionExecutions(inputSize))
    
    unseenData = {}

    for execution in unseenFunctionExecutions:
        worker = execution.worker
        function_name = execution.function_name

        if worker not in unseenData:
            unseenData[worker] = {}

        if function_name not in unseenData[worker]:
            unseenData[worker][function_name] = {'x': [], 'y': []}

        unseenData[worker][function_name]['x'].append(execution.predicted_duration)
        unseenData[worker][function_name]['y'].append(execution.duration)

    for worker in unseenData:
        for function_name in unseenData[worker]:
            plt.scatter(unseenData[worker][function_name]['x'], unseenData[worker][function_name]['y'], label=function_name)

        plt.xlabel('Predicted Duration')
        plt.ylabel('Actual Duration')
        plt.title(f"{worker} Executions (unseen input sizes)")
        plt.legend()
        plt.show()

if __name__ == '__main__':
    evaluateML()
