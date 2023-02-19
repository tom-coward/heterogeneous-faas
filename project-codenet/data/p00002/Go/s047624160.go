package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func sliceAtoi(sa []string) ([]int, error) {
	si := make([]int, 0, len(sa))
	for _, a := range sa {
		i, err := strconv.Atoi(a)
		if err != nil {
			return si, err
		}
		si = append(si, i)
	}
	return si, nil
}

func getDigits(ary []int) (digits int) {
	sum := 0
	for _, v := range ary {
		sum += v
	}

	for sum != 0 {
		sum /= 10
		digits++
	}
	return
}

func getsTwoNumAsArray() (nums [][]int) {
	scanner := bufio.NewScanner(os.Stdin)

	for scanner.Scan() {
		str := scanner.Text()
		if str == "" {
			break
		}

		ary, _ := sliceAtoi(strings.Split(str, " "))
		nums = append(nums, ary)
	}

	return
}

func main() {
	ary := getsTwoNumAsArray()

	for _, v := range ary {
		fmt.Println(getDigits(v))
	}
}

