import math
while True:
    try:

        num1, num2 = map(int, input().split())
        sum_number = num1 + num2

        ans = int(math.log10(sum_number) + 1)
        print(ans)

    except:
        break
    