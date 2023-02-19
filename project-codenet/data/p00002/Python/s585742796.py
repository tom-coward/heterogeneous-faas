import sys

for line in sys.stdin: print(len(str(sum(map(int,line.split())))))