# -*- coding:utf-8 -*-

s = str(input())
s = list(s)
x = [0]*len(s)
for i in range(len(s)):
    x[len(s)-i-1] = s[i]
for i in range(len(x)):
    print(x[i],end='')
print('')