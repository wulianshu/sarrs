package com.chaojishipin.sarrs.thirdparty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.MD5Utils;


public class Utils {


    private static final String DURATION_FORMAT = "%2d:%02d";
    private static final String DURATION_FORMAT_MORE_THAN_HOUR = "%2d:%02d:%02d";
    public static final int JUDGE_BUFFER = 0;
    public static final long JUDGE_BUFFER_DELAY_TIME = 3000;

    public static String getDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                        Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        } else {
            return "0";
        }
    }

    public static String getChannel() {
        try {
            PackageManager packInfo = ChaoJiShiPinApplication.getInstatnce().getPackageManager();
            if (packInfo != null) {
                ApplicationInfo appInfo =
                        packInfo.getApplicationInfo(ChaoJiShiPinApplication.getInstatnce().getPackageName(),
                                PackageManager.GET_META_DATA);
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
    public static String getSignInfo() {
        try {
            PackageInfo packageInfo = ChaoJiShiPinApplication.getInstatnce().getPackageManager().getPackageInfo(
                    ChaoJiShiPinApplication.getInstatnce().getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }
    private static String parseSignature(byte[] signature){
            try {  
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");  
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));  
                return MD5Utils.md5(cert.getEncoded());
            } catch (CertificateException e) {  
                e.printStackTrace();  
            }  
            return "unknown";
    }
    public static String getVersionName() {
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

    public static int ranInt() {
        Random r = new Random();
        return r.nextInt();
    }

    public static Calendar parseTime(String time, String pattern) {
        SimpleDateFormat formater = new SimpleDateFormat(pattern, Locale.US);
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        try {
            date = formater.parse(time);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    public static String getDurationInString(long duration) {

        if (duration >= 60 && duration < 3600) {
            int minute = (int) (duration / 60);
            int second = (int) (duration % 60);
            return String.format(DURATION_FORMAT, minute, second);
        } else if (duration < 60 && duration > 0) {
            return String.format(DURATION_FORMAT, 00, duration);
        } else if (duration >= 3600) {
            int hour = (int) (duration / 3600);
            int minute = (int) ((duration % 3600) / 60);
            int second = (int) ((duration % 3600) % 60);
            return String.format(DURATION_FORMAT_MORE_THAN_HOUR, hour, minute, second);
        } else {
            return "0:00";
        }
    }

    public static String bytes2humanity(long size) {
        long h_size = size / 1024;
        if (h_size < 1024) {
            return String.format("%dK", h_size);
        } else {
            float m_size = (float) (h_size / 1024.0f);
            return String.format("%.1fM", m_size);
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    public static String getSubchannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains("jrspsubchannel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        if (split != null && split.length >= 2) {
            return split[split.length - 1];

        } else {
            return "";
        }
    }

   /* public static String getTimeOffset(Context context, long timemillis) {
        long now = System.currentTimeMillis() / 1000l;
        int offset = (int) (now - timemillis);
        String time_digital = null; // "30"
        String time_text = null; // "分钟/秒/小时前"
        if (offset >= 0) {
            StringBuilder sb = new StringBuilder();
            if (offset < 60) {
                time_digital = Integer.toString(offset);
                sb.append(time_digital);
                sb.append(" ");
                sb.append(context.getString(R.string.recent_seconds));
                time_text = sb.toString();
            } else if (offset < 60 * 60) {
                time_digital = Integer.toString(offset / 60);
                sb.append(time_digital);
                sb.append(" ");
                sb.append(context.getString(R.string.recent_minutes));
                time_text = sb.toString();
            } else if (offset < 60 * 60 * 24) {
                time_digital = Integer.toString(offset / 3600);
                sb.append(time_digital);
                sb.append(" ");
                sb.append(context.getString(R.string.recent_hours));
                time_text = sb.toString();
            } else {
                Date d = new Date();
                d.setTime(timemillis * 1000);
                SimpleDateFormat formatter = new SimpleDateFormat("M-dd", Locale.getDefault());
                time_text = formatter.format(d);
            }
        }
        return time_text;
    }*/
    public static String getCurrentTimeByPattern(String pattern){
        DateFormat df = new SimpleDateFormat(pattern,Locale.US);
        String currentTime = df.format(new Date());
        return currentTime;
    }
    public static String getBrandName(){
        return Build.BRAND;
    }
    public static String getOSVersionName(){
        return Build.VERSION.RELEASE;
    }
    public static String getModel(){
        return Build.MODEL;
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getSerialNumber() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            return Build.SERIAL;
        }
        return "";
    }
    public static boolean checkPackage(Context context, String packageName){
        PackageManager pm = context.getPackageManager();  
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
        for (PackageInfo info : infoList) {  
            if (packageName.equals(info.packageName)) {  
                return true;
            }
        }
        return false;
    }
}
