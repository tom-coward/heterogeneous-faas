while True:
    try:
        list1 = [int(i) for i in input().split()]
        print(len(str(list1[0] + list1[1])))
    except:
        break;
