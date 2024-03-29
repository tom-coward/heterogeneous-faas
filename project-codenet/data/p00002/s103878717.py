# coding: utf-8

import sys

def digit_check(n):
    digit = 1
    
    while int(n/(10**digit)) != 0:
        digit += 1

    return digit
        
def main():
    for line in sys.stdin:
        ls = list(map(int, line.split(' ')))
        print(digit_check(ls[0]+ls[1]))

if __name__ == '__main__':
    main()