old = raw_input()
list = list(old)
list.reverse()
new = ''
for i in range(len(list)):
	new += list[i]
print new
