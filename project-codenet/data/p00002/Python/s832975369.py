import sys
try:
    while True:
        a, b = list(map(int, input().split()))
        c = str(a + b)
        print(len(c))
except Exception:
    sys.exit()
