output = []
while True:
    try :
        a,b = map(int, input().split())
    except :
        break
    output.append(len(str(a+b)))
for x in range(len(output)):
    print(output[x])
