package com.bartleboglehegarty.circleoflight;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
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

public class StepFiveFragment extends Fragment {
	public static final String TAG = "StepFiveFragment";

	public static Uri imageUri;
	public static String imagePath;
	private static Bitmap bitmap = null;

	private View progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View root = inflater.inflate(R.layout.fragment_step_five, container, false);

		Bundle bundle = getArguments();
		if (bundle != null) {
			imagePath = bundle.getString("path", null);
			if (imagePath != null) {
				imageUri = Uri.fromFile(new File(imagePath));
				ImageView image = (ImageView) root.findViewById(R.id.image);
				image.setImageURI(imageUri);
			}
		}
		
		View ok = root.findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity ma = (MainActivity) getActivity();
				ma.gotoMainMenu();
			}
		});

		View facebook = root.findViewById(R.id.facebook);
		facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// start Facebook Login
				Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if (state.isOpened())
							uploadFacebook();
					}
				});
			}
		});

		View email = root.findViewById(R.id.email);
		email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendEmail();
			}
		});

		View qrcode = root.findViewById(R.id.qrcode);
		qrcode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				uploadToImgur();
			}
		});

		progress = root.findViewById(R.id.progress);

		return root;
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
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		Header headers[] = new BasicHeader[1];
		headers[0] = new BasicHeader("Authorization", "Client-ID e27c72efae76d50");
		client.post(getActivity(), "https://api.imgur.com/3/image", headers, params, "image/jpeg", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.i(TAG, "Status: " + statusCode + "\nBody: " + new String(responseBody));
				progress.setVisibility(View.INVISIBLE);

				ImgurResponse resp = ImgurResponse.parse(responseBody);
				if (resp.status >= 400) {
					Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
					return;
				}

				bitmap = Bitmap.createScaledBitmap(qrcode(resp.data.link), 384, 384, false);
				showQRCode();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.i("MainActivity", "Status: " + statusCode + "\nBody: " + new String(responseBody));
				progress.setVisibility(View.INVISIBLE);
			}
		});

		Toast.makeText(getActivity(), "Uploading image for generating QR Code..", Toast.LENGTH_SHORT).show();
		progress.setVisibility(View.VISIBLE);
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
					Toast.makeText(getActivity(), "Successfully uploaded photo to Facebook!", Toast.LENGTH_SHORT).show();
					Session.getActiveSession().closeAndClearTokenInformation();
				}
			}
		).executeAsync();
	}

	private void showQRCode() {
		if (bitmap != null) {
			ImageView image = new ImageView(getActivity());
			image.setImageBitmap(bitmap);
			image.setPadding(16, 16, 16, 16);
			new AlertDialog.Builder(getActivity())
				.setView(image)
				.show();
		}
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
			// Ignore
		}
		return null;
	}

	private void sendEmail() {
		final EditText input = new EditText(getActivity());
		new AlertDialog.Builder(getActivity())
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
							Toast.makeText(getActivity(), "Successfully sent the email!", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							// Ignore
						}
					});
					Toast.makeText(getActivity(), "Sending email in the background..", Toast.LENGTH_SHORT).show();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Ignore
				}
			}).show();
	}
}
