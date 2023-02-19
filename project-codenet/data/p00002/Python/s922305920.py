from math import log10, ceil
while True:
 try:
  a=[int(item) for item in input().split()]
  b=a[0]+a[1]
  print(len(str(b)))
 except EOFError:break
