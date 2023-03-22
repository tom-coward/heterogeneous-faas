s=raw_input()
rev = ""
for x in xrange(len(s),0,-1):
    rev=rev+s[x-1]
print rev