n=list(input())
k=len(n)

for i in range(int(k/2)):
    n[i],n[k-i-1]=n[k-i-1],n[i]


for i in range(k):
    print(n[i], end="")
    
print("")
