chars = list(input())
outLen = ""
for i in range(1, len(chars) + 1):
    outLen = outLen + chars[i * -1]
print(outLen)
