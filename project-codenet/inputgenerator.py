import random
import argparse
import json
import string

def generateIntInputs(fileName, numColumns, numRows, lowerBound, upperBound):
    totalInputs = numColumns * numRows

    inputs = [random.randint(lowerBound, upperBound) for i in range(totalInputs)]
    
    rows = []

    for i in range(numRows):
        startIndex = i * numColumns
        endIndex = startIndex + numColumns
        rowInputs = inputs[startIndex:endIndex]
        rowString = ' '.join(map(str, rowInputs))
        rows.append(rowString)

    inputsArray = {"example_inputs": rows}

    writeToJsonFile(fileName, inputsArray)

def generateStringInputs(fileName, numColumns, numRows, lowerBound, upperBound):
    totalInputs = numColumns * numRows

    chars = string.ascii_letters
    
    inputs = [(''.join(random.choice(chars) for i in range(random.randint(lowerBound, upperBound)))) for i in range(totalInputs)]
    
    rows = []

    for i in range(numRows):
        startIndex = i * numColumns
        endIndex = startIndex + numColumns
        rowInputs = inputs[startIndex:endIndex]
        rowString = ' '.join(map(str, rowInputs))
        rows.append(rowString)

    inputsArray = {"example_inputs": rows}

    writeToJsonFile(fileName, inputsArray)

def writeToJsonFile(fileName, inputsArray):
    with open(fileName, 'w') as file:
        json.dump(inputsArray, file)

def generateInputs(problemId, type, numColumns, numRows, lowerBound, upperBound):
    fileName = f"./problems/{problemId}/inputs.json"
    
    match type:
        case "int":
            generateIntInputs(fileName, numColumns, numRows, lowerBound, upperBound)
        case "string":
            generateStringInputs(fileName, numColumns, numRows, lowerBound, upperBound)

    print(f"Inputs generated and placed in {fileName}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate a number of test inputs to be used when generating training data of a function")
    parser.add_argument("problemId", type=str, help="ID of the problem to generate inputs for")
    parser.add_argument("type", type=str, help="The type of inputs to generate (int/string)")
    parser.add_argument("numColumns", type=int, help="Number of inputs (columns) per row")
    parser.add_argument("numRows", type=int, help="Number of rows of inputs")
    parser.add_argument("lowerBound", type=int, help="Minimum value of an input")
    parser.add_argument("upperBound", type=int, help="Maximum value of an input")
    args = parser.parse_args()

    generateInputs(args.problemId, args.type, args.numColumns, args.numRows, args.lowerBound, args.upperBound)