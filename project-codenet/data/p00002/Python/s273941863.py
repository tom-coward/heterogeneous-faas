import sys

for line in sys.stdin:
    ar = line.split(" ")
    print(len(str(int(ar[0]) + int(ar[1]))))

