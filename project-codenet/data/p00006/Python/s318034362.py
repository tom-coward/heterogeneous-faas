import sys

for str in sys.stdin:
    rev = ""
    for x in range(len(str),0,-1):
        rev=rev+str[x-1]
　　print rev