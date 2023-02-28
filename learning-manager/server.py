from flask import Flask, make_response
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
    # get predictions for the function, for each worker
    predictions = predict.getPredictions(functionName)

    response = make_response(predictions, 200)
    return response


if __name__ == '__main__':
    app.run()