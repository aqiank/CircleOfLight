package com.bartleboglehegarty.circleoflight;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class StepFiveFragment extends Fragment {
	private View root;
	private GridView socialGrid;

	public static Uri imageUri;
	public static String imagePath;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_step_five, container, false);

		socialGrid = (GridView) root.findViewById(R.id.social_grid);
		socialGrid.setAdapter(new SocialAdapter(getActivity(), 0));

		Bundle bundle = getArguments();
		if (bundle != null) {
			imagePath = bundle.getString("path", null);
			if (imagePath != null) {
				imageUri = Uri.fromFile(new File(imagePath));
				ImageView image = (ImageView) root.findViewById(R.id.image);
				image.setImageURI(imageUri);
			}
		}
		
		View ok = root.findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity ma = (MainActivity) getActivity();
				ma.gotoMainMenu();
			}
		});

		return root;
	}

}
