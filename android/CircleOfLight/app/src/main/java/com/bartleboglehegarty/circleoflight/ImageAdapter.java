package com.bartleboglehegarty.circleoflight;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class ImageAdapter extends FancyCoverFlowAdapter {
	
	// =============================================================================
	// Private members
	// =============================================================================

	public static int[] IMAGES = {
			// Black
			R.drawable.black,

			// Da Vinci
			R.drawable.davinci_01,
			R.drawable.davinci_02,
			R.drawable.davinci_03,
			R.drawable.davinci_04,
			R.drawable.davinci_05,
			R.drawable.davinci_06,
			R.drawable.davinci_07,
			R.drawable.davinci_08,
			R.drawable.davinci_09,
			R.drawable.davinci_10,
			R.drawable.davinci_11,
			R.drawable.davinci_12,
			R.drawable.davinci_13,
			R.drawable.davinci_14,
			R.drawable.davinci_15,
			R.drawable.davinci_16,
			R.drawable.davinci_17,
			R.drawable.davinci_18,
	};

	// =============================================================================
	// Supertype overrides
	// =============================================================================

	@Override
	public int getCount() {
		return IMAGES.length;
	}

	@Override
	public Integer getItem(int i) {
		return IMAGES[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
		ImageView imageView;

		if (reuseableView != null) {
			imageView = (ImageView) reuseableView;
		} else {
			imageView = new ImageView(viewGroup.getContext());
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(300, 400));

		}

		imageView.setImageResource(this.getItem(i));
		return imageView;
	}

}
