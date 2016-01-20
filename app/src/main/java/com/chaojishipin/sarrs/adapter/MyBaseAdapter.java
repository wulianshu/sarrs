package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by liuzhuo on 2016/1/19.
 */
public abstract class MyBaseAdapter extends com.handmark.pulltorefresh.library.MyBaseAdapter {

    public MyBaseAdapter(Context context){
        super(context);
    }

    public void displayImage(String uri, ImageView imageView, int defaultId) {
        if(isScrolling()) {
            ImageLoader.getInstance().cancelDisplayTask(imageView);
            imageView.setImageResource(defaultId);
            return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(defaultId)
                .showImageForEmptyUri(defaultId)
                .showImageOnLoading(defaultId)
                .build();
        ImageLoader.getInstance().displayImage(uri, imageView,options);
    }
}
