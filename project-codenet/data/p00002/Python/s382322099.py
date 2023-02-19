import sys
for line in sys.stdin:
    a, b = [int(x) for x in line.split()]
    print(len(str(a+b)))
