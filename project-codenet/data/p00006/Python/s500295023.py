import sys

for line in sys.stdin:
    line = line.strip()
    print(line[::-1])