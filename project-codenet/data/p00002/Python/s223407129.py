while True:
    try:
        a, b = (int(x) for x in input().strip().split(' '))
        sum = a + b
        digit = 0

        while sum >= 1 :
            sum = sum / 10
            digit += 1

        print(digit)

    except EOFError:
        break

