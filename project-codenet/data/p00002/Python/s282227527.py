def BigAdd(numa, numb):
	longer = (numa if len(numa) >= len(numb) else numb)
	shorter = (numa if len(numa) < len(numb) else numb)
	carry = 0
	s = ''
	for i in range(-1, -len(shorter) - 1, -1):
		curr = carry + int(longer[i]) + int(shorter[i])
		if curr >= 10:
			carry = 1
			s = str(curr - 10) + s
		else:
			carry = 0
			s = str(curr) + s
	for i in range(-len(shorter) - 1, -len(longer) - 1, -1):
		curr = carry + int(longer[i])
		if curr >= 10:
			carry = 1
			s = str(curr - 10) + s
		else:
			carry = 0
			s = str(curr) + s
	if carry == 1:
		s = '1' + s
	return s

while True:
	try:
		numa, numb = [item for item in input().split()]
		print(len(BigAdd(numa, numb)))
	except EOFError:
		break