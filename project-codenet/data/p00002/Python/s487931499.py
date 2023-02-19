import math

while True:
	try:
		data = map(int, raw_input().split())
		[a, b] = data
		print int(math.log10(a+b)+1)
	except (EOFError):
		break
