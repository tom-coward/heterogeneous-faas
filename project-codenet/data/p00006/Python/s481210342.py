from collections import deque
s=deque()
for i in raw_input():
    s.appendleft(i)
print(''.join(s))