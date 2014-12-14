package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;

public class StepOneFragment extends Fragment {
	private FancyCoverFlow fancyCoverFlow;

	private TextView prevCategory = null;
	private TextView currCategory = null;

	private TextView black = null;
	private TextView davinci = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View root = inflater.inflate(R.layout.fragment_step_one, container, false);
		fancyCoverFlow = (FancyCoverFlow) root.findViewById(R.id.fancyCoverFlow);

		fancyCoverFlow.setAdapter(new ImageAdapter());
		fancyCoverFlow.setUnselectedAlpha(0.5f);
		fancyCoverFlow.setUnselectedScale(0.2f);
		fancyCoverFlow.setMaxRotation(10);
		fancyCoverFlow.setScaleX(2.0f);
		fancyCoverFlow.setScaleY(2.0f);
		fancyCoverFlow.setScaleDownGravity(0.5f);
		fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		fancyCoverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				prevCategory = currCategory;

				if (i == 0)
					currCategory = black;
				else if (i < 18)
					currCategory = davinci;

				updateSelection();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		fancyCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putInt("resId", ImageAdapter.IMAGES[position]);
				Fragment fragment = new StepTwoFragment();
				fragment.setArguments(bundle);
				getActivity().getFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
						.replace(R.id.fragment_container, fragment);

				updateSelection();
			}
		});

		fancyCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 				Bundle bundle = new Bundle();
				bundle.putInt("resId", ImageAdapter.IMAGES[position]);
				Fragment fragment = new StepTwoFragment();
				fragment.setArguments(bundle);
				getActivity().getFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
						.replace(R.id.fragment_container, fragment)
						.addToBackStack(null)
						.commit();
			}
		});

		fancyCoverFlow.setAnimationDuration(400);

		black = (TextView) root.findViewById(R.id.black);
		black.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(0, true);
			}
		});

		davinci = (TextView) root.findViewById(R.id.davinci);
		davinci.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(1, true);
			}
		});

		currCategory = black;

		updateSelection();

		return root;
	}

	private void updateSelection() {
		if (currCategory != prevCategory) {
			if (prevCategory != null) {
				prevCategory.setBackgroundResource(R.drawable.roundrect_outline);
				prevCategory.setTextColor(0xFFFFFFFF);
			}
			currCategory.setBackgroundResource(R.drawable.roundrect);
			currCategory.setTextColor(0xFF000000);
		}
	}
}
