package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.chaojishipin.sarrs.adapter.MyBaseAdapter;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by liuzhuo on 2016/1/14.
 */
public class MyListView extends com.handmark.pulltorefresh.library.MyListView {

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
