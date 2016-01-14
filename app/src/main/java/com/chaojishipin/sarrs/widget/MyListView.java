package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by liuzhuo on 2016/1/14.
 */
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
        init();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
		this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Throwable e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean performItemClick(View view, int position, long id){
        try{
            return super.performItemClick(view, position, id);
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }
}
