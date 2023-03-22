# -*- coding: utf-8 -*-

import sys

for line in sys.stdin.readlines():
    #List = map(int, line.strip().split())
    seq = line.split()
    print seq[::-1]