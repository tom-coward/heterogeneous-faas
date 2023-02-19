while True:
    try:
        a,b=map(int,input().split())
        a+=b
        print(len(str(a)))
    except:
        break

