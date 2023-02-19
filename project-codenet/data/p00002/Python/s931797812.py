# AOJ 0002 Digit Number
# Python3 2018.6.7 bal4u

while True:
    try: print(len(str(sum(list(map(int, input().split()))))))
    except EOFError: break
