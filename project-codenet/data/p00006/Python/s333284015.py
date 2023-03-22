import sys

s = (str)(raw_input())
a = []
for i in range(0,len(s)):
    a.append(s[i]);
a.reverse()

for i in a:
    sys.stdout.write(i)
print("")