try:
    while True:
        a, b = list(map(int, input().split()))
        print(len(str(a + b)))

except Exception as ex:
    pass
