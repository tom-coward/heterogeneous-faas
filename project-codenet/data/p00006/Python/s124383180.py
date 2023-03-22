# -*- coding: utf-8 -*-
import sys

for s in sys.stdin:
    res = ""
    for i in range(len(s)):
        res += s[len(s)-i-1]
    print res