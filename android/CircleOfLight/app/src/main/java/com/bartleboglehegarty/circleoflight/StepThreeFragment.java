package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class StepThreeFragment extends Fragment {
	public static final String TAG = "StepThreeFragment";

	public static final int NUM_LEDS = 216;
	public static final short END = 575;
	public static final short OFFSET = 0;
	public static final int SPEED = 10;

	private static final byte CMD_START[] = { 10 };
	private static final byte CMD_PIXELS[] = { 11 };
	private static final byte CMD_STOP[] = { 12 };

	private static UsbSerialPort port = null;
	private int position = 0;

	private ProgressView progress;
	private Bitmap bitmap;

	private Thread thread = null;
	private boolean running = false;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			start();

			while (running) {
				getPosition();
				sendPixels();
			}

			stop();

			gotoNextStep();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_step_three, container, false);
		progress = (ProgressView) root.findViewById(R.id.progress);
		return root;
	}

	@Override
	public void onPause() {
		super.onPause();

		stop();
		closeDevice();
	}

	@Override
	public void onResume() {
		super.onResume();

		// Look for serial device (Arduino)
		openDevice();

		// Prepare the image
		loadBitmap();

		// Re-map the image for LED strip output
		processBitmap();

		// Display simulation
		displayProgress();

		// Tell the camera to start capturing
		capturePhoto();

		// Start sending the pixels to the Arduino
		startDrawing();
	}

	private void loadBitmap() {
		if (bitmap == null) {
			String imagePath = getArguments().getString("imagePath", null);
			if (imagePath != null) {
				bitmap = BitmapFactory.decodeFile(imagePath);
			}
		}
	}

	private void startDrawing() {
		thread = new Thread(runnable);
		thread.start();
	}

	private void processBitmap() {
		bitmap = Bitmap.createScaledBitmap(bitmap, NUM_LEDS, NUM_LEDS, true);
		Bitmap bm = Bitmap.createBitmap(bitmap);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int hw = w / 2;
		int hh = h / 2;

		for (int x = 0; x < w; x++) {
			double angle = (((double) x) / w) * Math.PI;
			double minX = hw - (hw * Math.cos(angle));
			double minY = hh - (hh * Math.sin(angle));
			double maxX = hw + (hw * Math.cos(angle));
			double maxY = hh + (hh * Math.sin(angle));

			for (int y = 0; y < h; y++) {
				double p = ((double) y) / h;
				int xx = (int) (minX + p * (maxX - minX));
				int yy = (int) (minY + p * (maxY - minY));
				bitmap.setPixel(y, x, bm.getPixel(xx, yy));
			}
		}
	}

	private void displayProgress() {
		progress.setBitmap(bitmap);
	}

	private String saveResult() {
		// image naming and path to include sd card appending name you choose for file
		String path = Environment.getExternalStorageDirectory().toString() + "/result.jpg";
		File imageFile = new File(path);
		OutputStream fout;

		try {
			fout = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			fout.flush();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return path;
	}

	private void openDevice() {
		if (port == null)
			port = getPort();

		Log.d(TAG, "Resumed, port=" + port);
		if (port == null) {
			Toast.makeText(getActivity(), "No serial device.", Toast.LENGTH_SHORT).show();
		} else {
			final UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
			UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());
			if (connection == null) {
				Toast.makeText(getActivity(), "Opening device failed.", Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				port.open(connection);
				port.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
			} catch (IOException e) {
				Toast.makeText(getActivity(), "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				try {
					port.close();
				} catch (IOException e2) {
					// Ignore.
				}
				port = null;
				return;
			}
			Toast.makeText(getActivity(), "Serial device: " + port.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
		}
	}

	private void closeDevice() {
		if (port != null) {
			try {
				port.close();
			} catch (IOException e) {
				// Ignore.
			}
			port = null;
		}
	}

	private UsbSerialPort getPort() {
		// Find all available drivers from attached devices.
		UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
		if (availableDrivers.isEmpty())
			return null;

		// Open a connection to the first available driver.
		UsbSerialDriver driver = availableDrivers.get(0);
		UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
		if (connection == null)
			return null; // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)

		// Read some data! Most have just one port (port 0).
		List<UsbSerialPort> ports = driver.getPorts();
		if (ports.size() > 0)
			return ports.get(0);

		return null;
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

	private void getPosition() {
		try {
			byte buf[] = new byte[2];
			int nread = 0;

			while (nread != 2) {
				// FIXME: Really hacky way to deal with a case when nread is 1..but it works!
				if (nread == 1) {
					byte tmp[] = new byte[1];
					nread = 0;
					while (nread == 0) {
						nread = port.read(tmp, 1000);
					}
					buf[1] = tmp[0];
					break;
				}
				nread = port.read(buf, 1000);
			}

			int newPosition = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
			if (Math.abs(newPosition - position) > SPEED) {
				position += SPEED;
				if (position > END)
					position = END;
			} else {
				position = newPosition;
			}

			if (position == END)
				running = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendPixels() {
		try {
			port.write(CMD_PIXELS, 1000);
			port.purgeHwBuffers(false, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int finalPos = position + OFFSET;
		boolean flip = false;
		if (finalPos < 0)
			finalPos = END + finalPos;
		else if (finalPos >= END )
			finalPos = finalPos - END;

		progress.updateProgress(position, finalPos, flip);

		int y = (int) ((finalPos / ((double) END)) * NUM_LEDS);
		for (int x = 0; x < bitmap.getWidth(); x++) {
			int pix = 0;
			if (y >= 0 && y < bitmap.getHeight())
				pix = bitmap.getPixel(x, bitmap.getHeight() - y - 1);

			byte[] bs = { (byte) (((pix >> 16) & 0xff) / 8), (byte) (((pix >> 8) & 0xff) / 8), (byte) ((pix & 0xff) / 8) };
			try {
				port.write(bs, 1000);
				//port.purgeHwBuffers(false, true);
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	private void start() {
		try {
			port.write(CMD_START, 1000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		running = true;
	}

	private void stop() {
		try {
			port.write(CMD_STOP, 1000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		running = false;
		thread = null;
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
