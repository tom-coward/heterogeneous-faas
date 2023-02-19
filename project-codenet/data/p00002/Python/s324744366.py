while True:
    try:
        a,b=map(int,input().split())
    except:
        break
    sum=str(a+b)
    print(len(sum))