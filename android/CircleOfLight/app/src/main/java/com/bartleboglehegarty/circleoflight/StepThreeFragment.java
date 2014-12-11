package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class StepThreeFragment extends Fragment implements MachineController.Callback {
	//public static final String TAG = "StepThreeFragment";

	private Bitmap bitmap;
	private ProgressView progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_step_three, container, false);
		progress = (ProgressView) root.findViewById(R.id.progress);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Initialize machine controller
		MachineController controller = MainActivity.getMachineController();
		controller.setCallback(this);

		// Prepare the image
		loadBitmap();

		// Re-map the image for LED strip output
		processBitmap();

		// Display simulation
		displayProgress();

		// Tell the camera to start capturing
		capturePhoto();

		// Start sending the pixels to the Arduino
		controller.startDrawing(bitmap);
	}

	@Override
	public void onFinished() {
		gotoNextStep();
	}

	@Override
	public void onPositionUpdated(int position, int finalPosition) {
		progress.updateProgress(position, finalPosition);
	}

	private void loadBitmap() {
		if (bitmap == null) {
			String imagePath = getArguments().getString("imagePath", null);
			if (imagePath != null)
				bitmap = BitmapFactory.decodeFile(imagePath);
		}
	}

	private void processBitmap() {
		final int NUM_LEDS = MachineController.NUM_LEDS;
		bitmap = Bitmap.createScaledBitmap(bitmap, NUM_LEDS, NUM_LEDS, true);
		Bitmap bm = Bitmap.createBitmap(bitmap);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int hw = w / 2;
		int hh = h / 2;

		for (int y = 0; y < w; y++) {
			double angle = (((double) y) / w) * Math.PI;
			double minX = hw - (hw * Math.cos(angle));
			double minY = hh - (hh * Math.sin(angle));
			double maxX = hw + (hw * Math.cos(angle));
			double maxY = hh + (hh * Math.sin(angle));

			for (int x = 0; x < h; x++) {
				double p = ((double) x) / h;
				int xx = (int) (minX + p * (maxX - minX));
				int yy = (int) (minY + p * (maxY - minY));
				bitmap.setPixel(x, y, bm.getPixel(xx, yy));
			}
		}
	}

	private void displayProgress() {
		progress.setBitmap(bitmap);
	}

	private void capturePhoto() {
		Client.get("/capture", null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				//handler.sendEmptyMessage(BG_CHECK_CAPTURE);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			}
		});
	}

	private void gotoNextStep() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity()
					.getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
					.replace(R.id.fragment_container, new StepFourFragment())
					.commit();
			}
		});
	}
}
