package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

var sc = bufio.NewScanner(os.Stdin)

func main() {
	for sc.Scan() {
		s := strings.Split(sc.Text(), " ")
		a, _ := strconv.Atoi(s[0])
		b, _ := strconv.Atoi(s[1])
		fmt.Println(len(fmt.Sprint(a + b)))
	}
}

