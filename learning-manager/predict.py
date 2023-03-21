from cassandra.cluster import Cluster
import pickle
import asyncio

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

async def getMLModels(functionName: str):
    results = cassandraSession.execute(f"SELECT worker, model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}'")

    return results

async def getPredictions(functionName: str, inputSize: int):
    models = await getMLModels(functionName)

    predictions = []

    for m in models:
        model = pickle.loads(m.model)

        prediction = model.predict([[inputSize]])

        predictions.append([m.worker, prediction[0]])

    return predictions


if __name__ == '__main__':
    functionName = input("Enter name of function to predict: ")
    inputSize = int(input("Enter input size: "))

    predictions = asyncio.run(getPredictions(functionName, inputSize))

    print(predictions)