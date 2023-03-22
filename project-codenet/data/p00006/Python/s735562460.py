import sys

rawinput = raw_input();

strlist = []

for temp in rawinput:
    strlist.append(temp)

strlist.reverse()

for temp in strlist:
    sys.stdout.write(temp)