package com.chaojishipin.sarrs.download.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.download.fragment.DownloadFragment;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;


public class DownloadBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "DownloadBroadcastReceiver";
    public static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static String DOWNLOADCAN3G = "com.funshion.video.DOWNLOADCAN3G";
    public static final String SPEED_ACTION = "com.funshion.video.CUTDOWNLOADSPEED";
    public static final String SDCARD_NOSPACE_ACTION = "com.chaojishipin.SDCARDNOSPACE";
    private final static int FAILURE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != context && null != intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            // 网络发生变化时
            if (NET_ACTION.equals(action)) {
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null == manager) {
                    return;
                }
                NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (null == wifi || null == gprs) {
                    return;
                }
                if (wifi.isConnected()) {
                    DataUtils.getInstance().startAllDownload();
                }
                if (gprs.isConnected()) {
                    DataUtils.getInstance().pauseAllDownload();
                }
            }

            if (DOWNLOADCAN3G.equals(action)) {
                boolean isDownloadcan3g = intent.getBooleanExtra("isDownloadcan3g", false);
                // 当非移动网络时
                if (!(NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == 2))
                    return;
                if (!isDownloadcan3g) {
                    DataUtils.getInstance().pauseAllDownload();
                } else {
                    DataUtils.getInstance().startAllDownload();
                }
            }

            if (SDCARD_NOSPACE_ACTION.equals(action)) {
                ToastUtil.showShortToast(context, R.string.sdcard_nospace);
                DataUtils.getInstance().pauseAllDownload();
            }

            if (SPEED_ACTION.equals(action)) {
                boolean cutSpeed = intent.getBooleanExtra("player", false);
                ChaoJiShiPinApplication.getInstatnce().setSpeedCut(cutSpeed);
                if (cutSpeed) {
                    DataUtils.getInstance().pauseAllDownload();
                } else {
                    DataUtils.getInstance().startAllDownload();
                }
            }

            if (Intent.ACTION_MEDIA_REMOVED.equals(action) || Intent.ACTION_MEDIA_EJECT.equals(action)
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
                DataUtils.getInstance().pauseAllDownload();
            }

            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {

            }
            if (Intent.ACTION_MEDIA_EJECT.equals(action)) {

            }
        }
    }
}
