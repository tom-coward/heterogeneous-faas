import argparse

def main():
    l = []

    parser = argparse.ArgumentParser()
    parser.add_argument("filename", help="The filename to be processed")
    args = parser.parse_args()

    if args.filename:
        with open(args.filename) as f:
            str = f.read()
            str = str[::-1]
            print(str)

if __name__ == "__main__":
    main()
