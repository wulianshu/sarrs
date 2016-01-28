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
import com.chaojishipin.sarrs.utils.DataUtils;
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
                    DataUtils.getInstance().startAllDownload();
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
        boolean result = DataUtils.getInstance().needContinueDownload();
        if(result) {
            DataUtils.getInstance().pauseAllDownload();
            isContinudownload();
        }
    }

    private void isContinudownload(){
        PopupDialog.showMobileNetworkAlert(buttonClick);
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
            DataUtils.getInstance().startAllDownload();
        }

        @Override
        public void onRightClick() {
            DataUtils.getInstance().pauseAllDownload();
        }
    };
}
