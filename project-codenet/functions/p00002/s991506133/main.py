import sys

def main(input):
    results = []
    
    for line in input:
        if not line:
            break
        token = list(map(int, line.strip().split()))
        results.append(len(str(token[0] + token[1])))

    return results

def handler(event, context):
    results = main(event)

    return results