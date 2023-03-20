def solve(input):
    a,b=map(int, input.split())
    return len(str(a+b))

def handler(event, context):
    results = []
    
    for i in range(len(event)):
        results.append(solve(event[i]))

    return results


if __name__ == '__main__':
    data = ["81 404", "605 444"]

    print(handler(data, None))