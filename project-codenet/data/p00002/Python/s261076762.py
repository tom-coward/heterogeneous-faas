import sys
if __name__ == '__main__':

	for line in sys.stdin:
		n,m = map(int,line.split())
		print(len(str(n+m)))


