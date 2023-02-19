while True:
  try:
    a, b = map(int, input().split())
    c = a + b
    cc = str(c)
    print(len(cc))
    
  except EOFError:
    break
