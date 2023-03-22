
import sys


def str_reverse(str):
    r = []
    for c in str:
        r.insert(0, c)
    return "".join(r)


str = sys.stdin.readline().rstrip("\r\n")

print str_reverse(str)