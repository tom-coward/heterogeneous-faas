# Aizu Problem 0006: Reverse Sequence
#
import sys, math, os

# read input:
PYDEV = os.environ.get('PYDEV')
if PYDEV=="True":
    sys.stdin = open("sample-input.txt", "rt")


print(input().strip()[::-1])