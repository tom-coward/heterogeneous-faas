package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	sc := bufio.NewScanner(os.Stdin)
	for sc.Scan() {
		str := strings.Split(sc.Text(), " ")
		if len(str) != 2 {
			return
		}
		a, err1 := strconv.ParseInt(str[0], 10, 64)
		b, err2 := strconv.ParseInt(str[1], 10, 64)
		if err1 != nil || err2 != nil {
			return
		}
		a += b
		for b = 0; a > 0; a /= 10 {
			b++
		}
		fmt.Println(b)
	}
}

