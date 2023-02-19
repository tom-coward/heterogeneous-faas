while (1):
	try:
		a, b = map(int, input().split())
	except:
		break
	c = a+b
	print( len(str(c)) )
	