str = []
a = raw_input()
count = len(a)
str = list(a)
str_reverse = []
for i in range(0,count):
        str_reverse.append(str[count - i -1])
print ''.join(str_reverse)