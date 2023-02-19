while True:
    try:
        l=list(map(int,input().split()))
        print(len(str(l[0]+l[1])))
    except:
        break
