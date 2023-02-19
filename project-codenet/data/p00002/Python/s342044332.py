while True:    
    try:
        n=list(map(int,input().split()))
        print(len(str(sum(n))))
    except:
        break
