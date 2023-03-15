import json

def writeToJson(solutionId: str):
    with open(f"./functions/{solutionId}/main.py", "r") as f:
        sourceCode = f.read()

    jsonString = json.dumps({"source_code": sourceCode})

    with open(f"./functions/{solutionId}/code.json", "w") as f:
        f.write(jsonString)
