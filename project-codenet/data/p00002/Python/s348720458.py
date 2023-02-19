import sys
for e in sys.stdin:
    a, b = map(int, e.split())
    print(len(str(a+b)))
