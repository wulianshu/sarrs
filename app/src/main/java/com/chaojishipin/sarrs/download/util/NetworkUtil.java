package com.chaojishipin.sarrs.download.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.letv.component.utils.NetWorkTypeUtils;

/**
 * Created by vicky on 15/8/30.
 */
public class NetworkUtil {
    public final static int TYPE_WIFI = 1;
    public final static int TYPE_MOBILE = 2;
    public final static int TYPE_ERROR = -1;

    public static boolean isNetworkAvailable(Context context) {
        boolean isAvailable = false;
        if(null != context){
            NetworkInfo info = getNetworkInfo(context);
            if (info != null && info.isAvailable()) {
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    /*
		 * 判读网络类别并作出相应提示并返回相应网络类型用
		 * 0 无网 1 2g/3g 2 wifi 4无效
		 *
		 * qinguoli
		 *
		 * */
    public static int checkNet(Context context, Intent intent, int priorNetType){

        //初始化为4 无效
        int nowNetType = 4;

        String action = intent.getAction();
        if (null == action || "".equals(action))
            nowNetType = 4;
        if (action != null && action.equals(PlayerUtils.CONNECTIVTY_CHANGE)) {
            int netType = NetWorkTypeUtils.getNetType(context);

            if (NetWorkTypeUtils.NETTYPE_NO == netType) {
                if(0 != priorNetType){
                   // ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), R.string.nonet_tip);
                }
//                ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), R.string.nonet_tip);
                nowNetType = 0;
            } else if (NetWorkTypeUtils.NETTYPE_2G == netType || NetWorkTypeUtils.NETTYPE_3G == netType ) {

                if(1 != priorNetType){
                    ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), R.string.moblie_net_tip_new);
                }

                nowNetType = 1;


            }else if(NetWorkTypeUtils.NETTYPE_WIFI == netType){

                if(2 != priorNetType){

                    ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), R.string.wifi_tip);
                }

                nowNetType = 2;

            }


        }



        return nowNetType;
    }

    public static NetworkInfo getNetworkInfo(Context context){
        try{
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager){
                return connectivityManager.getActiveNetworkInfo();
            }
        }catch(Exception e){

        }
        return null;
    }

    public static int reportNetType(Context context) {
        int netMode = TYPE_ERROR;

        try {
            NetworkInfo info = getNetworkInfo(context);
            if (info != null && info.isAvailable()) {
                int netType = info.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    netMode = TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    netMode = TYPE_MOBILE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return netMode;
    }
}
