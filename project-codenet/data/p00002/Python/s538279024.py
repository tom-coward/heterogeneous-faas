import sys
    
while True:
    try:
        a,b = map(int, raw_input().split())
    except:
        break
    
    print len(str(a+b))