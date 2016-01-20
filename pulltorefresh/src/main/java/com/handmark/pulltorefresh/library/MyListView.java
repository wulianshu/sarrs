package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by liuzhuo on 2016/1/14.
 */
public class MyListView extends ListView {

    private OnScrollListener mOnScrollListener;
    private HashSet<OnScrollListener> mSet = new HashSet<OnScrollListener>();

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

    private void updateState(boolean scrolling){
        if(getAdapter() instanceof MyBaseAdapter)
            ((MyBaseAdapter)getAdapter()).setScrolling(scrolling);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void init() {
		this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);

        mOnScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Iterator<OnScrollListener> it = mSet.iterator();
                while (it.hasNext()){
                    it.next().onScrollStateChanged(view, scrollState);
                }
                if(scrollState == SCROLL_STATE_IDLE)
                    updateState(false);
                else
                    updateState(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Iterator<OnScrollListener> it = mSet.iterator();
                while (it.hasNext()){
                    it.next().onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        };

        super.setOnScrollListener(mOnScrollListener);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if(l == null)
            return;
        mSet.add(l);
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
