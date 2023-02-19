import sys

array = []
for line in sys.stdin:
    array.append(map(int, line.split(' ')))

for a, b in array:
    digit = 0
    c = a + b
    while c > 0:
        c = int(c / 10)
        digit = digit + 1
    
    print(digit)
