import sys
if __name__ == '__main__':
    for line in sys.stdin:
        a, b = map(int, line.split())
        n = str(a + b)
        print(len(n))
