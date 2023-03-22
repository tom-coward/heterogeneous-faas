string = raw_input()

tmp = ""
for i in xrange(len(string)):
    tmp += string[len(string) - i - 1]
    
print tmp