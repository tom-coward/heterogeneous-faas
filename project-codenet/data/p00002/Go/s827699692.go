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
	//sc.Split(bufio.ScanWords)
	for sc.Scan() {
		s := strings.Split(sc.Text(), " ")
		n1, _ := strconv.Atoi(s[0])
		n2, _ := strconv.Atoi(s[1])
		fmt.Printf("%d\n", len(strconv.Itoa(n1+n2)))
	}
}

