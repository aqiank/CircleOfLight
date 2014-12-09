package com.bartleboglehegarty.circleoflight;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class ImgurResponse {
    public Data data;
    public boolean success;
    public int status;

    public class Data {
        public String link;
    }

    public static ImgurResponse parse(byte[] data) {
        Gson gson = new Gson();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        JsonReader r = new JsonReader(new InputStreamReader(in));
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(r);
        return gson.fromJson(e, ImgurResponse.class);
    }
}
