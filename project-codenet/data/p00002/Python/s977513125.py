try:
    while True:
        a, b = [int(x) for x in input().split()]
        print(len(str(a+b)))
    pass

except EOFError:
    pass

