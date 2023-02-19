while True:
 try:
  a, b = map(int, raw_input().strip().split(" "));
  print len(str(a + b))
 except:
  break;