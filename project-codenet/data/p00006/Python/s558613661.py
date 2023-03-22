text = raw_input()
n = len(text)
rev = []
for i in range(n):
    rev.append(text[(n-1)-i])
ans = ''.join(rev)
print ans