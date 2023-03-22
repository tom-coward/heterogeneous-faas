import sys
line = sys.stdin.readline()
inp = line[0:-1]
str_list = list(inp)
str_list.reverse()
ret = "".join(str_list)
print (ret)