package com.bys.sangngoc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageViewHalf extends ImageView {

	public CustomImageViewHalf(Context context) {
		super(context);
	}

	public CustomImageViewHalf(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImageViewHalf(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		setMeasuredDimension(width/3, width/3);
	}

}
