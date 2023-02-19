import sys
l=[[int(i) for i in l.split()] for l in sys.stdin]
for i in l:
    print(len(str(i[0]+i[1])))
