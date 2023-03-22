import sys
str = ""
for l in sys.stdin:
    str = l
    print(str[-2::-1])
