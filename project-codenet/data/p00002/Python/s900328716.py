import sys
q = sys.stdin.readlines()
for line in q:
    a, b = map(int, line.split())
    print(len(str(a + b)))