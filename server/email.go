package main

import (
	"os"
	"io/ioutil"
	"net/smtp"
	"net/http"
	"encoding/base64"
	"log"
)

const EmailContent = 
`From: Circle of Light
To: aqiank@gmail.com
Subject: Test
MIME-Version: 1.0
Content-Type: multipart/mixed; boundary=frontier

This is a message with multiple parts in MIME format.
--frontier
Content-Type: text/plain

Here's your picture taken by Circle Of Light!
--frontier
Content-Type: application/octet-stream
Content-Transfer-Encoding: base64
Content-Disposition: attachment; filename="` + FILENAME + `"

`


func EmailHandler(w http.ResponseWriter, r *http.Request) {
	err := doEmail(r)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(err.Error()))
	}

	w.WriteHeader(http.StatusOK)
}

func doEmail(r *http.Request) error {
	auth := smtp.PlainAuth("", "bbh.circleoflight@gmail.com", "t3lkomnet", "smtp.gmail.com")
	to := []string{r.FormValue("to")}
	
	// Create Email
	msg, err := createEmail(FILENAME)
	if err != nil {
		goto out
	}

	// Send Email
	err = smtp.SendMail("smtp.gmail.com:587", auth, "bbh.circleoflight@gmail.com", to, msg)
	if err != nil {
		goto out
	}
out:
	if err != nil {
		log.Println(err)
	}
	return err
}

func createEmail(filename string) ([]byte, error) {
	var file *os.File
	var fileData []byte
	var encData []byte
	var msgData []byte

	// Open File
	file, err := os.Open(filename)
	if err != nil {
		goto out
	}

	// Read File Data
	fileData, err = ioutil.ReadAll(file)
	if err != nil {
		goto out
	}

	// Encode Data in Base64
	encData = make([]byte, base64.StdEncoding.EncodedLen(len(fileData)))
	base64.StdEncoding.Encode(encData, fileData)

	// Write Mesage
	msgData = []byte(EmailContent)
	msgData = append(msgData, encData...)
	msgData = append(msgData, []byte("\n--frontier--")...)
out:
	if err != nil {
		log.Println(err)
	}
	return msgData, err
}
