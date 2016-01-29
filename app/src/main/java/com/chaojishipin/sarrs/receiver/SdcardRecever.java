package com.chaojishipin.sarrs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.StoragePathsManager;

/**
 * Created by xulinlin on 2016/1/28.
 */
public class SdcardRecever extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED)){

            LogUtil.e("v1.2.0","sdcard insert");

            StoragePathsManager.getInstanse(ChaoJiShiPinApplication.getInstatnce()).getStoragePaths();



        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_CHECKING)){


            LogUtil.e("v1.2.0","sdcard checking");

        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_UNMOUNTED)){

            StoragePathsManager.getInstanse(ChaoJiShiPinApplication.getInstatnce()).getStoragePaths();
            //media unmounted reset user setting
            SPUtil.getInstance().putString("sdcardbyuser","");

            LogUtil.e("v1.2.0","sdcard unmounted clear user setting");
        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_EJECT)){

            StoragePathsManager.getInstanse(ChaoJiShiPinApplication.getInstatnce()).getStoragePaths();
            //media unmounted reset user setting
            SPUtil.getInstance().putString("sdcardbyuser","");

            LogUtil.e("v1.2.0","sdcard eject clear user setting");
        }else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_REMOVED)){
            StoragePathsManager.getInstanse(ChaoJiShiPinApplication.getInstatnce()).getStoragePaths();
            //media unmounted reset user setting
            SPUtil.getInstance().putString("sdcardbyuser","");

            LogUtil.e("v1.2.0","sdcard removed clear user setting");
        }




    }
}
