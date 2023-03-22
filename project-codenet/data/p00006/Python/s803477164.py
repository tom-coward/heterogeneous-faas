# coding: utf-8

str = raw_input()
ans = ""
for i in range(len(str)):
	ans += str[-i-1]
print ans