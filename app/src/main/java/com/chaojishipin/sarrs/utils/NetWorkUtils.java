package com.chaojishipin.sarrs.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

/**
 * @author daipei
 *         网络工具类
 */
public class NetWorkUtils {

    //走流量
    public static final int NETTYPE_GSM = 0;

    public static final int NETTYPE_4G = 4;

    public static final int NETTYPE_NO = -1;
    public static final int NETTYPE_WIFI = 1;
    public static final int NETTYPE_2G = 2;
    public static final int NETTYPE_3G = 3;

    /**
     * 获得网络信息
     */
    public static NetworkInfo getAvailableNetWorkInfo() {
        NetworkInfo activeNetInfo = null;
        try {
            if( ChaoJiShiPinApplication.getInstatnce()!=null){
                ConnectivityManager connectivityManager = (ConnectivityManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                activeNetInfo = connectivityManager.getActiveNetworkInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (activeNetInfo != null && activeNetInfo.isAvailable()) {
            return activeNetInfo;
        } else {
            return null;
        }
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetAvailable() {
        boolean isAvailable = false;
        NetworkInfo info = getAvailableNetWorkInfo();
        if (info != null && info.isAvailable()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * 判断是否是wifi
     *
     * @return
     */
    public static boolean isWifi() {
        NetworkInfo networkInfo = getAvailableNetWorkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得网络类型：wifi/2G/3G/4G 1 2 3 4
     *
     * @return
     */
    public static int getNetType() {
        NetworkInfo networkInfo = getAvailableNetWorkInfo();

        if (networkInfo != null && networkInfo.isAvailable()) {
            if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                return NETTYPE_WIFI;
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                        Context.TELEPHONY_SERVICE);

                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return NETTYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETTYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETTYPE_4G;
                    default:
                        return NETTYPE_4G;
                }
            }
        } else {
            return NETTYPE_NO;
        }
    }

    public static String getNetInfo() {
        String netType = null;
        int nt = NetWorkUtils.getNetType();
        switch (nt) {
            case NETTYPE_WIFI:
                netType = "wifi";
                break;
            case NETTYPE_4G:
                netType = "4g";
                break;
            case NETTYPE_3G:
                netType = "3g";
                break;
            case NETTYPE_2G:
                netType = "2g";
                break;
            case NETTYPE_NO:
                netType = "";
                break;
            default:
                netType = "";
                break;
        }
        return netType;
    }

}
