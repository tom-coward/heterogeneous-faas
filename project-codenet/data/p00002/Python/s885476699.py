import sys
 
for line in sys.stdin:
    a, b = line.split()
    print(len(str(int(a) + int(b))))