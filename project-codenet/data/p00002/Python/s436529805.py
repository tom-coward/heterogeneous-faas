import sys

for line in sys.stdin:
    a, b = [int(_) for _ in line.split()]
    print(len(str(a+b)))

