def funk():
    a,b=map(int,input().split())
    print(len(str(a+b)))
 
while True:
    try:
        funk()
    except:
        break
