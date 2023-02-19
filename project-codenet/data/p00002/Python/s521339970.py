import sys

while True:
    data = sys.stdin.readline().strip()
    if data is None or data == '':
        break

    data = data.split(' ')
    num1 = int(data[0])
    num2 = int(data[1])
    sum = num1 + num2
    print(len(str(sum)))
    