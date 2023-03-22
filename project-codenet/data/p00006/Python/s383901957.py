import sys
str_sequence = input()
if len(str_sequence) > 20:
    sys.exit(1)
print(str_sequence[::-1])