while 1:
    try:
        a,b = map(int,raw_input().split())
        print len(str(a+b))
    except:
        break