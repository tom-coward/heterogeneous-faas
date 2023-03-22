#coding: utf-8    
while 1:
    try:
        s = raw_input()
        print s[::-1]
    except EOFError:
        break