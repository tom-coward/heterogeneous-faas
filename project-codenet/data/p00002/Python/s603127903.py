import sys
for v in sys.stdin:
    a,b = map(int, v.split())
    print len(str(a+b))