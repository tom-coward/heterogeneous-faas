import math

while True:
    try:
        data = list(map(int,input().split()))
        sum = data[0] + data[1]
        sum_size = int (math.log10(sum) + 1)
        print(sum_size)
    except:
        break;
