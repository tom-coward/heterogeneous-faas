import sys

for i in sys.stdin.readlines():
    print "".join(reversed(i.strip()))