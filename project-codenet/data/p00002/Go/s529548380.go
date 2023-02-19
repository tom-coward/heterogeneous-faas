// 九九を出すだけ
package main
import (
	"fmt"
	"os"
	"bufio"
	"strconv"
)

var sc = bufio.NewScanner(os.Stdin)
var out = bufio.NewWriter(os.Stdout)

func main(){
	sc.Split(bufio.ScanWords)
	defer out.Flush()
	for sc.Scan(){
	    s := nextIntIs()
	    sc.Scan()
	    t := nextIntIs()
        a := len(strconv.Itoa(s+t))
	    fmt.Fprintln(out, a)
	}
}

func next() string{
	sc.Scan()
	return sc.Text()
}

func nextInt() int{
	a, _ := strconv.Atoi(next())
	return a
}

func nextIntIs() int{
	a, _ := strconv.Atoi(sc.Text())
	return a
}
