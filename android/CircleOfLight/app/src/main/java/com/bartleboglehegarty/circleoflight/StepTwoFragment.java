package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StepTwoFragment extends Fragment {
	private View root;
	private ImageView imageView;
	private TextView textView;
	private View lastLayoutItem;
	private boolean started;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_step_two, container, false);
		
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
		
		int id = getArguments().getInt("resId", 0);
		imageView = (ImageView) root.findViewById(R.id.image);
		imageView.setImageResource(id);
		
		View btnOk = root.findViewById(R.id.ok);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (started)
					return;
				String path = takeScreenshot();
				Bundle bundle = new Bundle();
				bundle.putString("imagePath", path);
				Fragment fragment = new StepTwoAndHalfFragment();
				fragment.setArguments(bundle);
				getActivity()
					.getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
					.replace(R.id.fragment_container, fragment)
					.addToBackStack(null)
					.commit();
				started = true;
			}
		});
		
		textView = (TextView) root.findViewById(R.id.text);
		
		LayoutAdapter layoutAdapter = new LayoutAdapter(getActivity(), 0);
		GridView grid = (GridView) root.findViewById(R.id.layout_grid);
		grid.setAdapter(layoutAdapter);
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setBackgroundColor(0x80ffffff);
				if (lastLayoutItem != null)
					lastLayoutItem.setBackgroundColor(0x40ffffff);

				lastLayoutItem = view;
				
				switch (position) {
				case 0:
					textView.setGravity(Gravity.TOP | Gravity.LEFT);
					break;
				case 1:
					textView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
					break;
				case 2:
					textView.setGravity(Gravity.TOP | Gravity.RIGHT);
					break;
				case 3:
					textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					break;
				case 4:
					textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
					break;
				case 5:
					textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					break;
				case 6:
					textView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
					break;
				case 7:
					textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
					break;
				case 8:
					textView.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
					break;
				}
			}
		});
		
		grid = (GridView) root.findViewById(R.id.color_grid);
		grid.setAdapter(new ColorAdapter(getActivity(), 0));
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				textView.setTextColor(ColorAdapter.COLORS[position]);
			}
		});
		
		SeekBar slider = (SeekBar) root.findViewById(R.id.text_size);
		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				textView.setTextSize(progress);
			}
		});
		
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();

		started = false;
	}

	private String takeScreenshot() {
		// image naming and path to include sd card appending name you choose for file
		String path = Environment.getExternalStorageDirectory().toString() + "/image.png";
		
		// create bitmap screen capture
		Bitmap bitmap;
		root.setDrawingCacheEnabled(true);
		
		boolean hasText = textView.getText().length() > 0;
		if (!hasText)
			textView.setVisibility(View.GONE);

		bitmap = Bitmap.createBitmap(root.getDrawingCache(), (int) ((root.getWidth() - root.getHeight()) / 2), (int) imageView.getY(), imageView.getHeight(), imageView.getHeight());
		root.setDrawingCacheEnabled(false);
		if (!hasText)
			textView.setVisibility(View.VISIBLE);

		OutputStream fout = null;
		File imageFile = new File(path);
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
}
