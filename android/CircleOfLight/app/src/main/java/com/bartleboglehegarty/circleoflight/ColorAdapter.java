package com.bartleboglehegarty.circleoflight;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;

public class ColorAdapter extends ArrayAdapter {
	
	// =============================================================================
	// Private members
	// =============================================================================

	public ColorAdapter(Context context, int resource) {
		super(context, resource);
	}

	public static int[] COLORS = {
		0xffffffff,
		0xff000000,
		0xffff0000,
		0xff00ff00,
		0xff0000ff,
		0xffffff00,
		0xff00ffff,
		0xffff00ff
	};

	// =============================================================================
	// Supertype overrides
	// =============================================================================

	@Override
	public int getCount() {
		return COLORS.length;
	}

	@Override
	public Integer getItem(int i) {
		return COLORS[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View reuseableView, ViewGroup viewGroup) {
		View view = null;

		if (reuseableView != null) {
			view = reuseableView;
		} else {
			view = new View(viewGroup.getContext());
			LayoutParams params = new LayoutParams(50, 50);
			view.setBackgroundColor(COLORS[i]);
			view.setLayoutParams(params);
		}

		return view;
	}

}
