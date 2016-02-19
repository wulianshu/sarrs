package com.chaojishipin.sarrs.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.fragment.SaveFragment;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.ConstantUtils;


public class SaveActivity extends ChaoJiShiPinBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SaveFragment saveF=new SaveFragment();
        replaceFragment(R.id.save_content, saveF);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengPagePath.beginpage(ConstantUtils.AND_FAVOR,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengPagePath.beginpage(ConstantUtils.AND_FAVOR, this);
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
}
