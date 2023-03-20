import json
import os

def writeToJson(problemId: str, solutionId: str):
    with open(f"./functions/{problemId}/{solutionId}/main.py", "r") as f:
        sourceCode = f.read()

    jsonString = json.dumps({"source_code": sourceCode})

    with open(f"./functions/{problemId}/{solutionId}/code.json", "w") as f:
        f.write(jsonString)

def writeAllToJson():
    for problemId in os.listdir("./functions/"):
        if problemId == "example":
            continue

        for solutionId in os.listdir(f"./functions/{problemId}/"):
            writeToJson(problemId, solutionId)