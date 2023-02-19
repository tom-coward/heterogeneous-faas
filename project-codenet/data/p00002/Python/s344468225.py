import sys

def digit(n):
	now = n
	cnt = 1
	while now / 10 != 0:
		cnt += 1
		now = now / 10
	return cnt

for line in sys.stdin:
	a, b = map(int, line.split())
	sum = a + b
	print digit(sum)
