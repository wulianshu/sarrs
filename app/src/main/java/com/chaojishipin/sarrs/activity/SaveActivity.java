package com.chaojishipin.sarrs.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.fragment.SaveFragment;


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
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        SaveFragment saveF=new SaveFragment();
        fragmentTransaction.replace(R.id.save_content, saveF);
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
}
