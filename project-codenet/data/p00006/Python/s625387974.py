import sys
#from me.io import dup_file_stdin

#@dup_file_stdin
def solve():
    print(''.join(reversed(list(sys.stdin.read()))))
 
solve()