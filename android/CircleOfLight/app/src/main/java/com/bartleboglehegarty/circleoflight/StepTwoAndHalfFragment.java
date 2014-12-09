package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class StepTwoAndHalfFragment extends Fragment implements Handler.Callback {
    private static final int BG_COUNT = 10;
    private static final int UI_UPDATE = 20;
    private static final int UI_NEXT = 21;

	private static String imagePath;

    private View root;
    private TextView counterText;
    private int counter;

    private Handler handler;
    private Handler uiHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_step_two_and_half, container, false);
        counterText = (TextView) root.findViewById(R.id.counter);

		Bundle bundle = getArguments();
		if (bundle != null) {
			imagePath = bundle.getString("imagePath", null);
		}

		return root;
	}

    @Override
    public void onResume() {
        super.onResume();
        counter = 5;
        counterText.setText("" + counter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();

        handler = new Handler(thread.getLooper(), this);
        uiHandler = new Handler(getActivity().getMainLooper(), this);

        handler.sendEmptyMessageDelayed(BG_COUNT, 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case BG_COUNT:
            if (counter > 0) {
                counter--;
                uiHandler.sendEmptyMessage(UI_UPDATE);
                handler.sendEmptyMessageDelayed(BG_COUNT, 1000);
            } else {
                uiHandler.sendEmptyMessage(UI_NEXT);
            }
            break;

        case UI_UPDATE:
            counterText.setText("" + counter);
            break;

        case UI_NEXT:
            Bundle bundle = new Bundle();
            bundle.putString("imagePath", imagePath);
            Fragment fragment = new StepThreeFragment();
            fragment.setArguments(bundle);
            getActivity()
                    .getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            break;
        }

        return true;
    }
}