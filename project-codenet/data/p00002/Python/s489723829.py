import sys
for line in sys.stdin:
    nums = list(map(int,line.split()))
    print(len(str(sum(nums))))