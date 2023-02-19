while True :
    try :
        a, b = map(int, input().split())
    except EOFError :
        break
    
    x = a + b
    print(len(list(str(x))))

