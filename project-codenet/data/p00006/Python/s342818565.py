import sys

def get_revstr(s):
    s_len = len(s)
    
    if s_len == 1:
        return s
    else:
        return s[-1] + get_revstr(s[:-1])
    
def main():
    s = sys.stdin.readline().strip()
    rev_s = get_revstr(s)
    print(rev_s)

if __name__ == '__main__':
    main()
    