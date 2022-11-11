from flask import Flask
app = Flask(__name__)


@app.route("/function/invoke")
def invokeFunction():
    //


if __name__ == "__main__":
    app.run()