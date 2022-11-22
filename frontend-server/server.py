from flask import Flask
import requests


app = Flask(__name__)


@app.route("/function/invoke")
def invokeFunction():
    # forward request to Resource Manager /invoke
    #requests.post()
    
    # return response from Resource Manager (function output) to client
    return "Hello World!"


if __name__ == "__main__":
    app.run()