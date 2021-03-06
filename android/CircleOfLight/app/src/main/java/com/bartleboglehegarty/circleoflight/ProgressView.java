package com.bartleboglehegarty.circleoflight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class ProgressView extends SurfaceView {
	public static final String TAG = "ProgressView";
	
	private Paint paint = new Paint();
	private Bitmap bitmap;
	private int position;
	private int finalPosition;
	
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public void updateProgress(int position, int finalPosition) {
		this.position = position;
		this.finalPosition = finalPosition;
		if (position <= MachineController.END_POSITION) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null) {
				drawSimulation(canvas);
				getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
	
	private void drawSimulation(Canvas canvas) {
		float angle = (float) ((float) position / MachineController.END_POSITION * Math.PI);
		
		paint.setColor(0x40000000);
		canvas.drawRect(canvas.getClipBounds(), paint);
		
		canvas.save();
		canvas.translate(getWidth() / 2, getHeight() / 2);
		canvas.rotate((float) Math.toDegrees(angle));
		
		float w = bitmap.getWidth();
		float h = bitmap.getHeight();

		int y = (int) (((float)finalPosition / MachineController.END_POSITION) * h);
		if (y >= 0 && y < bitmap.getHeight()) {
			for (float x = 0; x < w; x++) {
				float startX = x * (getWidth() / w) - (getWidth() / 2);
				float stopX = startX + (getWidth() / w);
				int pix = bitmap.getPixel((int) x, y);
				paint.setColor(pix);
				canvas.drawLine(startX, 0, stopX, 0, paint);
			}
		}
		
		canvas.restore();	
	}
}
