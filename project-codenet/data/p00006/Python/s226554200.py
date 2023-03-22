while True:
	try:
		a = raw_input()
		print a[::-1]
	except EOFError:
		break