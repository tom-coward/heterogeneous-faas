package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
	"unicode/utf8"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	var strs []string
	var a, b, sum int64
	var digits int
	for scanner.Scan() {
		strs = strings.Split(scanner.Text(), " ")
		a, _ = strconv.ParseInt(strs[0], 10, 64)
		b, _ = strconv.ParseInt(strs[1], 10, 64)

		sum = a + b
		// goでlen(string)はbyte数が返る
		digits = utf8.RuneCountInString(strconv.FormatInt(sum, 10))

		fmt.Printf("%d\n", digits)
	}
}

