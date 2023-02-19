while True:
    try:
        a, b = map(int,input().split())
        s = str(a + b)
        print(len(s))
    except EOFError:
        break

