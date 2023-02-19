try:
    while True:
        a,b=[int(i) for i in input().split(" ")]
        c=a+b
        S=0
        while c>0:
            S+=1
            c=int(c/10)
        print(S)
except EOFError:
    pass

