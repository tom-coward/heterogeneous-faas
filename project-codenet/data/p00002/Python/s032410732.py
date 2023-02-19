sss = input().split()
while sss:
    a,b = int(sss[0]), int(sss[1])
    ab = a + b
    c = 1
    while ab // 10 > 0:
        c += 1
        ab //= 10
    print(c)
    try: sss = input().split()
    except EOFError: break
