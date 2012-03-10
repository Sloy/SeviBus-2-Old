package com.sloy.sevibus.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation{
	
	private final View _view;
	private final int _startHeight;
	private final int _finishHeight;
	
	public ExpandAnimation(View view, int startHeight, int finishHeight){
		_view = view;
		_startHeight = startHeight;
		_finishHeight = finishHeight;
		setDuration(220);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final int newHeight = (int)((_finishHeight-_startHeight)*interpolatedTime+_startHeight);
		_view.getLayoutParams().height = newHeight;
		_view.requestLayout();
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
	
}