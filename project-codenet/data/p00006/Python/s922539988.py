# -*- coding: utf-8 -*-

import sys

for line in sys.stdin.readlines():
    #List = map(int, line.strip().split())
    
    seq = line.strip()
    print seq[::-1]