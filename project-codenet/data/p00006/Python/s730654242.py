def main():
    while True:
        try:
            IN=input()
            if IN=='':
                break
            print(IN[::-1])

        except:
            break