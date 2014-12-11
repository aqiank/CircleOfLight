package com.bartleboglehegarty.circleoflight;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class MachineController {
	public static final String TAG = "MachineController";

	public static final int NUM_LEDS = 214;
	public static final short START_POSITION = 0;
	public static final short END_POSITION = 574;
	public static final int MOTOR_SPEED = 10;
	private static final short OFFSET = 32;

	private static final byte CMD_INIT[] = { 1 };
	private static final byte CMD_START[] = { 10 };
	private static final byte CMD_PIXELS[] = { 11 };
	private static final byte CMD_STOP[] = { 12 };

	private static final short SETTINGS[] = { START_POSITION, END_POSITION, MOTOR_SPEED };

	private Context context;
	private UsbSerialPort port = null;
	private byte[] pixels = new byte[NUM_LEDS * 3];

	private Bitmap bitmap;

	private boolean running = false;
	private Thread thread = null;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			start();

			while (running) {
				getPosition();
				sendPixels();
			}

			stop();
			
			callback.onFinished();
			position = 0;
		}
	};

	private Callback callback = null;	

	private int position = 0;

	public MachineController(Context context) {
		this.context = context;
	}

	public void onResume() {
		openDevice();

		if (port != null)
			init();
	}

	public void onPause() {
		if (port != null) {
			stop();
			closeDevice();
		}
	}

	public void startDrawing(Bitmap bitmap) {
		this.bitmap = bitmap;
		thread = new Thread(runnable);
		thread.start();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	private void init() {
		try {
			java.nio.ByteBuffer bb = ByteBuffer.allocate(2 * SETTINGS.length);
			bb.asShortBuffer().put(SETTINGS);
			port.write(CMD_INIT, 1000);
			port.write(bb.array(), 1000);
		} catch (IOException e) {
			e.printStackTrace();
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

	private void getPosition() {
		try {
			byte buf[] = new byte[2];
			int nread = 0;

			while (nread != 2) {
				// FIXME: Really hacky way to deal with a case when nread is 1 but it works!
				if (nread == 1) {
					byte tmp[] = new byte[1];
					nread = 0;
					while (nread == 0)
						nread = port.read(tmp, 1000);
					buf[1] = tmp[0];
					break;
				}
				nread = port.read(buf, 1000);
			}

			int newPosition = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
			if (Math.abs(newPosition - position) > MOTOR_SPEED) {
				position += MOTOR_SPEED;
				if (position > END_POSITION)
					position = END_POSITION;
			} else {
				position = newPosition;
			}

			if (position == END_POSITION)
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

		boolean flip = false;
		int finalPosition = position + OFFSET;
		if (finalPosition < 0) {
			finalPosition = END_POSITION + finalPosition;
		} else if (finalPosition >= END_POSITION) {
			finalPosition = finalPosition - END_POSITION;
			flip = true;
		}

		callback.onPositionUpdated(position, finalPosition);

		int y = (int) ((finalPosition / ((double) END_POSITION)) * NUM_LEDS);
		for (int x = 0; x < bitmap.getWidth(); x++) {
			if (y >= 0 && y < bitmap.getHeight()) {
				final int start;

				if (flip)
					start = (bitmap.getWidth() - x - 1) * 3;
				else
					start = x * 3;

				int pix = bitmap.getPixel(x, bitmap.getHeight() - y - 1);
				pixels[start + 0] = (byte) (((pix >> 16) & 0xff) / 8);
				pixels[start + 1] = (byte) (((pix >> 8) & 0xff) / 8);
				pixels[start + 2] = (byte) ((pix & 0xff) / 8);
			}
		}

		try {
			port.write(pixels, 1000);
			port.purgeHwBuffers(false, true);
		} catch (IOException e) {
			// Ignore
		}
	}

	private void openDevice() {
		if (port == null)
			port = queryPort();

		Log.d(TAG, "Resumed, port=" + port);
		if (port == null) {
			Toast.makeText(context, "No serial device.", Toast.LENGTH_SHORT).show();
		} else {
			final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
			UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());
			if (connection == null) {
				Toast.makeText(context, "Opening device failed.", Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				port.open(connection);
				port.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
			} catch (IOException e) {
				Toast.makeText(context, "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				try {
					port.close();
				} catch (IOException e2) {
					// Ignore
				}
				port = null;
				return;
			}
			Toast.makeText(context, "Serial device: " + port.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
		}
	}

	private void closeDevice() {
		try {
			port.close();
		} catch (IOException e) {
			// Ignore
		}
		port = null;
	}

	private UsbSerialPort queryPort() {
		// Find all available drivers from attached devices.
		UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
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

	public interface Callback {
		public void onFinished();
		public void onPositionUpdated(int position, int finalPosition);
	}
}
