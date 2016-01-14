package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.thirdparty.umeng.UMengAnalysis;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

/**
 * Created by vicky on 15/9/8.
 */
public class ShowHeaderActivity extends ChaoJiShiPinBaseActivity{
    private ImageView header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        setContentView(R.layout.show_header_activity);
        header = (ImageView)findViewById(R.id.imageViewer);
        String image = getIntent().getStringExtra("image");
        ImageLoader.getInstance().displayImage(image, header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
