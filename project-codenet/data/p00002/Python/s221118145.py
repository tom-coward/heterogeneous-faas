# coding: utf-8
# Here your code !

while 1:
    try:
        a,b = map(int,input().split())
        su = a+b
        print(len(str(su)))
    except:
        break