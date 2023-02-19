while True:
    try:
        a, b = map(int, input().split())
        print(len(str(a+b)))
    except:
        exit(0)

