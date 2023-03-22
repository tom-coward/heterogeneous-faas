import sys

for i in sys.stdin.readlines():
    print reversed(i.strip())