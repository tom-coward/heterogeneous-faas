import sys 
for line in sys.stdin:
  a, b = line.split()
  a = int(a)
  b = int(b)
  c = a + b
  cnt = 0
  while c > 0:
    c //= 10
    cnt += 1
  print(cnt) 
