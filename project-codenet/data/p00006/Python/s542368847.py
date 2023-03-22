import sys

for line in sys.stdin:
    print sorted(line, reverse=True)