while True:
    try:
        a, b = [int(i) for i in raw_input().split()]
        print len(str(a + b))
    except EOFError:
        break