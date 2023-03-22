def rev_str(ls):
    rev = []
    rev += ls[::-1]
    return rev

if __name__ == '__main__':
    inp = input()
    inpls = list(inp)
    oupls = rev_str(inpls)
    oup = ''.join(oupls)
    print(oup)