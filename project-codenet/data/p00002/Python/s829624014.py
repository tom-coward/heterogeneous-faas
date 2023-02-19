import sys
from math import log10

for line in sys.stdin:
        a, b = map(int, line.split())
        print(int(log10(a+b)+1))