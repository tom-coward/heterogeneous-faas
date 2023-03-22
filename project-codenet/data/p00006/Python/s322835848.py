a=[]
str=str(input())
for i in range(len(str)):
    b=str[-(i+1)]
    a.append(b)
a=''.join(a)
print(a)
