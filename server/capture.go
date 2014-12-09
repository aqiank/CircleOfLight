package main

import (
	"net/http"
	"os"
	"log"
	"github.com/jackyb/go-gphoto2"
)

func CaptureHandler(w http.ResponseWriter, r *http.Request) {
	go doCapture()
	w.WriteHeader(http.StatusOK)
}

func doCapture() error {
	var context *gp.Context
	var camera *gp.Camera
	var path gp.CameraFilePath
	var file *gp.CameraFile

	captured = false

	context = gp.NewContext()
	camera, err := gp.NewCamera()
	if err != nil {
		goto out
	}

	err = camera.Init(context)
	if err != nil {
		goto out
	}

	path, err = camera.Capture(gp.CAPTURE_IMAGE, context)
	if err != nil {
		goto out
	}

	file, err = camera.File(path.Folder, path.Name, gp.FILE_TYPE_NORMAL, context)
	if err != nil {
		goto out
	}

	_ = os.Remove(FILENAME)
	err = file.Save(FILENAME)
	if err != nil {
		goto out
	}

	err = camera.Free()
	if err != nil {
		goto out
	}

	captured = true
out:
	context.Free()
	if err != nil {
		log.Println(err)
	}
	return err
}
