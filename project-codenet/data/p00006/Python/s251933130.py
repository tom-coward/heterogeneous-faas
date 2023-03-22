line = input()

ret = "";
for i in range(1,len(line)):
	ret += line[len(line)-1-i];

print(ret)