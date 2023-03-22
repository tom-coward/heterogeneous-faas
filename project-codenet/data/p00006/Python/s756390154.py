import sys

for line in sys.stdin:
    print sorted(line.rstrip('\n'), reverse=True)