import sys

for line in sys.stdin:
    print sorted(line, reversed=True)