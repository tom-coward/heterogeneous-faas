package main

import (
	"fmt"
	"strconv"
)

func main() {
	result := make([]int, 0, 200)

	for {
		var a, b int
		scanCount, _ := fmt.Scanf("%d %d", &a, &b)
		if scanCount == 2 {
			result = append(result, a+b)
		} else {
			break
		}
	}

	for _, v := range result {
		length := len(strconv.Itoa(v))

		fmt.Println(length)
	}
}

