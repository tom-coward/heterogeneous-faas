line = raw_input()
lis = ['' for i in range(len(line))]
for i in range(len(line)-1, -1, -1):
    lis[len(line)-1-i] = line[i]
else:
    print ''.join(lis)