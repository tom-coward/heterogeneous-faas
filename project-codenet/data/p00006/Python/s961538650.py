import math
import itertools
x = 100
for _ in itertools.repeat(None,int(input())):
    x = math.ceil(x*1.05)
print(x*1000)