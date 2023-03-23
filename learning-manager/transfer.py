import cluster
from cluster import InvalidClusterException
from cassandra.cluster import Cluster
from sklearn.linear_model import SGDRegressor
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
import pickle
import asyncio

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()


def getClusterFunction(clusterId: int):
    result = cassandraSession.execute(f"SELECT function_name FROM heterogeneous_faas.cluster_function WHERE cluster_id = {clusterId} ALLOW FILTERING")

    return result.one()

def getMlModel(functionName: str, worker: str):
    result = cassandraSession.execute(f"SELECT model FROM heterogeneous_faas.ml_model WHERE function_name='{functionName}' AND worker = '{worker}' ALLOW FILTERING")

    return result.one().model

def transferLearn(fromFunctionName: str, toFunctionName: str):
    # get from function's models
    cloudFromModel = pickle.loads(getMlModel(fromFunctionName, "AWS"))
    edgeFromModel = pickle.loads(getMlModel(fromFunctionName, "KUBERNETES"))

    # create new models for to function
    cloudToModel = SGDRegressor(loss="squared_error", alpha=0.0001)
    edgeToModel = SGDRegressor(loss="squared_error", alpha=0.0001)

    # transfer coefficient (weights) and intercept (bias) from from models to to models
    cloudToModel.coef_ = cloudFromModel.named_steps['sgdregressor'].coef_
    cloudToModel.intercept_ = cloudFromModel.named_steps['sgdregressor'].intercept_

    edgeToModel.coef_ = edgeFromModel.named_steps['sgdregressor'].coef_
    edgeToModel.intercept_ = edgeFromModel.named_steps['sgdregressor'].intercept_

    # save new models
    cloudRegressor = make_pipeline(
        StandardScaler(),
        cloudToModel
    )
    edgeRegressor = make_pipeline(
        StandardScaler(),
        cloudToModel
    )

    print(cloudRegressor.predict([[100]]))
    print(edgeRegressor.predict([[100]]))


async def transfer(functionName: str):
    try:
        clusterId = await cluster.predictFunction(functionName)
    except InvalidClusterException as ex:
        print(f"Predicted cluster for function {functionName} was invalid")
        return False # not transferred
    
    # get function from cluster
    function = getClusterFunction(clusterId)

    # transfer learn from function
    transferLearn(function.function_name, functionName)

    return True # transferred


if __name__ == '__main__':
    functionName = input("Enter function name: ")
    
    transfer = asyncio.run(transfer(functionName))
    print(transfer)