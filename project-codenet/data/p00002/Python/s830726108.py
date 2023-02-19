while True:
    try:
        s = [int(x) for x in input().split()]
    
        print(len(str(s[0]+s[1])))
        
    except:
        break
