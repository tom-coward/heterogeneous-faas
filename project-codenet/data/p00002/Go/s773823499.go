package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
)

var sc = bufio.NewScanner(os.Stdin)

func Sliceconvtoi() []int {
	strs := strings.Split(sc.Text(), " ")

	var ints []int
	var x int

	for _, s := range strs {
		x, _ = strconv.Atoi(s)
		ints = append(ints, x)
	}

	return ints
}

func main() {
	for sc.Scan() {
		i := Sliceconvtoi()
		fmt.Println(int(math.Log10(float64(i[0]+i[1]))) + 1)
	}

}

