package com.chaojishipin.sarrs.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.chaojishipin.sarrs.interfaces.INetWorkObServe;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;

import java.util.ArrayList;

/**
 * 网络状态观察者 add by zhangshuo
 */
public class PackageStartReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //刚进入界面会读取网络状态
       if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED)){
            LogUtil.e("xll","pkg remove "+intent.getDataString());
        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)){

            LogUtil.e("xll", "pkg add ");
           Intent startIntent=new Intent();
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startIntent.setComponent(new ComponentName("com.chaojishipin.sarrs","ChaojishipinSplashActivity"));
           context.startActivity(startIntent);

        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)){
           LogUtil.e("xll", "pkg repalace ");
           Intent startIntent=new Intent();
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startIntent.setComponent(new ComponentName("com.chaojishipin.sarrs","ChaojishipinSplashActivity"));
           context.startActivity(startIntent);

       }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MY_PACKAGE_REPLACED)){
           LogUtil.e("xll", "my pkg repalace ");
           Intent startIntent=new Intent();
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startIntent.setComponent(new ComponentName("com.chaojishipin.sarrs","ChaojishipinSplashActivity"));
           context.startActivity(startIntent);



       }


    }


}
