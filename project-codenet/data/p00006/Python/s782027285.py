string = input("input data")
size = len(string)
st = []
for i in range(size):
    st.append(string[size-i-1])

print(''.join(st))
