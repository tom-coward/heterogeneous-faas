# coding: utf-8

def digit_check(n):
    digit = 1
    
    while int(n/(10**digit)) != 0:
        digit += 1

    return digit
        
def main(input):
    for line in input:
        ls = list(map(int, line.split(' ')))
        return digit_check(ls[0]+ls[1])

def handler(event, context):
    return main(event['data'])