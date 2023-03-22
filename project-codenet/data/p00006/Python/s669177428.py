str = raw_input()
strlist = []
for x in str:
	strlist.append(x)
strlist.reverse()
revstr = ''
for x in strlist:
	revstr += x
print revstr