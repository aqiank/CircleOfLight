package com.bartleboglehegarty.circleoflight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SocialAdapter extends ArrayAdapter<Integer> {
	public static final String TAG = "SocialAdapter";

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static Bitmap bitmap = null;
	
	public SocialAdapter(Context context, int resource) {
		super(context, resource);
	}

	public static int[] ICONS = {
		R.drawable.fb,
		R.drawable.wechat,
		R.drawable.email
	};

	@Override
	public int getCount() {
		return ICONS.length;
	}

	@Override
	public Integer getItem(int i) {
		return ICONS[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View reuseableView, ViewGroup viewGroup) {
		ImageView view = null;

		if (reuseableView != null) {
			view = (ImageView) reuseableView;
		} else {
			LayoutParams params = new LayoutParams(128, 128);
			view = new ImageView(viewGroup.getContext());
			view.setImageResource(ICONS[i]);
			view.setLayoutParams(params);
			if (i == 0)
				view.setOnClickListener(facebookListener);
			else if (i == 1)
				view.setOnClickListener(wechatListener);
			else if (i == 2)
				view.setOnClickListener(emailListener);
		}

		return view;
	}

	private View.OnClickListener facebookListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			// start Facebook Login
			Session.openActiveSession((Activity) getContext(), true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (state.isOpened())
						uploadFacebook(); // publishFacebookPost();
				}
			});
		}
	};

	private View.OnClickListener wechatListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			uploadToImgur();
		}
	};

	private View.OnClickListener emailListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			sendEmail();
		}
	};

	private void publishFacebookPost() {
		Bundle params = new Bundle();
		params.putString("name", "Facebook SDK for Android");
		params.putString("caption", "Build great social apps and get more installs.");
		params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		params.putString("link", "https://developers.facebook.com/android");
		params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getContext(), Session.getActiveSession(), params))
			.setOnCompleteListener(new WebDialog.OnCompleteListener() {
				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
						final String postId = values.getString("post_id");
						if (postId != null)
							Toast.makeText(getContext(), "Posted story, id: " + postId, Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getContext().getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
					} else if (error instanceof FacebookOperationCanceledException) {
						Toast.makeText(getContext().getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getContext().getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
					}

					Session.getActiveSession().closeAndClearTokenInformation();
				}
			})
			.build();
		feedDialog.show();
	}

	private void uploadFacebook() {
		Bundle params = new Bundle();
		Bitmap bitmap = BitmapFactory.decodeFile(StepFiveFragment.imagePath);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		params.putByteArray("source", out.toByteArray());

		new Request(
			Session.getActiveSession(),
			"/me/photos",
			params,
			HttpMethod.POST,
			new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					Toast.makeText(getContext(), "Successfully uploaded photo to Facebook!", Toast.LENGTH_SHORT).show();
					Session.getActiveSession().closeAndClearTokenInformation();
				}
			}
		).executeAsync();
	}

	private void loginToTwitter() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(getContext().getString(R.string.consumer_key));
				builder.setOAuthConsumerSecret(getContext().getString(R.string.consumer_secret));
				Configuration configuration = builder.build();

				TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				try {
					requestToken = twitter.getOAuthRequestToken();
					getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void sendEmail() {
		final EditText input = new EditText(getContext());

		new AlertDialog.Builder(getContext())
				.setTitle("Send Email")
				.setMessage("Enter your email address")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();
						RequestParams params = new RequestParams();
						params.put("to", value.toString());
						Client.get("/email", params, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								Toast.makeText(getContext(), "Successfully sent the email!", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							}
						});
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		}).show();
	}

	private void uploadToImgur() {
		if (bitmap != null) {
			showQRCode();
			return;
		}

		RequestParams params = new RequestParams();

		try {
			InputStream is = new FileInputStream(new File(StepFiveFragment.imagePath));
			params.put("image", is);
		} catch (FileNotFoundException e) {
			Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		Header headers[] = new BasicHeader[1];
		headers[0] = new BasicHeader("Authorization", "Client-ID e27c72efae76d50");
		client.post(getContext(), "https://api.imgur.com/3/image", headers, params, "image/jpeg", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.i(TAG, "Status: " + statusCode + "\nBody: " + new String(responseBody));

				ImgurResponse resp = ImgurResponse.parse(responseBody);
				if (resp.status >= 400) {
					Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
					return;
				}

				bitmap = Bitmap.createScaledBitmap(qrcode(resp.data.link), 512, 512, false);
				showQRCode();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.i("MainActivity", "Status: " + statusCode + "\nBody: " + new String(responseBody));
			}
		});

		Toast.makeText(getContext(), "Uploading image for generating QR Code..", Toast.LENGTH_SHORT).show();
	}

	private Bitmap qrcode(String name) {
		try {
			QRCode qrcode = Encoder.encode(name, ErrorCorrectionLevel.Q);
			ByteMatrix matrix = qrcode.getMatrix();
			Bitmap bitmap = Bitmap.createBitmap(matrix.getWidth(), matrix.getHeight(), Bitmap.Config.RGB_565);
			for (int y = 0; y < matrix.getHeight(); y++) {
				for (int x = 0; x < matrix.getWidth(); x++) {
					bitmap.setPixel(x, y, matrix.get(x, y) == 0 ? 0xFFFFFFFF : 0xFF000000);
				}
			}
			return bitmap;
		} catch (WriterException e) {

		}
		return null;
	}

	private void showQRCode() {
		if (bitmap != null) {
			ImageView image = new ImageView(getContext());
			image.setImageBitmap(bitmap);
			image.setPadding(16, 16, 16, 16);

			new AlertDialog.Builder(getContext())
					.setView(image)
					.show();
		}
	}
}
