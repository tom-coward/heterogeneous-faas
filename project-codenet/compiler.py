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
        with open(os.path.join(metadataDirectory, selectedProblem + ".csv"), "w") as csvfile:
            reader = csv.DictReader(csvfile)

            for row in reader:
                if meetsCriteria(row, language):
                    submissionIds.append(row["submission_id"])

        # select random subset (5% - max 5) of Accepted submissions (submission_ids) to be used
        subsetSize = min(math.ceil(len(submissionIds) * 0.05), 5)
        selectedSubmissionIds = random.sample(submissionIds, subsetSize)

        # remove any files in ./data directory not in the list of Accepted submission IDs
        languageDataDirectory = os.path.join(dataDirectory, selectedProblem, language)

        for filename in os.listdir(languageDataDirectory):
            # remove file if not in list of accepted submission_ids
            if filename[:-3] not in selectedSubmissionIds:
                    os.remove(os.path.join(languageDataDirectory, filename))

        selectedSolutions[selectedProblem] = selectedSubmissionIds

    return selectedSolutions