s = raw_input()
l = list()
for i in xrange(len(s)):
	l.insert(0, s[i])
print "".join(l)