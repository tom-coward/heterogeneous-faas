import sys

for line in sys.stdin:
    a, b = map(int, line.strip().split())
    print len(str(a+b))