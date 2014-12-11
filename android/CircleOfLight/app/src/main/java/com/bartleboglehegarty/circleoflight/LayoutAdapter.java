package com.bartleboglehegarty.circleoflight;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;

public class LayoutAdapter extends ArrayAdapter {
	
	// =============================================================================
	// Private members
	// =============================================================================

	public LayoutAdapter(Context context, int resource) {
		super(context, resource);
	}

	// =============================================================================
	// Supertype overrides
	// =============================================================================

	@Override
	public int getCount() {
		return 9;
	}

	@Override
	public Integer getItem(int i) {
		return 0;
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
			LayoutParams params = new LayoutParams(72, 72);
			view = new View(viewGroup.getContext());
			view.setLayoutParams(params);
			view.setBackgroundColor(0x40ffffff);
		}

		return view;
	}

}
