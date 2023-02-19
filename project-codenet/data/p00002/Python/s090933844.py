while(1):
    try:
        a,b=map(int,input().split(" "))
        sum=a+b
        length=len(str(sum))
        print(length)
    except EOFError:
        break
