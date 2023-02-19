ans = []

while(1):
    try:
        a, b = map(int, input().split())
    except:
        break

    s = str(a + b)
    cnt = len(s)
    ans.append(cnt)

for a in ans:
    print(a)

