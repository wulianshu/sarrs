package com.chaojishipin.sarrs.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;

public abstract class ChaoJiShiPinBaseFragment extends Fragment{
    private boolean mIsWifiTo3GFlag;
    protected UIHandler mHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHandler();
        registInfo();
    }

    public void setWifiTo3GFlag(boolean flag) {
        mIsWifiTo3GFlag = flag;
    }
    public boolean getWifiTo3GFlag() {
        return mIsWifiTo3GFlag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 注册信息
     */
    protected void registInfo() {
        //注册EventBus
    EventBus.getDefault().register(this);
    }
    protected void unRegistInfo(){
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistInfo();
    }

    public void onEventMainThread(Object obj) {

    }



    protected abstract void handleInfo(Message msg);
    private void setHandler() {
        mHandler = new UIHandler(this);
    }
//    protected void changeFont(ViewGroup root) {
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
//                "fonts/huawenxinson.ttf");
//        for(int i = 0; i <root.getChildCount(); i++) {
//            View v = root.getChildAt(i);
//            if(v instanceof Button) {
//                ((Button)v).setTypeface(tf);
//            } else if(v instanceof TextView) {
//                ((TextView)v).setTypeface(tf);
//            } else if(v instanceof EditText) {
//                ((EditText)v).setTypeface(tf);
//            }else if(v instanceof AutoCompleteTextView){
//                ((AutoCompleteTextView)v).setTypeface(tf);
//            }else if(v instanceof ViewGroup) {
//                changeFont((ViewGroup)v);
//            }
//        }
//    }

    public void replaceFragment(int id, Fragment fragment){
        try{
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(id, fragment);
            transaction.commitAllowingStateLoss();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    protected static class UIHandler extends Handler {
        private final WeakReference<ChaoJiShiPinBaseFragment> mFragmentView;

        UIHandler(ChaoJiShiPinBaseFragment view) {
            this.mFragmentView = new WeakReference<ChaoJiShiPinBaseFragment>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ChaoJiShiPinBaseFragment service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.handleInfo(msg);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
