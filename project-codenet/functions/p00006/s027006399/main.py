def main(input):
    return(input[::-1])

def handler(event, context):
    result = main(event)

    return result
