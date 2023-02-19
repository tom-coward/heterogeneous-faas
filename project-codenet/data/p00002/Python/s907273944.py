for i in range(1, 200):

    try:
        str1 = input()
        list = str1.split(" ")
        result = len(str(int(list[0]) + int(list[1])))
        print(result)
    except:
        break
