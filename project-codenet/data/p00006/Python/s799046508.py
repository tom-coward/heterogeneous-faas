input_line1 = input()

length = len(input_line1)
result = ''

for i in range(length):
    result += input_line1[(i * -1)-1]
    
print(result)    