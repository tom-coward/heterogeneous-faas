while True:
    try:
        a, b = map(int, input().split())
    except:
        break
    print(len(str(a+b)))