while True:
	try:
		n=list(map(int, input().split()))
	except EOFError:
		break
	a=n[0]+n[1]
	for i in range(0,10):
		if a/10**i<10:
			print(i+1)
			break

