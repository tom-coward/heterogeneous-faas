text = raw_input("半角英数20文字まで入力: ")
n = len(text)
rev = []
for i in range(n):
    rev.append(text[(n-1)-i])
ans = ''.join(rev)
print ans