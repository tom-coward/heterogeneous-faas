def digit_check(n):
    digit = 1
    
    while int(n/(10**digit)) != 0:
        digit += 1

    return digit
        
def main(input):
    results = []
    for line in input:
        ls = list(map(int, line.split(' ')))
        results.append(digit_check(ls[0]+ls[1]))
    
    return results

def handler(event, context):
    return main(event)

if __name__ == '__main__':
    data = ["81 404", "605 444"]

    print(handler(data, None))