import sys
#from me.io import dup_file_stdin

#@dup_file_stdin
def solve():
    l=list(sys.stdin.readline())
    l.reverse()
    print(''.join(l))
 
solve()