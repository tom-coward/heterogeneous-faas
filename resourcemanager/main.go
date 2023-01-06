/*
* HTTP server
 */

package main

import (
	"errors"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
)

func main() {
	http.HandleFunc("/", getRoot)

	err := http.ListenAndServe(":3333", nil)

	if errors.Is(err, http.ErrServerClosed) {
		fmt.Println("HTTP server closed!")
	} else if err != nil {
		fmt.Printf("\n!!! Server error: %v !!!\n", err)
	}
}

func getRoot(w http.ResponseWriter, r *http.Request) {
	// get request body
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		fmt.Printf("ERROR reading request body on GET /: %v", err)
	}

	fmt.Printf("got / request\n")
	io.WriteString(w, "hello world\n")
}
