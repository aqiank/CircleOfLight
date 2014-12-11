package main

import (
	"time"
)

func generateFilename() string {
	const layout = "2-Jan-2006-15-04-05"
	now := time.Now()
	return "photo_" + now.Format(layout) + ".jpg"
}
