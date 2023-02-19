while True:
    try:
        a, b = map(int, input().split())
    except:
        break
    string = str(a+b)
    print(len(string))