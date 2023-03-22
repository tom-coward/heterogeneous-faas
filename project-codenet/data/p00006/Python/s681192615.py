import sys

def gcd(a,b):
	a,b=max(a,b),min(a,b)
	while a%b:
		a,b=b,a%b
	return b
	
def lcm(a,b):
	return a//gcd(a,b)*b

s=input()
print(s[::-1])
#for i in sys.stdin:
#	a,b=map(int,i.split())
#	print(gcd(a,b),lcm(a,b))