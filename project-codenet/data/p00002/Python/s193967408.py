while True:
    try:
        a, b = map(int,input().split())
        sum = a + b
        i = 0
        while i >= 0:
            c = 10**i
            if sum % c == sum:
                break
            else:
                i += 1
        print(i)
    except EOFError:
        break

