import sys

for line in sys.stdin:
  a, b = line.strip().split(" ")
  print(len(str(int(a)+int(b))))

