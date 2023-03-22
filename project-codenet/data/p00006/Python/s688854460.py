import sys

for line in sys.stdin:
    l = [ c for c in line.rstrip('\n') ]
    l.reverse()
    print ''.join(l)