package com.chaojishipin.sarrs.download.download;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 下载模块的sdCard容量管理
 *
 * @author daipei
 */
public class ContainSizeManager {

    // 容量相关：
    // 视图
    private TextView sdcardView; // 视图
    private ProgressBar progressBar;
    // 数据
    //private double funshionSize, freeSize; // 总容量，其他程序容量，风行容量，剩余大小(精度：2位,单位：G)
    private String kuaikanStr, freeStr; // (对应上面的数据的字符串表示)
    private DecimalFormat df;
    private Activity view;
    private double free;
    private double kuaikan;

    private Timer mTimer;

//    private ContainSizeManager(Activity view) {
//        df = new DecimalFormat();
//        df.setMinimumFractionDigits(2);
//        df.setMaximumFractionDigits(2);
//        this.view = view;
//    }


    public void setView(Activity view) {
        this.view = view;
    }

    private ContainSizeManager() {
        df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    }

    private static class Singleton {
        private static final ContainSizeManager INSTANCE = new ContainSizeManager();
    }

    public static final ContainSizeManager getInstance() {
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
        LogUtil.e("wulianshu","yiyong:"+kuaikanStr);
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

    public void ansynHandlerSdcardSize() {
        countFunshionSize();
        countFreeSize();
        showView();
//      anysnUpdateSizeView();
    }

//    public void anysnUpdateSizeView() {
//        new Thread() {
//            public void run() {
//                // 取得相关值
//                countFunshionSize();
//                countFreeSize();
//                showView();
////                ansynUpdateHandler.post(ansynUpdateUi);
//            }
//        }.start();
//    }

    private void showView() {
        // 找到视图并赋值
        if (null != view)
            sdcardView = (TextView) view.findViewById(R.id.available_space_tv);
        if (null != sdcardView) {
            sdcardView.setText("已用" + kuaikanStr + "G/" + "可用" + freeStr + "G");
        }
        if (null != view)
            progressBar = (ProgressBar) view.findViewById(R.id.available_space_bar);
        if (null != progressBar && (kuaikan + free) > 0) {
            double percentage = kuaikan / (kuaikan + free);
            if (0.0 == percentage) {
                if (free == 0.0) {
                    percentage = 10.0;
                }
            }
            progressBar.setProgress((int) (percentage * 100));
        } else {
            if (null != progressBar)
                progressBar.setProgress(0);
        }
    }

//}

//Runnable ansynUpdateUi = new Runnable() {
//
//    @Override
//    public void run() {
//        // 找到视图并赋值
//        sdcardView = (TextView) view.findViewById(R.id.available_space_tv);
//        if (null != sdcardView) {
//            sdcardView.setText("已用" + kuaikanStr + "G/" + "可用" + freeStr + "G");
//        }
//        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.available_space_bar);
//        if (null != progressBar && (kuaikan + free) > 0) {
//            double percentage = kuaikan / (kuaikan + free);
//            if (0.0 == percentage) {
//                if (free == 0.0) {
//                    percentage = 10.0;
//                }
//            }
//            progressBar.setProgress((int) (percentage * 100));
//        } else {
//            progressBar.setProgress(0);
//        }
//    }
//};

//    // 处理容量更新的Handler
//    @SuppressLint("HandlerLeak")
//    Handler ansynUpdateHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//        }
//    };


    public void checkSDCard() {
        if (mTimer != null) {
            Log.d("wym", "return");
            return;
        }
        final int num = DataUtils.getInstance().getDownloadJobNum();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (num > 0) {
                    Log.d("wym", "freeSize is " + getFreeSize());
                    if (getFreeSize() <= Utils.SDCARD_MINSIZE) {
                        Intent intent = new Intent();
                        intent.setAction(DownloadBroadcastReceiver.SDCARD_NOSPACE_ACTION);
                        ChaoJiShiPinApplication.getInstatnce().getApplicationContext().sendBroadcast(intent);
                    }
                } else {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }, 5000, 5000);
    }
}
