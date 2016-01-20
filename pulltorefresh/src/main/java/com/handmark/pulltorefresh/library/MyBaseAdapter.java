package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by liuzhuo on 2016/1/19.
 */
public abstract class MyBaseAdapter extends BaseAdapter {

    private boolean mScrolling;
    protected Context mContext;

    public MyBaseAdapter(Context context){
        mContext = context;
    }

    public void setScrolling(boolean scrolling){
        mScrolling = scrolling;
    }

    protected boolean isScrolling(){
        return mScrolling;
    }

    protected abstract View getView(int position, View convertView);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            return getView(position, convertView);
        }catch(Throwable e){
            e.printStackTrace();
            return new View(mContext);
        }
    }
}
