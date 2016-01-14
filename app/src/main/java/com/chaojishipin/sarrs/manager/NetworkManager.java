package com.chaojishipin.sarrs.manager;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.view.KeyEvent;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.interfaces.INetWorkObServe;
import com.chaojishipin.sarrs.receiver.NetWorkStateReceiver;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.widget.PopupDialog;

import java.util.List;

/**
 * Created by vicky on 15/11/6.
 */
public class NetworkManager implements INetWorkObServe {
    private NetWorkStateReceiver mNetWorkReceiver;
    private Context mContext;
    private static NetworkManager manager;
    private AlertDialog.Builder customBuilder;
    private Dialog dialog;


    public static NetworkManager getInstance()
    {
        if (manager == null)
        {
            synchronized (NetworkManager.class)
            {
                if (manager == null)
                    manager = new NetworkManager();
            }
        }
        return manager;
    }

    private NetworkManager()
    {
    }

    public void registerReceiver(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.mNetWorkReceiver = new NetWorkStateReceiver();
        this.mNetWorkReceiver.setmNetWorkObserve(this);
//        if (context != null) {
            context.registerReceiver(this.mNetWorkReceiver, filter);
//        }
    }

    private void unRegisterReceiver() {
        if (null != mNetWorkReceiver) {
            mContext. unregisterReceiver(mNetWorkReceiver);
            mNetWorkReceiver = null;
        }
    }

    @Override
    public void observeNetWork(String netName, int netType, boolean isHasNetWork) {
        if (netType == ConstantUtils.NET_TYPE_ERROR) {
        } else {
            if (netType == ConnectivityManager.TYPE_WIFI) {
//                LogUtil.e("xll", "base net wifi execute childactivity");
                if (isHasNetWork) {
                    dismissDialog();
                    ChaoJiShiPinApplication.getInstatnce().getDownloadManager().continueDownload();
                }
            }else{
                if (isHasNetWork)
                {
                    processMobileDownload();
                }

            }
            //判断什么网络类型
        }
    }

    private void processMobileDownload()
    {
        boolean result = ChaoJiShiPinApplication.getInstatnce().getDownloadManager().needContinueDownload();
        if(result) {
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().pauseDownloadingJob();
            isContinudownload();
        }
    }

    private void isContinudownload(){
//        Context context = ChaoJiShiPinApplication.getInstatnce();
        PopupDialog.showMobileNetworkAlert(buttonClick);
//        if (customBuilder != null)
//            return;
//        customBuilder = new AlertDialog.Builder(AllActivityManager.getInstance().getCurrentActivity());
//        customBuilder
//                .setTitle(R.string.tip)
//                .setMessage(R.string.wireless_tip)
//                .setPositiveButton(R.string.continue_download,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//
//                                ChaoJiShiPinApplication.getInstatnce().getDownloadManager().continueDownload();
//
//                                dismissDialog();
//                            }
//                        })
//                .setNegativeButton(R.string.pause_download,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                ChaoJiShiPinApplication.getInstatnce().getDownloadManager().pauseAllJobs();
//                                dismissDialog();
//                            }
//                        })
//                .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                            dialog.dismiss();
//                        }
//                        return false;
//                    }
//                });
//        dialog = customBuilder.create();
//        dialog.show();
    }

    private void dismissDialog()
    {
        if (customBuilder != null)
        {
            dialog.dismiss();
            customBuilder = null;
        }
    }

    PopupDialog.PopupButtonClickInterface buttonClick = new PopupDialog.PopupButtonClickInterface() {
        @Override
        public void onLeftClick() {
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().continueDownload();
        }

        @Override
        public void onRightClick() {
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().pauseAllJobs();
//            Intent intent = new Intent();
//            intent.setAction(BROADCAST_ACTION);
//            intent.putExtra("name", "qqyumidi");
//            sendBroadcast(intent);
        }
    };

}
