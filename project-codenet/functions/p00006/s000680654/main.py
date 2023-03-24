def main(input):
    a=list(str(input))
    a.reverse()
    return a

def handler(event, context):
    result = main(event)

    return result
