import compiler
import uploader
import inputgenerator
import argparse
import writer


# select random solutions to selected problems (as defined by their ID below) in the chosen languages
def compile(selectedProblem):
    selectedLanguages = ["Python"]

    metadataDirectory = "./metadata"
    dataDirectory = "./data"

    compiler.selectSolutions(selectedProblem, selectedLanguages, metadataDirectory, dataDirectory)


# build the selected solutions into Docker containers and upload them to AWS ECR
def upload(solutionId):
    awsAccountId = "963689541346" 
    awsRegion = "eu-west-1"

    containerDirectory = f"./functions/{solutionId}"

    uploader.upload(containerDirectory, awsAccountId, awsRegion)

def generateInputs(problemId, numColumns, numRows, lowerBound, upperBound):
    fileName = f"./problems/{problemId}/inputs.json"
    
    inputgenerator.generateInputs(fileName, numColumns, numRows, lowerBound, upperBound)

def write():
    writer.writeAllToJson()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Compile solutions, upload functions to AWS ECR or generate random inputs for a problem solution")
    
    parser.add_argument("option", type=str, help="The operation to perform (compile/upload/write/generateInputs [problemId numColumns numRows lowerBound upperBound])")

    # optional upload arguments
    parser.add_argument("--solutionId", type=str, help="Solution ID of Docker container to upload to AWS ECR", required=False)

    # optional generateInputs arguments
    parser.add_argument("--problemId", type=str, help="ID of the problem to generate inputs for", required=False)
    parser.add_argument("--numColumns", type=int, help="Number of inputs (columns) per row", required=False)
    parser.add_argument("--numRows", type=int, help="Number of rows of inputs", required=False)
    parser.add_argument("--lowerBound", type=int, help="Minimum value of an input", required=False)
    parser.add_argument("--upperBound", type=int, help="Maximum value of an input", required=False)

    args = parser.parse_args()

    match args.option:
        case "compile":
            compile()
        case "upload":
            upload(args.solutionId)
        case "write":
            write()
        case "generateInputs":
            generateInputs(args.problemId, args.numColumns, args.numRows, args.lowerBound, args.upperBound)
        case _:
            print("Invalid option")