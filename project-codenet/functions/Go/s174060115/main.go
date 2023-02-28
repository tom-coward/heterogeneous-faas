package main

import (
	"github.com/aws/aws-lambda-go/lambda"
	"bufio"
    "os"
    "strconv"
    "strings"
)

func function() (int, error) {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		row := strings.Split(scanner.Text(), " ")
		var sum int

		for _, v := range row {
			num, _ := strconv.Atoi(v)
			sum += num
		}

		return len([]rune(strconv.Itoa(sum))), nil
	}
}

func main() {
	lambda.Start(function)
}