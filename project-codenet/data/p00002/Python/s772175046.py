ans = []
while True :
    try :
        s = input()
        a, b = map(int, s.split())
        ans.append(len(str(a + b)))
    except :
        for a in ans :
            print(a)
        break

