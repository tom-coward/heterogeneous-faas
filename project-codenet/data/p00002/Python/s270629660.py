import sys

for line in sys.stdin:
 n =map(int,line.split())
 a = n[0]+n[1]
 print len(str(a))