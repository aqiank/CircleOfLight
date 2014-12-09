package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

public class StepFourFragment extends Fragment implements Handler.Callback {
    public static final String TAG = "StepFourFragment";

    private static final int BG_CHECK_CAPTURE = 10;
    private static final int UI_LOAD_IMAGE = 20;

	private View root;
    private ImageView image;
    private View progress;
    private String resultPath;

    private Handler handler = null;
	private Handler uiHandler = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_step_four, container, false);

        image = (ImageView) root.findViewById(R.id.image);
        progress = root.findViewById(R.id.progress);

		return root;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        uiHandler = new Handler(getActivity().getMainLooper(), this);

        handler.sendEmptyMessage(BG_CHECK_CAPTURE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case BG_CHECK_CAPTURE:
            checkImage();
            break;

        case UI_LOAD_IMAGE:
            loadImage();
            TextView tv = (TextView) progress.findViewById(R.id.text);
            tv.setText("Downloading image from camera..");
            break;
        }
        return true;
    }

    private void checkImage() {
        Client.get("/check_capture", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody[0] == 1) {
                    uiHandler.sendEmptyMessage(UI_LOAD_IMAGE);
                } else {
                    handler.sendEmptyMessageDelayed(BG_CHECK_CAPTURE, 1000);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handler.sendEmptyMessage(BG_CHECK_CAPTURE);
            }
        });
    }

    private void loadImage() {
        Client.getFile("/photo.jpg", new FileAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(getActivity(), "Failed to get the image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                resultPath = file.getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath);
                progress.setVisibility(View.INVISIBLE);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
                root.findViewById(R.id.ok).setVisibility(View.VISIBLE);
            }
        });

        View ok = root.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle bundle = new Bundle();
                bundle.putString("path", resultPath);
                Fragment fragment = new StepFiveFragment();
                fragment.setArguments(bundle);
                getActivity()
                        .getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
    }
}