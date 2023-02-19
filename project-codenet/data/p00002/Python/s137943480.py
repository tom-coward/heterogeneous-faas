import sys

for line in sys.stdin:
    a, b = list(map(int, line.split()))

    s=len(str(a+b))
    
    print(s)
    