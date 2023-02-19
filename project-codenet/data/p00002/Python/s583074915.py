data_set = []
while True:
    try:
        k = list(map(int, input().split()))
        data_set.append(k[0] + k[1])
    except:
        for i in data_set:
            print(len(str(i)))
        break