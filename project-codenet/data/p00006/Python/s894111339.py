while True:
    try:
        print(raw_input()[::-1])
    except EOFError: break