from flask import Flask, make_response
import train

app = Flask(__name__)

@app.route('/train/<string:functionName>', methods=['PUT'])
def putTrain(functionName: str):
    # initiate training of the model
    train.train(functionName)

    response = make_response("Training initiated", 202)
    return response

if __name__ == '__main__':
    app.run()