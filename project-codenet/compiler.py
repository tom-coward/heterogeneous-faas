import csv
import os
import random
import math


selectedProblems = ["p00002"]
acceptedLanguages = ["C#", "Go", "Python"]

metadataDirectory = "./metadata"
dataDirectory = "./data"


def meetsCriteria(row, language):
    return row["language"] == language and row["status"] == "Accepted"

def selectSolutions():
    selectedSolutions = []

    # Iterate through each selected problem
    for problem in selectedProblems:
        for language in acceptedLanguages:
            submissionIds = []

            # Get list of Accepted submission IDs for the problem (from metadata csv)
            with open(os.path.join(metadataDirectory, problem + ".csv"), "r") as csvfile:
                reader = csv.DictReader(csvfile)

                for row in reader:
                    if meetsCriteria(row, language):
                        submissionIds.append(row["submission_id"])


            # Select random subset (5% - max 5) of Accepted submissions (submission_ids) to be used
            subsetSize = min(math.ceil(len(submissionIds) * 0.05), 5)
            selectedSubmissionIds = random.sample(submissionIds, subsetSize)

            # Remove any files in ./data directory not in the list of Accepted submission IDs
            languageDataDirectory = os.path.join(dataDirectory, problem, language)

            for filename in os.listdir(languageDataDirectory):
                # remove file if not in list of accepted submission_ids
                if filename[:-3] not in selectedSubmissionIds:
                        os.remove(os.path.join(languageDataDirectory, filename))

            selectedSolutions[problem] = selectedSubmissionIds

    return selectedSolutions

def prepareSolutionFunctions(selectedSolutions):
    


if __name__ == "__main__":
    selectedSolutions = selectSolutions() # select random subset of Accepted solutions for each selected problem

    prepareSolutionFunctions(selectedSolutions) # wrap selected solutions in function handlers to prepare functions
