import sys
for i in sys.stdin: print len(list(str(sum(map(int,i.split())))))
	