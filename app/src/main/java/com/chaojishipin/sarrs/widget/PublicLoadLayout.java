package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chaojishipin.sarrs.R;

/**
 * Created by zhangshuo on 2015/6/3.
 * 公共的数据加载类
 */
public class PublicLoadLayout extends FrameLayout {

    private Context mContext;


    /**
     * 页面要展现的内容
     */
    private LinearLayout mContentView;

   // private RelativeLayout mNetErrorLayout;
    private NetStateView mNetView;

    public PublicLoadLayout(Context context) {
        super(context);
        init(context);
    }

    public PublicLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        inflate(context, R.layout.widget_public_loading_layout, this);
        findView();
    }

    private void findView() {
        mContentView = (LinearLayout) findViewById(R.id.public_loading_content);
        //mNetErrorLayout =(RelativeLayout)findViewById(R.id.public_loading_layout_net_error);
       // mNetView=(NetStateView)findViewById(R.id.base_neterror);
    }
    public void addContent(int viewId){
        inflate(getContext(),viewId,mContentView);
    }
    public NetStateView getNetView(){
        return mNetView;
    }

    public void addContent(View view) {
        if(null != mContentView){
            mContentView.addView(view);
        }
    }


    public LinearLayout getContentView(){
        return mContentView;
    }



    public void removeContent() {
        if(null != mContentView){
            mContentView.removeAllViews();
        }
    }
    public void isLoading(boolean flag) {
    }




}
