string = list(input())
rev_str = [0 for i in range(len(string))]

for i in range(len(string)):
    rev_str[i] = string[len(string) - i - 1]
   
print("".join(rev_str))