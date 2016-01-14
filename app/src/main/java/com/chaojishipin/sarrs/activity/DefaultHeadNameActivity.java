package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wangyemin on 2015/10/16.
 */
public class DefaultHeadNameActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView mHead;
    private TextView mName;
    private TextView mModifyInfo;
    private RelativeLayout mClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sucess);
        mContext = this;
        mHead = (ImageView) findViewById(R.id.default_head);
        mName = (TextView) findViewById(R.id.default_name);
        mModifyInfo = (TextView) findViewById(R.id.modify_info);
        mClose = (RelativeLayout) findViewById(R.id.bottom_close);
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        String str = getResources().getString(R.string.login_content2);
        int start = 4;
        int end = str.length();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_ff1E27)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mModifyInfo.setText(style);
        ImageLoader.getInstance().displayImage(UserLoginState.getInstance().getUserInfo().getAvatar(), mHead);
        mName.setText(UserLoginState.getInstance().getUserInfo().getName());
    }

    private void setListener() {
        mModifyInfo.setOnClickListener(this);
        mClose.setOnClickListener(this);
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_info:
                finish();
                startActivity(new Intent(mContext, ChaojishipinModifyUserInfoActivity.class));
                break;
            case R.id.bottom_close:
                finish();
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
