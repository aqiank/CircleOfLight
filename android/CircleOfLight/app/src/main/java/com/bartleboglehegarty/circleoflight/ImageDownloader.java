package com.bartleboglehegarty.circleoflight;

import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloader extends AsyncTask<String, Void, String> {
    private ImageView image;
    public ImageDownloader(ImageView image) {
        super();
        this.image = image;
    }

    @Override
    protected String doInBackground(String... params) {
        try {

        } catch (Exception e) {
            // Ignore
        }
        return params[0];
    }
}