package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"strconv"
)

var scanner = bufio.NewScanner(os.Stdin)

func nextString() string {
	scanner.Scan()
	return scanner.Text()
}

func nextInt() (int, error) {
	return strconv.Atoi(nextString())

}

func main() {
	scanner.Split(bufio.ScanWords)

	for {
		a, err := nextInt()
		if err != nil {
			break
		}
		b, _ := nextInt()

		s := a + b
		nDigits := int(math.Log10(float64(s))) + 1
		fmt.Println(nDigits)
	}
}

