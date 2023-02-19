while True:
    try:
        a, b = raw_input().split()
        #print a, b
        print len(str(int(a)+int(b)))
    except EOFError:
        break