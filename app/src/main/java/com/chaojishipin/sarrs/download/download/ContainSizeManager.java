package com.chaojishipin.sarrs.download.download;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 下载模块的sdCard容量管理
 *
 * @author daipei
 */
public class ContainSizeManager {

    // 数据
    private String kuaikanStr, freeStr; // (对应上面的数据的字符串表示)
    private DecimalFormat df;
    private double free;
    private double kuaikan;
    private Handler mHandler;

    private final int MSG_UPDATE_UI = 0;

    private ContainSizeManager() {
        df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        mHandler = new MyHandler(this);
    }

    private static class Singleton {
        private static final ContainSizeManager INSTANCE = new ContainSizeManager();
    }

    public static synchronized final ContainSizeManager getInstance() {
        return Singleton.INSTANCE;
    }

    // SDCard剩余空间大小（单位：GB，2位精度）
    public void countFreeSize() {
        String path = DownloadHelper.getDownloadPath().replace("/" + Utils.getDownLoadFolder(), "");
        free = DownloadHelper.getSdcardStorage(path) / 1024;
        if (df != null)
            freeStr = df.format(free);
    }

    // SDCard剩余空间大小（单位：GB，2位精度）
    public void countFreeSizeForSetting(String path) {
        free = DownloadHelper.getSdcardStorage(path.replace("/" + Utils.getDownLoadFolder(), "")) / 1024;
        if (df != null)
            freeStr = df.format(free);
        LogUtil.e("wulianshu","freeStr:"+freeStr);
    }

    // SDCard\kuaikan程序所占的空间大小（单位：GB，2位精度）
    public void countFunshionSize() {
        String path = DownloadHelper.getDownloadPath().replace("/" + Utils.getDownLoadFolder(), "");
        kuaikan = DownloadHelper.getSdUsedStorage(path)/1024;
        if (df != null)
            kuaikanStr = df.format(kuaikan);
        LogUtil.e("wulianshu", "yiyong:" + kuaikanStr);
    }

    /**
     * 获得sd卡余量
     *
     * @author daipei
     */
    public double getFreeSize() {
        countFreeSize();
        return free * 1024;
    }

    /**
     * 获得sd卡余量
     * 剩余空间大于1024MB，显示GB
     * 否则。显示MB
     *
     * @author daipei
     */
    public String getFreeSizeForSetting(String path) {
        countFreeSizeForSetting(path);
        String freesize = free >= 1 ? freeStr + "GB" : Double.parseDouble(freeStr) * 1024 + "MB";
        return freesize;
    }

    public void ansynHandlerSdcardSize(final Activity ac) {
        new Thread(){
            public void run(){
                countFunshionSize();
                countFreeSize();
                Message msg = mHandler.obtainMessage(MSG_UPDATE_UI);
                msg.obj = ac;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void showView(Activity view) {
        // 找到视图并赋值
        if(view == null || view.isFinishing())
            return;
        try{
            TextView tv = (TextView) view.findViewById(R.id.available_space_tv);
            if(tv != null)
                tv.setText("已用" + kuaikanStr + "G/" + "可用" + freeStr + "G");
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.available_space_bar);
            if(progressBar != null){
                int percentage;
                if (kuaikan + free > 0) {
                    percentage = (int)(kuaikan * 100 / (kuaikan + free));
                    if (0 == percentage && free < 0.01) {
                        percentage = 10;
                    }
                    progressBar.setProgress(percentage);
                } else {
                    progressBar.setProgress(0);
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    private void handleInfo(Message msg){
        switch(msg.what){
            case MSG_UPDATE_UI:
                if(msg.obj instanceof Activity)
                    showView((Activity)msg.obj);
                break;
        }
    }

    protected static class MyHandler extends Handler {
        private final WeakReference<ContainSizeManager> mFragmentView;

        MyHandler(ContainSizeManager view) {
            this.mFragmentView = new WeakReference<ContainSizeManager>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ContainSizeManager service = mFragmentView.get();
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
