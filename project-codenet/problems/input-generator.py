import random
import argparse

def generateInputs(fileName, numColumns, numRows, lowerBound, upperBound):
    totalInputs = numColumns * numRows

    inputs = [random.randint(lowerBound, upperBound) for i in range(totalInputs)]
    
    with open(fileName, 'w') as inputFile:
        for i in range(numRows):
            startIndex = i * numColumns
            endIndex = startIndex + numColumns
            rowInputs = inputs[startIndex:endIndex]
            rowString = ' ' . join(map(str, rowInputs)) + '\n'

            inputFile.write(rowString)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate a number of test inputs to be used when generating training data of a function")
    parser.add_argument("problemId", type=str, help="ID of the problem to generate inputs for")
    parser.add_argument("numColumns", type=int, help="Number of inputs (columns) per row")
    parser.add_argument("numRows", type=int, help="Number of rows of inputs")
    parser.add_argument("lowerBound", type=int, help="Minimum value of an input")
    parser.add_argument("upperBound", type=int, help="Maximum value of an input")
    args = parser.parse_args()

    fileName = f"./{args.problemId}/inputs.txt"
    
    generateInputs(fileName, args.numColumns, args.numRows, args.lowerBound, args.upperBound)

    print(f"Inputs generated and placed in ./{args.problemId}/inputs.txt")
