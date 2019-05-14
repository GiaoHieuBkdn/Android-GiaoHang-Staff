package com.bys.sangngoc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomLinnerLayoutSquare extends LinearLayout {

	public CustomLinnerLayoutSquare(Context context) {
		super(context);
	}

	public CustomLinnerLayoutSquare(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomLinnerLayoutSquare(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		setMeasuredDimension(width, width);
	}

}
