package com.chaojishipin.sarrs.download.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.bean.VideoDataBean;
import com.chaojishipin.sarrs.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoviesUtils {

    private final static String TAG = "MoviesUtils";
    
    private static String mOSVersion = "";
    
    private static String mDeviceModel = "";
    
    // public static String timestamp2year(long times){
    // Calendar cal = Calendar.getInstance();
    // cal.setTimeInMillis(times);
    // return Integer.toString(cal.get(cal.YEAR));
    //
    // }
    public static String timestamp2Recent(long times, Context context) {
        Resources res = context.getResources();
        long now = System.currentTimeMillis();
        long delta = (now - times) / 1000;
        if (delta < 60) {
            return res.getString(R.string.time_less_than_1_min);
        } else if (delta < 60 * 60) {
            return String.format(res.getString(R.string.time_less_than_1_hour), delta / 60);
        } else if (delta < 24 * 60 * 60) {
            return String.format(res.getString(R.string.time_less_than_1_day), delta / (60 * 60));
        } else {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("M-dd", Locale.CHINESE);
            return localSimpleDateFormat.format(new Date(times));
        }
    }


    public static String seekHistroyTimeFormat(long seekHistroy, Context context) {

        Resources res = context.getResources();
        long tempSeekHistroy = seekHistroy / 1000;
        String formatSeekHistroy = null;
        if (tempSeekHistroy < 60) {

            formatSeekHistroy = res.getString(R.string.time_less_than_1_min);
        } else if (tempSeekHistroy < 10 * 60) {

            formatSeekHistroy =
                    String.format(res.getString(R.string.time_less_than_ten_minute),
                            tempSeekHistroy / 60, tempSeekHistroy % 60);

        } else if (tempSeekHistroy < 60 * 60) {
            formatSeekHistroy =
                    String.format(res.getString(R.string.time_less_than_1_hour_histroy),
                            tempSeekHistroy / 60, tempSeekHistroy % 60);
        } else {
            long second = tempSeekHistroy % 60;
            formatSeekHistroy =
                    String.format(res.getString(R.string.time_more_than_1_hour),
                            tempSeekHistroy / 60, second >= 10 ? "" + second : "0" + second);
        }
        return formatSeekHistroy;
    }



    /**
     * 得到客户端版本名
     */
    public static String getClientVersionName() {
        try {
            PackageInfo packInfo =
                    ChaoJiShiPinApplication.getInstatnce().getPackageManager()
                            .getPackageInfo(ChaoJiShiPinApplication.getInstatnce().getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 得到客户端版本号
     */
    public static int getClientVersionCode() {
        try {
            PackageInfo packInfo =
                    ChaoJiShiPinApplication.getInstatnce().getPackageManager()
                            .getPackageInfo(ChaoJiShiPinApplication.getInstatnce().getPackageName(), 0);
            return packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getClientChannel() {
        try {
            PackageManager packInfo = ChaoJiShiPinApplication.getInstatnce().getPackageManager();
            if (packInfo != null) {
                ApplicationInfo appInfo =
                        packInfo.getApplicationInfo(ChaoJiShiPinApplication.getInstatnce()
                                .getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo != null) {
                    Bundle b = appInfo.metaData;
                    Object obj = b.get("UMENG_CHANNEL");
                    return obj.toString();
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String optString(String str, String defStr) {
        if (TextUtils.isEmpty(str)) {
            return defStr;
        } else {
            return str;
        }
    }

    /**
     * 获取海报图上的描述
     * 
     * @param video
     * @return zhangshuo 2014年6月5日 下午4:08:53
     */
    public static String getEpisodeInfo(Context context, VideoDataBean video) {
        if (null != video && !TextUtils.isEmpty(video.getVt())) {
            String vt = video.getVt();
            // 如果当前的影片类型为电影
            if (MoviesConstant.VT_MOVIE.equals(vt)) {
                return String.format("%.1f", video.getRating());
            } else if (MoviesConstant.VT_CARTOON.equals(vt) || MoviesConstant.VT_TV.equals(vt)) {
                if (!TextUtils.isEmpty(video.getIsend())
                        && !TextUtils.isEmpty(video.getNowepisodes())) {
                    StringBuffer nameBuff = new StringBuffer();
                    String isSend = video.getIsend();
                    // 如果正在更新剧集
                    if (MoviesConstant.EPISODE_UPDATE.equals(isSend)) {
                        nameBuff.append(context.getString(R.string.updateto));
                        nameBuff.append(video.getNowepisodes());
                        nameBuff.append(context.getString(R.string.episode));
                    } else if (MoviesConstant.EPISODE_UPDATE_END.equals(isSend)) {
                        // 如果是全集
                        nameBuff.append(video.getNowepisodes());
                        nameBuff.append(context.getString(R.string.episode_total));



                    }
                    return nameBuff.toString();
                }
            } else if (MoviesConstant.VT_ZONGYI.equals(vt)) {
            	try {
            		return video.getEpisodeList().get(0).getPorder();
				} catch (Exception e) {
					e.printStackTrace();
				}
                return video.getNowepisodes();
            } else {
                return String.format("%.1f", video.getRating());
            }
        }
        return null;
    }


    public static String getPhoneImei() {
        String phoneImei = "";
        String UNKNOWN_IMEI = "ImeiUnknown";
        try {
            TelephonyManager telephonyManager = null;
            telephonyManager =
                    (TelephonyManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                            Context.TELEPHONY_SERVICE);
//            LogUtils.i(TAG, "after get system service.");
            if (null != telephonyManager) {
                phoneImei = telephonyManager.getDeviceId();
                Log.i(TAG, "mPhoneImei = " + phoneImei);
                if (phoneImei == null) {
                    phoneImei = UNKNOWN_IMEI;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            phoneImei = UNKNOWN_IMEI;
            Log.i(TAG, "in the catch.");
        }
        return phoneImei;
    }

    
    public static String getOSVersion() {
        if (TextUtils.isEmpty(mOSVersion)) {
            mOSVersion = android.os.Build.VERSION.RELEASE;
        }
        return mOSVersion;
    }

    public static String getDeviceModel() {
        if(StringUtil.isEmpty(mDeviceModel)){
            try {
                mDeviceModel = URLEncoder.encode(android.os.Build.MODEL, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return mDeviceModel;
    }
    
    public static String getMacAddress() {
        try {
            WifiManager wifiManager =
                    (WifiManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                            Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
