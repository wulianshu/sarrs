package com.chaojishipin.sarrs;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.dao.DBHelper;
import com.chaojishipin.sarrs.dao.DatabaseManager;
import com.chaojishipin.sarrs.download.download.DownloadManager;
import com.chaojishipin.sarrs.download.service.DownloadService;
import com.chaojishipin.sarrs.manager.NetworkManager;
import com.chaojishipin.sarrs.thirdparty.ShareConstants;
import com.chaojishipin.sarrs.thirdparty.umeng.UMengAnalysis;
import com.chaojishipin.sarrs.utils.ChannelUtil;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogoImageLoader;
import com.chaojishipin.sarrs.utils.SarrsManager;
import com.chaojishipin.sarrs.utils.Utils;
import com.ibest.thirdparty.share.model.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executor;

/**
 * Created by zhangshuo on 2015/5/29.
 */
public class
        ChaoJiShiPinApplication extends Application {

    private static ChaoJiShiPinApplication mInstatnce;

    private static DisplayImageOptions defaultoptions;
    private IWXAPI mApi;

    /**
     * 下载管理类
     */
    private DownloadManager mDownloadManager;
    /**
     * 下载限速
     */
    private boolean isSpeedCut = false;
    private Stack<Activity> activityStack;

    private Bitmap mBitmap;

    public void setBitmap(Bitmap bm){
        mBitmap = bm;
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channelname = ChannelUtil.getCurrentChannel(this);
        ConstantUtils.CHANNEL_NAME = channelname;
        UMengAnalysis.setCatchUncaughtExceptions(true);
        //这么做是为了解决APP中多个独立进程或多个Service导致Application不断重启
        //http://www.cnblogs.com/0616--ataozhijia/p/4203433.html
        String processName = getProcessName(this, android.os.Process.myPid());
        if (!TextUtils.isEmpty(processName)) {
            boolean defaultProcess = processName.equals(this.getPackageName());
            if (defaultProcess) {
                mInstatnce = this;
                SarrsManager.init(this);
                SarrsManager.setHttpTest(true);
                //加载ImageLoader
                initImageLoader();
                // 注册微信授权认证
                regToWx();

                CrashHandler crashHandler = CrashHandler.getInstance();
                // 注册crashHandler
                crashHandler.init(getApplicationContext());
            }
        }
//        mInstatnce = this;
//        NetworkManager.getInstance();
        mDownloadManager = new DownloadManager(getApplicationContext());
//        activityStack = new Stack<Activity>();
        DatabaseManager.initializeInstance(new DBHelper(getApplicationContext()));
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                //execute the task
//                startNetworkObserveService();
//            }
//        }, 3000);
        startNetworkObserveService();
    }

    public IWXAPI getmApi() {
        return mApi;
    }

    public void setmApi(IWXAPI mApi) {
        this.mApi = mApi;
    }

    /**
     * 注册应用APP_ID到微信 zhangshuo 2014年5月21日 下午5:10:30
     */
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI实例
        mApi = WXAPIFactory.createWXAPI(this, ShareConstants.WEIXIN_APP_ID, true);
        // 将应用的appId注册到微信
        mApi.registerApp(ShareConstants.WEIXIN_APP_ID);
    }

    void initImageLoader() {
        defaultoptions =
                new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.color.color_e7e7e7)
                                // resource or drawable
                        .showImageForEmptyUri(R.color.color_e7e7e7)
                                // resource or drawable
                        .showImageOnFail(R.color.color_e7e7e7)
                        //.showImageOnFail(R.color.color_e7e7e7)
                                // resource or drawable
                        .resetViewBeforeLoading(false).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(false)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        //.displayer(new FadeInBitmapDisplayer(50))
                       // .displayer(new SimpleBitmapDisplayer()).handler(new Handler())
                        .build();

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        Log.e("Application", cacheDir.getAbsolutePath());
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(getApplicationContext())
                        //.memoryCacheExtraOptions(, 800) // default = device screen dimensions
                        //.diskCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                        .threadPoolSize(2).threadPriority(Thread.NORM_PRIORITY - 1)
                        .tasksProcessingOrder(QueueProcessingType.FIFO)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                        .memoryCacheSize(4 * 1024 * 1024)
                        .memoryCacheSizePercentage(13) // default
                        .diskCache(new UnlimitedDiscCache(cacheDir))
                        .diskCacheSize(50 * 1024 * 1024)
                        .diskCacheFileCount(100)
                        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                        .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                        .imageDecoder(new BaseImageDecoder(true)) // default
                        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                        .defaultDisplayImageOptions(defaultoptions).build();
        DisplayImageOptions defaultLogoOptions =
                new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                        .cacheInMemory(true).cacheOnDisc(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        ImageLoaderConfiguration LogoConfig =
                new ImageLoaderConfiguration.Builder(getApplicationContext())
                        .threadPoolSize(1).threadPriority(Thread.NORM_PRIORITY - 1)
                        .tasksProcessingOrder(QueueProcessingType.FIFO)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCache(new LruMemoryCache(1 * 1024 * 1024))
                        .diskCache(new UnlimitedDiscCache(cacheDir))
                        .defaultDisplayImageOptions(defaultLogoOptions).build();

        LogoImageLoader.getInstance().init(LogoConfig);
        ImageLoader.getInstance().init(config);


    }


    private String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static ChaoJiShiPinApplication getInstatnce() {
        return mInstatnce;
    }

    public DownloadManager getDownloadManager() {
        return mDownloadManager;
    }
//    public Stack<Activity> getActivityStack() {
//        return activityStack;
//    }

//    public void setActivityStack(Activity activity) {
//        this.activityStack.add(activity);
//    }

//    public Activity getActivity() {
//        if (null != activityStack) {
//            for (int i = 0; i < activityStack.size(); i++) {
//                if (activityStack.get(i) != null) {
//                    return activityStack.get(i);
//                }
//            }
//        }
//        return null;
//    }
//
//    public void popActivity() {
//        if (activityStack != null && activityStack.size() > 0)
//            activityStack.remove(activityStack.size());
//    }

    public void startCheckSDCardFreeSizeService() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction(DownloadService.CHECK_SDCARD_FREESIZE);
        startService(intent);
    }

    public boolean isSpeedCut() {
        return isSpeedCut;
    }

    public void setSpeedCut(boolean isSpeedCut) {
        this.isSpeedCut = isSpeedCut;
    }

    /**
     * 网络监听
     */
    private void startNetworkObserveService(){
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.registerReceiver(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
