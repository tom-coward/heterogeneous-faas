import sys

lines = []
for line in sys.stdin:
    print(line[::-1])