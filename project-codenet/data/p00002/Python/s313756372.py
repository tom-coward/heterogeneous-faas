while True:
    try:
        a, b = (int(i) for i in map(int, input().split()))
        print(len(str(a+b)))
    except :
        break

