# coding: utf-8

line = input()
for k in range(0, len(line)):
    print(line[len(line) - k - 1], end="")
print("")