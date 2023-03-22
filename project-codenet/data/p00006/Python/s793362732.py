import sys

for line in sys.stdin:
    print ''.join(sorted(line.rstrip('\n'), reverse=True)),