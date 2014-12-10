package main

import (
	"net/http"
	"log"
)

func IndexHandler(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path[1:]
	http.ServeFile(w, r, path)
}

func main() {
	http.HandleFunc("/", IndexHandler)
	http.HandleFunc("/capture", CaptureHandler)
	http.HandleFunc("/check_capture", CheckCaptureHandler)
	http.HandleFunc("/email", EmailHandler)
	log.Fatal(http.ListenAndServe(":8001", nil))
}
