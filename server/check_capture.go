
package main

import (
	"net/http"
)

func CheckCaptureHandler(w http.ResponseWriter, r *http.Request) {
	captured := doCheckCapture()
	if captured {
		w.Write([]byte{1})
	} else {
		w.Write([]byte{0})
	}
}

func doCheckCapture() bool {
	return captured
}
