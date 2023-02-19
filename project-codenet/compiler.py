import csv
import os

selected_problems = ["p00002"]

accepted_languages = ["C#", "Go", "Python"]

metadata_directory = "./metadata"

def meets_criteria(row):
    return row["language"] in accepted_languages and row["status"] == "Accepted"

submission_ids = []

# Iterate through each selected problem
for problem in selected_problems:
    # Get list of Accepted submission IDs for the problem (from metadata csv)
    with open(os.path.join(metadata_directory, problem + ".csv"), "r") as csvfile:
        reader = csv.DictReader(csvfile)

        for row in reader:
            if meets_criteria(row):
                submission_ids.append(row["submission_id"])


    data_directory = "./data"

    # Remove any files in ./data directory not in the list of Accepted submission IDs
    for language in accepted_languages:
        language_data_directory = os.path.join(data_directory, problem, language)

        for filename in os.listdir(language_data_directory):
            if filename[:-3] not in submission_ids:
                    os.remove(os.path.join(language_data_directory, filename))