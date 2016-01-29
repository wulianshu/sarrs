package com.chaojishipin.sarrs.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.fragment.ChaoJiShiPinBaseFragment;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhangshuo on 2015/6/1.
 */
public class Utils {
    public final static String LIVE_TAG = "live";
    public static final String VIDEO_TITLE = "title";
    public final static int SDCARD_MINSIZE = 500; //1.48G--1515.52   1335--1.3037   1500--1.46G    // 9.04G--->9256.96M
    public static final int GET_JS_RESULT = 300;
    public static final int EXECUTE_JS_TIME = 300;

    public static final String PLAY_3G_NET = "3G_play";
    public static final String PLAY_DATA = "play_data";
    public static final String LIVE_PLAY_DATA = "live_play_data";
    public static final String LIVEDATAENTITY = "livedataentity";


    public static final String Medea_Mode = "media";
    /**
     * SD 卡路径
     */
    public final static String SDCARD_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    /**
     * 创建快看文件夹路径
     */
    public final static String SAVE_FILE_PATH_DIRECTORY = SDCARD_PATH + "/" + getDownLoadFolder();

    public static final byte[] AES_KEY = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54};

    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(ConstantUtils.DEVICE_ID)) {
            return ConstantUtils.DEVICE_ID;
        } else if (context == null) {
            return "";
        } else {
            try {
                String deviceId = ((TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE)).getDeviceId();
                if (null == deviceId || deviceId.length() <= 0) {
                    return "";
                } else {
                    ConstantUtils.DEVICE_ID = deviceId.replace(" ", "");
                    return deviceId.replace(" ", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 获得屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getHeightPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * Base64解码
     *
     * @param data
     * @return xll 2014年8月27日 下午6:20:57
     */
    public static String getBase64Decode(String data) {
        try {
            byte[] reuslt = Base64.decode(data, Base64.DEFAULT);
            return new String(reuslt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 时间戳--->date xxxx年xx月xx日
     */
    public static String getVeiwTimeTag(String timeStamp) {
        String str = "";
        if (timeStamp != null) {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = s.format(new Date(Long.valueOf(timeStamp)));

            if (dateStr != null) {
                String[] ds = dateStr.split(" ")[0].split("-");
                str = str + ds[0] + ChaoJiShiPinApplication.getInstatnce().getString(R.string.year) + ds[1] + ChaoJiShiPinApplication.getInstatnce().getString(R.string.month) + ds[2] + ChaoJiShiPinApplication.getInstatnce().getString(R.string.day);
            }

            LogUtil.e("xll", "date " + str);
        }

        return str;


    }


    /**
     * 获得屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getWidthPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取控件宽
     *
     * @return 像素
     */
    public static int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /**
     * 获取控件高
     *
     * @return 像素
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
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
        } catch (PackageManager.NameNotFoundException e) {
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
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得imei
     */
    public static String getPhoneImei() {
        String phoneImei = "";
        String UNKNOWN_IMEI = "ImeiUnknown";
        try {
            TelephonyManager telephonyManager = null;
            telephonyManager =
                    (TelephonyManager) ChaoJiShiPinApplication.getInstatnce().getSystemService(
                            Context.TELEPHONY_SERVICE);
            if (null != telephonyManager) {
                phoneImei = telephonyManager.getDeviceId();
                if (phoneImei == null) {
                    phoneImei = UNKNOWN_IMEI;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            phoneImei = UNKNOWN_IMEI;
        }
        return phoneImei;
    }


    /**
     * 根据cid 展示 图片资源
     */

    public static int loadUrl(String contentType, String cid, boolean flag) {
        int reId = 0;
        if (contentType.equalsIgnoreCase("10")) {

        } else if (contentType.equalsIgnoreCase("7")) {
            switch (Integer.parseInt(cid)) {
                // 精彩推荐
                case 0:
                    reId = flag ? R.drawable.main_recoment_press : R.drawable.main_carton_normal;
                    break;
                // 电视剧

                case 1:
                    reId = flag ? R.drawable.main_dsj_press : R.drawable.main_dsj_normal;
                    break;
                // 电影

                case 2:
                    reId = flag ? R.drawable.main_movie_press : R.drawable.main_movie_normal;
                    break;
                // 动漫

                case 3:
                    reId = flag ? R.drawable.main_carton_press : R.drawable.main_carton_normal;

                    break;


                // 综艺
                case 4:
                    reId = flag ? R.drawable.main_zy_press : R.drawable.main_zy_normal;

                    break;
                //纪录片

                case 16:
                    reId = flag ? R.drawable.main_jlp_press : R.drawable.main_jlp_normal;

                    break;

            }
            // 专题
        } else if (contentType.equalsIgnoreCase("8")) {
            reId = flag ? R.drawable.main_specail_press : R.drawable.main_specail_normal;
        } else if (contentType.equalsIgnoreCase("9")) {
            reId = flag ? R.drawable.main_rank_press : R.drawable.main_rank_normal;
        }


        return reId;


    }


    /**
     * 获取系统版本字符串
     *
     * @return
     */
    public static String getOSVersion() {
        String mOSVersion = android.os.Build.VERSION.RELEASE;
        return mOSVersion;
    }

    /**
     * 获取cpu核心数
     */
    public static int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Print exception
            e.printStackTrace();
            // Default to return 1 core
            return 1;
        }
    }

    public static String cpu_result = "";

    // 获取CPU最大频率
    // "/system/bin/cat" 命令行
    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
    public static String getMaxCpuFreq() {
        if (!TextUtils.isEmpty(cpu_result) && !"N/A".equals(cpu_result)) {
            return cpu_result;
        } else {
            ProcessBuilder cmd;
            try {
                String[] args =
                        {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
                cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[24];
                while (in.read(re) != -1) {
                    cpu_result = cpu_result + new String(re);
                }
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                cpu_result = "";
            }
            return cpu_result.trim();
        }
    }

    public static String getDeviceModel() {
        String mDeviceModel = null;
        try {
            mDeviceModel = URLEncoder.encode(android.os.Build.MODEL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mDeviceModel;
    }

    /**
     * 获取mac地址
     *
     * @return
     */
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = ChaoJiShiPinApplication.getInstatnce().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    /**
     * 获取包名
     *
     * @return
     */
    public static String getSystemPackageName() {
        try {
            return ChaoJiShiPinApplication.getInstatnce().getApplicationContext().getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取android系统版本
     *
     * @return
     */
    public static int getAPILevel() {
        try {
            return android.os.Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static byte[] AES256_Encode(String str) {
        try {
//            // 获取KeyGenerator对象
//            KeyGenerator kgen = KeyGenerator.getInstance("AES");
//            // 设置加密密匙位数，目前支持128、192、256位
//            kgen.init(keySize);
//            // 获取密匙对象
//            SecretKey skey = kgen.generateKey();
//            // 获取随机密匙
//            byte[] raw = skey.getEncoded();
            // 初始化SecretKeySpec对象
            SecretKeySpec skeySpec = new SecretKeySpec(ConstantUtils.AES_KEY, "AES");
            // 初始化Cipher对象
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 用指定密匙对象初始化加密Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            // 加密字符串
            return cipher.doFinal(str.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String AES256_decode(byte[] base64plain, byte[] aesKey) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec k = new SecretKeySpec(aesKey, "AES");
            byte[] byteContent = base64plain;
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] result = c.doFinal(byteContent);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证密码是否是 中英文，数字和字母，长度4-30,区分大小写
     */
    public static int passwordFormat(String password) {
        String hzStr = "";
        String enStr = "";
        if (password == null)
            return -1;
        String regular = "^[\\u4e00-\\u9fa50-9a-zA-Z_]+$";
        String regular1 = "[^!\\u4e00-\\u9fa5]";
        String regular2 = "[^!_a-zA-Z0-9]";

        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            return -2;
        } else {

            Pattern pattern1 = Pattern.compile(regular1);
            Matcher matcher1 = pattern1.matcher(password);

            hzStr = matcher1.replaceAll("");
            LogUtil.e("Utils", "hzStr " + hzStr);
            Pattern pattern2 = Pattern.compile(regular2);
            Matcher matcher2 = pattern2.matcher(password);
            enStr = matcher2.replaceAll("");
            LogUtil.e("Utils", "enStr " + enStr);
            int totalSize = hzStr.length() * 2 + enStr.length();
            if (totalSize >= 4 && totalSize <= 30) {
                return 0;
            } else {
                return -3;
            }


        }
    }

    /**
     * 验证注册手机号码是否正确
     */
    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null) {
            return false;
        }
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 设置手机号显示格式 xxx xxxx  xxxx
     */
    public static String buildFormatPhone(String str) {
        String sub1 = str.substring(0, 3);
        String sub2 = str.substring(3, 7);
        String sub3 = str.substring(7, 11);
        return sub1 + " " + sub2 + " " + sub3;


    }

    /**
     * 设置文字前景色
     */
    public static void addForeGroundColor(TextView tv, int colorId, int start, int end) {
        SpannableStringBuilder builder = new SpannableStringBuilder(tv.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(colorId);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
    }

    /**
     * android4.4后，SD 卡下载的系统目录
     */
    public static String getDownLoadFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append("Android/data/")
                .append(ChaoJiShiPinApplication.getInstatnce().getPackageName()).append("/files/Movies").toString();
        return sb.toString();
    }


    /*
 *   判断是否是横屏或或者竖屏
 *
 *   @return true 横屏
 * */
    public static boolean getScreenOrientation() {

        if (ChaoJiShiPinApplication.getInstatnce().getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;

        } else if (ChaoJiShiPinApplication.getInstatnce().getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return false;

        }
        return false;

    }

    /**
     * 获取手机型号
     * zhangshuo
     * 2015年1月29日 下午4:33:40
     */
    public static String getDeviceMode() {
        try {
            return android.os.Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取操作系统版本
     * zhangshuo
     * 2015年1月29日 下午4:34:16
     */
    public static String getDeviceVersion() {
        try {
            return android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机当前是否锁屏
     *
     * @return zhangshuo 2014年4月29日 下午5:23:19
     */
    public static boolean getScreenLockStatus() {
        Context context = ChaoJiShiPinApplication.getInstatnce();
        if (null == context) {
            return true;
        }

        try {
            KeyguardManager mkeyguardManager =
                    (KeyguardManager) context.getSystemService("keyguard");
            return (null != mkeyguardManager && mkeyguardManager.inKeyguardRestrictedInputMode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 获取重定向之后的网址信息
     */
    public static void getRedirectInfo(final String url) {
        final Handler mHanler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "xxx";
                mHanler.sendMessage(msg);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                RedirectHandler redirectHandler = new RedirectHandler();
                httpClient.setRedirectHandler(redirectHandler);
                HttpContext httpContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(url);
                try {
                    //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
                    HttpResponse response = httpClient.execute(httpGet, httpContext);
                    //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
                    HttpHost targetHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                    //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
                    HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                    System.out.println("主机地址:" + targetHost);
                    System.out.println("URI信息:" + realRequest.getURI());
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        //System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    httpClient.getConnectionManager().shutdown();
                }
                Looper.loop();


            }
        }).start();


    }

   /**
    *  获取屏幕宽度
    * */
    public static int getScreenWidth(Context context) {
        // 获取小屏播放器高度
        // 获取小屏播放器高度
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        int mMedieHeight = width * 9 / 16;
        return width;

    }
    /**
     *  获取屏幕高度
     *  @param context  activity context
     * */

    public static  int getScreenHeight(Context context) {
        // 获取小屏播放器高度
         DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）

        return height;

    }

    /**
     *  获取系统版本号
     * */
    public static String getSystemVer(){
        return Build.VERSION.RELEASE;
    }
    /**
     *  获取手机型号
     * */

    public static String getMobileType(){
        return Build.MODEL;
    }
     public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view.getWidth();
        int height = view.getHeight();
        int statusBarHeight = getStatusBarHeight();
        // y=y-statusBarHeight;
        LogUtil.e("xll", "rectx touch" + ev.getX());
        LogUtil.e("xll", "recty touch" + ev.getY());
        LogUtil.e("xll", "rect buttonX " + x);
        LogUtil.e("xll", "rect buttony " + y);
        LogUtil.e("xll", "rect buttonw " + width);
        LogUtil.e("xll", "rect buttonH " + height);
        LogUtil.e("xll", "rect buttonTop " + view.getTop());
        LogUtil.e("xll", "rect buttonBottom " + view.getBottom());
        LogUtil.e("xll", "rect buttonLeft " + view.getLeft());
        LogUtil.e("xll", "rect buttonRight " + view.getRight());
        int titleHeight = Utils.dip2px(41);
        float calY = ev.getY() + titleHeight + statusBarHeight;
        LogUtil.e("xll", "rect calY " + calY);

        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || calY < y || calY > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    public static int getStatusBarHeight() {
        Resources resources = ChaoJiShiPinApplication.getInstatnce().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }




    protected void setLoadings(ImageView coverView, String url, int failid) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(failid)
                .showImageForEmptyUri(failid)
                .displayer(new RoundedBitmapDisplayer(200)).build();
        ImageLoader.getInstance().displayImage(url,coverView,options);
    }
/**
 *  @param streamFormat M3U8 MP4
 *  @param requestFormat REAL SUPER  HIGH NORMAL
 * */
  public static int getPriority(String streamFormat,String requestFormat){
      // 默认优先级
      int priority=1;
      if(TextUtils.isEmpty(streamFormat)||TextUtils.isEmpty(requestFormat)){
          return priority;
      }
      if(streamFormat.equalsIgnoreCase(ConstantUtils.OutSiteDateType.M3U8)){

          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.REAL)){
               priority=9;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER)){
              priority=1;
          }

          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER2)){
              priority=3;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.HIGH)){
              priority=5;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.NORMAL)){
              priority=7;
          }
      }else if(streamFormat.equalsIgnoreCase(ConstantUtils.OutSiteDateType.MP4)){

          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.REAL)){
              priority=10;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER)){
              priority=2;
          }

          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER2)){
              priority=4;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.HIGH)){
              priority=6;
          }
          if(requestFormat.equalsIgnoreCase(ConstantUtils.ClarityType.NORMAL)){
              priority=8;
          }


      }
      return priority;

  }

    /**
     *  @param os M3U8 MP4
     *  @param format REAL SUPER  HIGH NORMAL
     * */
    public static int getPriority4Download(String os,String format){
        // 默认优先级 越低越高
        int priority=11;
        if(TextUtils.isEmpty(os)||TextUtils.isEmpty(format)){
            return priority;
        }
        if(os.equalsIgnoreCase(ConstantUtils.OutSiteDateType.MP4)){
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER2)){
                priority=1;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER)){
                priority=2;
            }

            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.HIGH)){
                priority=3;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.NORMAL)){
                priority=4;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.REAL)){
                priority=5;
            }
        }else if(os.equalsIgnoreCase(ConstantUtils.OutSiteDateType.M3U8)){

            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER2)){
                priority=6;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.SUPER)){
                priority=7;
            }

            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.HIGH)){
                priority=8;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.NORMAL)){
                priority=9;
            }
            if(format.equalsIgnoreCase(ConstantUtils.ClarityType.REAL)){
                priority=10;
            }
        }
        return priority;
    }

    /**
     * 获取外置SD卡路径
     *  命令行方式
     * @return
     */
    public static List<String> getSDCardPaths() {
        List<String> sdcardPaths = new ArrayList<String>();
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                LogUtil.i("CommonUtil:getSDCardPath", lineStr);

                String[] temp = TextUtils.split(lineStr, " ");
                // 得到的输出的第二个空格后面是路径
                String result = temp[1];
                File file = new File(result);
                if (file.isDirectory() && file.canRead() && file.canWrite()) {
                    LogUtil.d("directory can read can write:",
                            file.getAbsolutePath());
                    // 可读可写的文件夹未必是sdcard，我的手机的sdcard下的Android/obb文件夹也可以得到
                    sdcardPaths.add(result);

                }

                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                    LogUtil.e("CommonUtil:getSDCardPath", "命令执行失败!");
                }
            }
            inBr.close();
            in.close();
        } catch (Exception e) {
            LogUtil.e("CommonUtil:getSDCardPath", e.toString());

            sdcardPaths.add(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
        }

        optimize(sdcardPaths);
        for (Iterator iterator = sdcardPaths.iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
            Log.e("清除过后", string);
        }
        return sdcardPaths;
    }


    public static void testStorage(Context context){
        LogUtil.e("sdcardTest", "--start--");
        StringBuilder sb=new StringBuilder();
        sb.append("begin");
        sb.append("state:");
        sb.append(Environment.getExternalStorageState());
        sb.append("\n");
        sb.append("root:");
        sb.append(Environment.getRootDirectory());
        sb.append("\n");
        sb.append("getExternalStorageDirectory:");
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("\n");
        sb.append("end");
        LogUtil.e("sdcardTest", "--sdcardinfo " + sb.toString());
        String storageName="storage.html";
        FileUtils.writeHtmlToData(context, storageName, sb.toString());
        LogUtil.e("sdcardTest", "--end--");

    }



    public static  boolean getRootAhth()
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0)
            {
                return true;
            } else
            {
                return false;
            }
        } catch (Exception e)
        {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }




   /* public static String[] getSdcardPathByInvoke(Context context){

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
   // 获取sdcard的路径：外置和内置
        String[] paths=null;
        paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null)

    }
*/
    private static void optimize(List<String> sdcaredPaths) {
        if (sdcaredPaths.size() == 0) {
            return;
        }
        int index = 0;
        while (true) {
            if (index >= sdcaredPaths.size() - 1) {
                String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
                for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
                    if (sdcaredPaths.get(i).contains(lastItem)) {
                        sdcaredPaths.remove(i);
                    }
                }
                return;
            }

            String containsItem = sdcaredPaths.get(index);
            for (int i = index + 1; i < sdcaredPaths.size(); i++) {
                if (sdcaredPaths.get(i).contains(containsItem)) {
                    sdcaredPaths.remove(i);
                    i--;
                }
            }

            index++;
        }

    }

    public static void destroyWebView(WebView wb){
        if(wb == null)
            return;
        if(wb.getParent() != null){
            try{
                ((ViewGroup)wb.getParent()).removeView(wb);
            }catch(Throwable e){
                e.printStackTrace();
            }
        }
        wb.removeAllViews();
        wb.destroy();
    }
	
	/**
     * 按照指定格式返回今天日期字符串
     *
     * @param format 格式
     * @return
     */
    public static String getTodayStr(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }
}
