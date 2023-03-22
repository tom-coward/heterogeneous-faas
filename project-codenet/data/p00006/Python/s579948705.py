line = raw_input().strip()
print reduce(lambda a, b: a + b, reversed(line))