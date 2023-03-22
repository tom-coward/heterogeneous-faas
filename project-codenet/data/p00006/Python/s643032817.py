n=str(input())
A=len(n)
N=list(n)
B=A//2
for i in range(B):
    N[i],N[A-1-i]=N[A-1-i],N[i]
C=''.join(N)
print(C)
