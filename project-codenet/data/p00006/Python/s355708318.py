def main():
    s = list(input())
    for x in range(len(s) - 1, -1, -1):
        print(s[x], end = "")
    print()

if __name__ == "__main__":
    main()