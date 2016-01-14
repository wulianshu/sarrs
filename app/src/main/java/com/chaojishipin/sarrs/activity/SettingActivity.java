package com.chaojishipin.sarrs.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.fragment.SaveFragment;
import com.chaojishipin.sarrs.fragment.SettingFragment;
import com.chaojishipin.sarrs.widget.TitleActionBar;


public class SettingActivity extends ChaoJiShiPinBaseActivity implements TitleActionBar.onActionBarClickListener {

    private TitleActionBar mTitleActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(true);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        setTitleBarVisibile(false);
        mTitleActionBar = (TitleActionBar) findViewById(R.id.mainactivity_title_layout);
        mTitleActionBar.setVisibility(View.VISIBLE);
        mTitleActionBar.setTitle(getResources().getString(R.string.sarrs_str_setting));
        mTitleActionBar.setRightEditButtonVisibility(true);
        mTitleActionBar.setmRightButtonVisibility(false);
        mTitleActionBar.setRightEditButtonVisibility(false);
        mTitleActionBar.setOnActionBarClickListener(this);
        mTitleActionBar.getmLeftButton().setBackgroundResource(R.drawable.selector_ranklistdetail_titlebar);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        SettingFragment settingFragment=new SettingFragment();
        fragmentTransaction.replace(R.id.save_content, settingFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.activity_seve, null);
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public void onTitleLeftClick(View v) {
         finish();
    }

    @Override
    public void onTitleRightClick(View v) {

    }

    @Override
    public void onTitleDoubleTap() {

    }
}
