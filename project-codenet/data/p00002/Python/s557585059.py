import math
while 1:
    try:
        a, b = map(int,raw_input().split())
    except EOFError:
        break
    print int(math.log10(a+b)+1)