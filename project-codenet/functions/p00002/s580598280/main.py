def main(input):
    results = []
    
    for line in input:
            s=sum([int(num) for num in line.split(' ')])
            results.append(len(str(s)))

    return results

def handler(event, context):
    results = main(event)

    return results


if __name__ == '__main__':
    data = ["81 404", "605 444"]

    print(handler(data, None))