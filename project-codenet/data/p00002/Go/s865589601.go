package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		i := scanner.Text()
		var total int
		inputNum := strings.Fields(i)
		for _, v := range inputNum {
			num, err := strconv.Atoi(v)
			if err != nil {
				break
			}
			total += num
		}
		fmt.Println(DigitCount(strconv.Itoa(total)))
	}
}

func DigitCount(str string) int {
	return len(str)
}

