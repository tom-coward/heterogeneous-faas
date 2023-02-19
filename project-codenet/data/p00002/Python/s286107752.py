while True:
    try:
        a,b = raw_input().split()
        print len(str(int(a)+int(b)))
    except EOFError:
        break