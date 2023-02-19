while(1):
    try:
        a, b= (int(i) for i in input().split())
        print(len(str(eval('a+b'))))
    except:break
