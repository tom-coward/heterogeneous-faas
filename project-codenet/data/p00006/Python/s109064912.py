def rev(str):
    revstr=''
    for i in range(len(str)-1,-1,-1):
        revstr=revstr + str[i]
    return revstr
str=input("")
print(rev(str))