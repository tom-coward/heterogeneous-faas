from sys import stdin
for a in stdin :
    b = a.strip().split()
    b = sum([int(i) for i in b])
    c = 1
    while int(b/10) > 0:
        b = int(b/10)
        c += 1
    print(c)
