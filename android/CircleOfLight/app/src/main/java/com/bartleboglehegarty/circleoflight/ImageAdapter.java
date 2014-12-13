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

			// Christmas
			R.drawable.christmas01,
			R.drawable.christmas02,
			R.drawable.christmas03,
			R.drawable.christmas04,
			R.drawable.christmas05,
			R.drawable.christmas06,
			R.drawable.christmas07,
			R.drawable.christmas08,
			R.drawable.christmas09,
			R.drawable.christmas10,
			R.drawable.christmas11,
			R.drawable.christmas12,
			R.drawable.christmas13,
			R.drawable.christmas14,
			R.drawable.christmas15,
			R.drawable.christmas16,
			R.drawable.christmas17,
			R.drawable.christmas18,
			R.drawable.christmas19,
			R.drawable.christmas20,
			R.drawable.christmas21,
			R.drawable.christmas22,
			R.drawable.christmas23,
			R.drawable.christmas24,
			R.drawable.christmas25,
			R.drawable.christmas26,

			// Fairy Tales
			R.drawable.fairytale01,
			R.drawable.fairytale02,
			R.drawable.fairytale03,
			R.drawable.fairytale04,
			R.drawable.fairytale05,
			R.drawable.fairytale06,
			R.drawable.fairytale07,
			R.drawable.fairytale08,
			R.drawable.fairytale09,
			R.drawable.fairytale10,
			R.drawable.fairytale11,
			R.drawable.fairytale12,
			R.drawable.fairytale13,
			R.drawable.fairytale14,
			R.drawable.fairytale15,
			R.drawable.fairytale16,
			R.drawable.fairytale17,
			R.drawable.fairytale18,
			R.drawable.fairytale19,
			R.drawable.fairytale20,
			R.drawable.fairytale21,
			R.drawable.fairytale22,
			R.drawable.fairytale23,
			R.drawable.fairytale24,
			R.drawable.fairytale25,
			R.drawable.fairytale26,
			R.drawable.fairytale27,
			R.drawable.fairytale28,
			R.drawable.fairytale29,
			R.drawable.fairytale30,
			R.drawable.fairytale31,
			R.drawable.fairytale32,
			R.drawable.fairytale33,
			R.drawable.fairytale34,
			R.drawable.fairytale35,
			R.drawable.fairytale36,
			R.drawable.fairytale37,
			R.drawable.fairytale38,
			R.drawable.fairytale39,
			R.drawable.fairytale40,
			R.drawable.fairytale41,
			R.drawable.fairytale42,
			R.drawable.fairytale43,
			R.drawable.fairytale44,
			R.drawable.fairytale45,
			R.drawable.fairytale46,
			R.drawable.fairytale47,
			R.drawable.fairytale48,
			R.drawable.fairytale49,
			R.drawable.fairytale50,
			R.drawable.fairytale51,
			R.drawable.fairytale52,
			R.drawable.fairytale53,
			R.drawable.fairytale54,
			R.drawable.fairytale55,
			R.drawable.fairytale56,

			// Wings
			R.drawable.wing01,
			R.drawable.wing02,
			R.drawable.wing03,
			R.drawable.wing04,
			R.drawable.wing05,
			R.drawable.wing06,
			R.drawable.wing07,
			R.drawable.wing08,
			R.drawable.wing09,
			R.drawable.wing10,
			R.drawable.wing11,
			R.drawable.wing12,
			R.drawable.wing13,
			R.drawable.wing14,
			R.drawable.wing15,
			R.drawable.wing16,
			R.drawable.wing17,
			R.drawable.wing18,
			R.drawable.wing19,
			R.drawable.wing20,

			// Pop
			R.drawable.pop01,
			R.drawable.pop02,
			R.drawable.pop03,
			R.drawable.pop04,
			R.drawable.pop05,
			R.drawable.pop06,
			R.drawable.pop07,
			R.drawable.pop08,
			R.drawable.pop09,
			R.drawable.pop10,
			R.drawable.pop11,
			R.drawable.pop12,
			R.drawable.pop13,
			R.drawable.pop14,
			R.drawable.pop15,
			R.drawable.pop16,
			R.drawable.pop17,

			// Face
			R.drawable.faces01,
			R.drawable.faces02,
			R.drawable.faces03,
			R.drawable.faces04,
			R.drawable.faces05,
			R.drawable.faces06,
			R.drawable.faces07,
			R.drawable.faces08,
			R.drawable.faces09,
			R.drawable.faces10,
			R.drawable.faces11,
			R.drawable.faces12,
			R.drawable.faces13,
			R.drawable.faces14,
			R.drawable.faces15,
			R.drawable.faces16,
			R.drawable.faces17,
			R.drawable.faces18,
			R.drawable.faces19,
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
