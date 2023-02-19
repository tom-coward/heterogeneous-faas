package main

import (
  "fmt"
  "unicode/utf8"
)


func main() {
  var a int
  var b int
  for _, err := fmt.Scan(&a, &b); err == nil; _, err = fmt.Scan(&a, &b) {
    fmt.Println(utf8.RuneCountInString(fmt.Sprint(a+b)))
  }
}
