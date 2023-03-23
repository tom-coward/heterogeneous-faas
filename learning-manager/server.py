from quart import Quart, request, make_response
import train
import predict

app = Quart(__name__)


@app.route('/train/<string:functionName>', methods=['PUT'])
async def putTrain(functionName: str):
    # initiate training of the model
    await train.train(functionName)

    # cluster function
    #cluster.cluster(functionName)

    response = await make_response("Training initiated", 202)
    return response

@app.route('/train/incremental/<string:functionName>', methods=['PUT'])
async def putTrainIncremental(functionName: str):
    # get worker, input size and duration from URL query params
    worker = request.args.get('worker', None)
    if worker == None:
        response = make_response("Missing input (worker)", 400)
        return response
   
    # initiate incremental training of the model
    train.incrementalTrain(functionName, worker)

    response = await make_response("Incremental training initiated", 202)
    return response

@app.route('/predictions/<string:functionName>', methods=['GET'])
async def getPredictions(functionName: str):
    # get input size from URL query params
    inputSize = request.args.get('inputSize', None)
    if inputSize == None:
        return 400, "Input size not provided"
    inputSize = int(inputSize)
    
    # get predictions for the function, for each worker
    predictions = predict.getPredictions(functionName, inputSize)

    response = await make_response(predictions, 200)
    return response

@app.route('/transfer/<string:functionName>', methods=['PUT'])
async def putTransfer(functionName: str):
    # initiate transfer of the model
    transferred = transfer.transfer(functionName)

    if transferred:
        response = make_response("Transfer initiated", 202)
    else:
        response = make_response("Transfer failed", 500)
    
    return response


if __name__ == '__main__':
    app.run()