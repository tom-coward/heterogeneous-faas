def main(input):
    results = []
    
    for line in input:
            s=sum([int(num) for num in line.split(' ')])
            results.append(len(str(s)))

    return results

def handler(event, context):
    results = main(event)

    return results