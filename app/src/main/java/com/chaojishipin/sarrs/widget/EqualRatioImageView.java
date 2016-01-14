package com.chaojishipin.sarrs.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import com.chaojishipin.sarrs.R;

public class EqualRatioImageView extends ImageView {

	private float ratio;
	public EqualRatioImageView(Context context) {
		this(context,null);
	}
	public EqualRatioImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EqualRatioImageView);
	    Integer height = a.getInteger(R.styleable.EqualRatioImageView_imageHeight, 1);
	    Integer width = a.getInteger(R.styleable.EqualRatioImageView_imageWidth, 1);
	    ratio=new Float(height.floatValue()/width.floatValue());
	    a.recycle();
	}

	public EqualRatioImageView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// For simple implementation, or internal size is always 0.
		// We depend on the container to specify the layout size of
		// our view. We can't really know what it is since we will be
		// adding and removing different arbitrary views and do not
		// want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		// 高度和宽度等比例
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				childWidthSize, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize*ratio), MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
