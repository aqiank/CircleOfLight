package com.bartleboglehegarty.circleoflight;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.Session;

public class MainActivity extends Activity {
	private Fragment firstFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
				Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				View decorView = getWindow().getDecorView();
				int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
								View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				View decorView = getWindow().getDecorView();
				int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
				decorView.setSystemUiVisibility(uiOptions);
		}

		setContentView(R.layout.activity_main);

		gotoMainMenu();
	}

	public void gotoMainMenu() {
		FragmentManager fm = getFragmentManager();
		fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		if (firstFragment != null)
			fm.beginTransaction().remove(firstFragment).commit();

		firstFragment = new StepOneFragment();
		fm.beginTransaction()
			.replace(R.id.fragment_container, firstFragment)
			.commit();
	}
	
	public void back(View v) {
		getFragmentManager().popBackStack();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
}
