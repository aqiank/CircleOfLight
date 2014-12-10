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
	private TextView animal = null;
	private TextView brand = null;
	private TextView face = null;
	private TextView opart = null;
	private TextView pattern = null;
	private TextView pop = null;
	private TextView superhero = null;
	private TextView wings = null;
	private TextView fairytale = null;
	
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
				else if (i < 5)
					currCategory = animal;
				else if (i < 8)
					currCategory = brand;
				else if (i < 27)
					currCategory = face;
				else if (i < 45)
					currCategory = opart;
				else if (i < 57)
					currCategory = pattern;
				else if (i < 74)
					currCategory = pop;
				else if (i < 98)
					currCategory = superhero;
				else if (i < 112)
					currCategory = wings;
				else
					currCategory = fairytale;

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


		animal = (TextView) root.findViewById(R.id.animal);
		animal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(1, true);
			}
		});

		brand = (TextView) root.findViewById(R.id.brand);
		brand.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(5, true);
			}
		});

		face = (TextView) root.findViewById(R.id.face);
		face.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(8, true);
			}
		});

		opart = (TextView) root.findViewById(R.id.opart);
		opart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(27, true);
			}
		});

		pattern = (TextView) root.findViewById(R.id.pattern);
		pattern.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(45, true);
			}
		});

		pop = (TextView) root.findViewById(R.id.pop);
		pop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(57, true);
			}
		});

		superhero = (TextView) root.findViewById(R.id.superhero);
		superhero.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(74, true);
			}
		});

		wings = (TextView) root.findViewById(R.id.wings);
		wings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(98, true);
			}
		});

		fairytale = (TextView) root.findViewById(R.id.fairytale);
		fairytale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fancyCoverFlow.setSelection(112, true);
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
