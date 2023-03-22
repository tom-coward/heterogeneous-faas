import sys

s = input()
for i in range(len(s)):
	sys.stdout.write(s[len(s)-i-1])
print("")