package main

import (
        "fmt"
        "os"
        "bufio"
        "strings"
        "strconv"
)

func main() {
        sc := bufio.NewScanner(os.Stdin)

        for sc.Scan() {
                digits := 0
                num := 0
                for _, s := range strings.Fields(sc.Text()) {
                        i, _ := strconv.Atoi(s)
                        num += i
                }
                for num > 0 {
                        digits += 1
                        num /= 10
                }
                fmt.Printf("%d\n", digits)
        }
}

