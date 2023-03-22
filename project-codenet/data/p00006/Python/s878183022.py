str = []
a = raw_input()
count = len(a)
str = list(a)
for i in range(0,count):
        str[i] = str[count - i -1]

print ''.join(str)