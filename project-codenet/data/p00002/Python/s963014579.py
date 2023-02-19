import sys

if __name__ == '__main__':

    nums = []

    for line in sys.stdin:
        if line == "\n":
            break
        else :
            nums.append([int(item) for item in line.split(" ")])

    for n in nums:
        sum = n[0] + n[1]
        print(len(str(sum)))
    