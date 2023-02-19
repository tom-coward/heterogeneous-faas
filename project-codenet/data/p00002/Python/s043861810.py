while True:
    try:
        a,b = input().split()
        a = int(a)
        b = int(b)
        print(len(str(a+b)))
    except:
        break
