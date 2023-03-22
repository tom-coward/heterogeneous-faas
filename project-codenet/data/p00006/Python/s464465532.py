# Reverse Sequence

import sys

string = ""

for line in sys.stdin:
	string = line

print string[::-1]