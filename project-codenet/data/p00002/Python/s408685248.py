k=[]
try:
  while True:
    k.append(list(map(int,input().split())))
except EOFError:
  pass
for x,y in k:
  print(len(str(x+y)))

               

