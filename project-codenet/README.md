# Project CodeNet
Project CodeNet is a collection of submitted solutions to problems compiled by IBM. More details here: https://github.com/IBM/Project_CodeNet

I have taken a subset of these submitted solutions based on their associated problem (problems that would be expected to have increased time complexity corresponding to their input parameter size), taken any "Accepted" (valid) solutions and wrapped these in a function handler ready to be ran as a FaaS function; these compiled functions are then added to the Hetergeneous FaaS system for evaluation.

Solutions written in Python, Go and C# have been used. This provides a range between interpreted and compiled languages with differing traits.

## Selected problems
The following subset of problems from the Project CodeNet data repository has been selected:
- P00002: "Write a program which computes the digit number of sum of two integers a and b."
- 

Each problem has a number of solutions for each language (C#/Go/Python) - a subset of solutions for each language (5% or 5, whichever is lowest) are selected and then manually modified to be wrapped in a function handler.

## Preparing functions
To prepare functions for Heterogeneous FaaS, you can simply run `[python/python3] main.py compile`. This will randomly select a subset of accepted (valid) solutions from the problem metadata stored in `./metadata`, and remove the rest from the `./data` directory - you may then manually compile each of the selected solutions into a function file within the `./functions` directory.

`[python/python3] main.py upload` to upload any Docker containers within the `./functions` directory to the AWS ECR registry. These registry image URIs can then be passed to the Resource Manager API to create a function within the system.

`[python/python3] main.py generateInputs` can also be used to generate random test inputs given the input constraints of a problem - these inputs can then be passed as parameters to the function when generating training data (execution history) for functions. The test inputs are stored as a JSON array which can be easily copied into the request body when creating a function via. the Resource Manager API.
