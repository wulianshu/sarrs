package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NoScrollListView extends ListView {
	public NoScrollListView(Context context) {
		super(context);
		init();
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
	}
}
