while True:
    try:
        list = input().split()
        a = int(list[0])
        b = int(list[1])
        print(len(str(a+b)))
    except:
        break

