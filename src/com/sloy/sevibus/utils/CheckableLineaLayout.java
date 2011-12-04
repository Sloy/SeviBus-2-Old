package com.sloy.sevibus.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import com.sloy.sevibus.R;

public class CheckableLineaLayout extends RelativeLayout implements Checkable {

	private boolean isChecked;
	private Checkable checkableView;
	
	public CheckableLineaLayout(Context context) {
		this(context,null);
	}

	public CheckableLineaLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableLineaLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	
	
	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean check) {
		isChecked = check;
/*		for (Checkable c : checkableViews) {
			// Pass the information to all the child Checkable widgets
			c.setChecked(isChecked);
		}*/
		checkableView.setChecked(check);
	}

	@Override
	public void toggle() {
		setChecked(!isChecked);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		checkableView = (Checkable)this.findViewById(R.id.radio); 
		/*
		final int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			findCheckableChildren(this.getChildAt(i));
		}*/
	}
	
	/**
	 * Add to our checkable list all the children of the view that implement the
	 * interface Checkable
	 */
	/*private void findCheckableChildren(View v) {
		if (v instanceof Checkable) {
			this.checkableViews.add((Checkable) v);
		}

		if (v instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup) v;
			final int childCount = vg.getChildCount();
			for (int i = 0; i < childCount; ++i) {
				findCheckableChildren(vg.getChildAt(i));
			}
		}
	}*/

}
