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

            // Animal
            R.drawable.animal01,
            R.drawable.animal02,
            R.drawable.animal03,
            R.drawable.animal04,

            // Brand
            R.drawable.brand01,
            R.drawable.brand02,
            R.drawable.brand03,

            // Faces
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

            // Op Art
            R.drawable.opart01,
            R.drawable.opart02,
            R.drawable.opart03,
            R.drawable.opart04,
            R.drawable.opart05,
            R.drawable.opart06,
            R.drawable.opart07,
            R.drawable.opart08,
            R.drawable.opart09,
            R.drawable.opart10,
            R.drawable.opart11,
            R.drawable.opart12,
            R.drawable.opart13,
            R.drawable.opart14,
            R.drawable.opart15,
            R.drawable.opart16,
            R.drawable.opart17,
            R.drawable.opart18,

            // Pattern
            R.drawable.pattern01,
            R.drawable.pattern02,
            R.drawable.pattern03,
            R.drawable.pattern04,
            R.drawable.pattern05,
            R.drawable.pattern06,
            R.drawable.pattern07,
            R.drawable.pattern08,
            R.drawable.pattern09,
            R.drawable.pattern10,
            R.drawable.pattern11,
            R.drawable.pattern12,

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

            // Superhero
            R.drawable.superhero01,
            R.drawable.superhero02,
            R.drawable.superhero03,
            R.drawable.superhero04,
            R.drawable.superhero05,
            R.drawable.superhero06,
            R.drawable.superhero07,
            R.drawable.superhero08,
            R.drawable.superhero09,
            R.drawable.superhero10,
            R.drawable.superhero11,
            R.drawable.superhero12,
            R.drawable.superhero13,
            R.drawable.superhero14,
            R.drawable.superhero15,
            R.drawable.superhero16,
            R.drawable.superhero17,
            R.drawable.superhero18,
            R.drawable.superhero19,
            R.drawable.superhero20,
            R.drawable.superhero21,
            R.drawable.superhero22,
            R.drawable.superhero23,
            R.drawable.superhero24,

            // Wings
            R.drawable.wings01,
            R.drawable.wings02,
            R.drawable.wings03,
            R.drawable.wings04,
            R.drawable.wings05,
            R.drawable.wings06,
            R.drawable.wings07,
            R.drawable.wings08,
            R.drawable.wings09,
            R.drawable.wings10,
            R.drawable.wings11,
            R.drawable.wings12,
            R.drawable.wings13,
            R.drawable.wings14,

            // Fairy Tales
            R.drawable.fairytale_01,
            R.drawable.fairytale_02,
            R.drawable.fairytale_03,
            R.drawable.fairytale_04,
            R.drawable.fairytale_05,
            R.drawable.fairytale_06,
            R.drawable.fairytale_07,
            R.drawable.fairytale_08,
            R.drawable.fairytale_09,
            R.drawable.fairytale_10,
            R.drawable.fairytale_11,
            R.drawable.fairytale_12,
            R.drawable.fairytale_13,
            R.drawable.fairytale_14,
            R.drawable.fairytale_15,
            R.drawable.fairytale_16,
            R.drawable.fairytale_17,
            R.drawable.fairytale_18,
            R.drawable.fairytale_19,
            R.drawable.fairytale_20,
            R.drawable.fairytale_21,
            R.drawable.fairytale_22,
            R.drawable.fairytale_23,
            R.drawable.fairytale_24,
            R.drawable.fairytale_25,
            R.drawable.fairytale_26,
            R.drawable.fairytale_27,
            R.drawable.fairytale_28,
            R.drawable.fairytale_29,
            R.drawable.fairytale_30,
            R.drawable.fairytale_31,
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
		ImageView imageView = null;

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
