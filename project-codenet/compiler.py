import csv
import os
import random
import math


def meetsCriteria(row, language):
    return row["language"] == language and row["status"] == "Accepted"

def selectSolutions(selectedProblem, selectedLanguages, metadataDirectory, dataDirectory):
    selectedSolutions = {}

    for language in selectedLanguages:
        submissionIds = []

        # get list of Accepted submission IDs for the problem (from metadata csv)
        with open(os.path.join(metadataDirectory, selectedProblem + ".csv"), "r") as csvfile:
            reader = csv.DictReader(csvfile)

            for row in reader:
                if meetsCriteria(row, language):
                    submissionIds.append(row["submission_id"])

        # select random subset (5% - max 5) of Accepted submissions (submission_ids) to be used
        subsetSize = min(math.ceil(len(submissionIds) * 0.05), 15)
        selectedSubmissionIds = random.sample(submissionIds, subsetSize)

        # remove any files in ./data/{problemId} directory not in the list of Accepted submission IDs
        problemDirectory = os.path.join(dataDirectory, selectedProblem)
        for filename in os.listdir(problemDirectory):
            print(filename)
            # remove file if not in list of accepted submission_ids
            if filename[:-3] not in selectedSubmissionIds:
                    path = os.path.join(problemDirectory, filename)
                    print(path)
                    os.remove(path)

        selectedSolutions[selectedProblem] = selectedSubmissionIds

    return selectedSolutions


if __name__ == '__main__':
    selectedProblem = input("Enter problem ID: ")
    selectedLanguages = ["Python"]
    metadataDirectory = "./metadata"
    dataDirectory = "./data"

    selectSolutions(selectedProblem, selectedLanguages, metadataDirectory, dataDirectory)
    print("Solutions were selected - any others were removed")