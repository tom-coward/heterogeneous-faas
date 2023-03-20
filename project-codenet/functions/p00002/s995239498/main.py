def main(input):
    results = []
    
    for line in input:
        try:
            a, b = map(int, line.split())
            s = str(a + b)
            results.append(len(s))
        except EOFError:
            break

    return results

def handler(event, context):
    results = main(event)

    return results


if __name__ == '__main__':
    data = ["81 404", "605 444"]

    print(handler(data, None))