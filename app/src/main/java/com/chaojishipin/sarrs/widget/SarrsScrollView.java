package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import java.util.jar.Attributes;

/**
 * Created by xulinlin on 2015/7/2.
 */
public class SarrsScrollView extends ScrollView {

    public SarrsScrollView(Context context) {
        super(context);
        init();
    }
    public SarrsScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SarrsScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        this.setHorizontalFadingEdgeEnabled(false);
        this.setVerticalFadingEdgeEnabled(false);
        this.setVerticalScrollBarEnabled(false);
    }
}
