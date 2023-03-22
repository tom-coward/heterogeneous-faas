
array = map(str, raw_input())


sum = ''
for i in range(len(array)):
    sum = sum + array[-(1+i)]

print sum