from flask import Flask, make_response, request
import train
import predict

app = Flask(__name__)


@app.route('/train/<string:functionName>', methods=['PUT'])
def putTrain(functionName: str):
    # initiate training of the model
    train.train(functionName)

    response = make_response("Training initiated", 202)
    return response

@app.route('/predictions/<string:functionName>', methods=['GET'])
def getPredictions(functionName: str):    
    # get input size from URL query params
    inputSize = request.args.get('inputSize', '')
    if inputSize == None:
        response = make_response("Input size not provided", 400)
        return response
    inputSize = int(inputSize)
    
    # get predictions for the function, for each worker
    predictions = predict.getPredictions(functionName, inputSize)

    print(predictions)

    response = make_response(predictions, 200)
    return response


if __name__ == '__main__':
    app.run()