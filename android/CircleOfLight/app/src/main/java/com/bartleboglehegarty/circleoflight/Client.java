package com.bartleboglehegarty.circleoflight;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Client {
	public static final String BASE_URL = "http://192.168.0.102:8001";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteUrl(url), params, handler);
	}

	public static void getFile(String url, FileAsyncHttpResponseHandler handler) {
		client.get(getAbsoluteUrl(url), handler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
