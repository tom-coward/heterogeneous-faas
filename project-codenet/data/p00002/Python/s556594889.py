def keta(num):
    counter = 0
    while(1):
        num = num/10
        counter += 1
        if(num<1):
            break
    return counter
    
while(1):
    try:
        a,b = map(int, input().split())
        num=a+b
        print(keta(num))
    except:
        break
