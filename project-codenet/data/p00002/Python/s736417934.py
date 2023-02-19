import sys

for line in sys.stdin.readlines():
    i = line.rstrip().split(' ')
    j = int(i[0]) + int(i[1])
    a = str(j)
    print(len(a))
