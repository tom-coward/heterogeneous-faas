st = input()
ans = ""
for i in range(len(st)):
    ans += st[len(st)-i-1]
print(ans)