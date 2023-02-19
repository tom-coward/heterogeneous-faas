def degit(num):
    count = 0
    while num > 0:
        num //= 10
        count += 1
    return count

a = []
b = []
count = 0

while True:
    try:
        buff = input().split()
        a.append(int(buff[0]))
        b.append(int(buff[1]))
        count += 1
    except:
        break

for i in range(count):
    print(degit(a[i]+b[i]))
