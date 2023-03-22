import sys

for line in sys.stdin:
    l = line.rstrip('\n').split('')
    l.reverse()
    print ''.join(l)