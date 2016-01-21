package com.chaojishipin.sarrs.download.download;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.OutSiteData;
import com.chaojishipin.sarrs.bean.OutSiteDataInfo;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.bean.SnifferReport;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thread.ThreadPoolManager;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.UpdateSnifferManager;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class DownloadJob {
    private static final String TAG = "DownloadJob";
    private DownloadEntity mEntity;

    public OutSiteDataInfo getOutSiteDataInfo() {
        return outSiteDataInfo;
    }

    OutSiteDataInfo outSiteDataInfo;

    public void setOutSiteDataInfo(OutSiteDataInfo outSiteDataInfo) {
        this.outSiteDataInfo = outSiteDataInfo;
    }

    private int mProgress;
    private long mTotalSize;
    private long mDownloadedSize;
    private long mM3u8DownloadedSize;
    private String mRate;
    private long mOldTime = 0;
    private long mOldBytes = 0;
    private String mDestination;
    private int mIndex;
    private DownloadJobListener mListener;
    private DownloadManager mDownloadManager;
    private DownloadTask mDownloadTask;
    private int mRetryNum;
    private int mTotalRetryNum;
    private boolean autoSnifferRetry = true;//站外源，走截流时，自动重试走截流逻辑

    // job 的各种状态
    public static final int INIT = 5;
    public static final int DOWNLOADING = 2;
    public static final int PAUSE = 3; // 指用户人为暂停
    public static final int WAITING = 4;
    public static final int COMPLETE = 1;
    public static final int DELETE = 6;
    public static final int PAUSEONSPEED = 7;
    public static final int NO_USER_PAUSE = 0; // 指网络等其他原因非用户任务暂停
    private int mStatus = INIT;

    public final static int NET_TIMEOUT = 1;
    public final static int FILE_NOT_FOUND = 2;
    public final static int SD_SPACE_FULL = 3;
    public final static int NO_SD = 4;
    public final static int NET_SHUT_DOWN = 5;
    public final static int DOWNLOAD_FAILUER = 6;//下载失败
    public final static int MOBILE = 7;
    public final static int OTHER_EXCEP = 9;
    private int mExceptionType;
    public DownloadHandler downloadHandler;
    private boolean isUserPauseWhen3G = true;
    String gvid;
    String playid = "2";
    String mFormat = "0,1,2,3,4";//获取所有的清晰度的流地址
    private int count = 0;
    private boolean isCheck = false;//删除选择时，判断是否被选中
    /**
     * 劫流上报对象
     */
    private SnifferReport mSnifferReport;
    private ArrayList mStateList;


    private WebView mWebView;
    private TestJavaScriptInterface mTestInterface;
    private List<String> downloadurllist = new ArrayList<String>();

    public int getCurrentdownloadpositon() {
        return currentdownloadpositon;
    }

    public void setCurrentdownloadpositon(int currentdownloadpositon) {
        this.currentdownloadpositon = currentdownloadpositon;
    }

    private int currentdownloadpositon = 0;
    private boolean isjscut = false;
    //    private Map<Integer,List<String>> totalstreamlist;
    ArrayList<List<String>> totalstreamlist = new ArrayList<List<String>>();

    public int getCurrent_streamlistposition() {
        return current_streamlistposition;
    }

    public void setCurrent_streamlistposition(int current_streamlistposition) {
        this.current_streamlistposition = current_streamlistposition;
    }

    int current_streamlistposition=0;
    public DownloadJob(DownloadEntity data, String destination) {
        this.mEntity = data;
        mDestination = destination;
        mDownloadedSize = DownloadHelper.getDownloadedFileSize(data, destination);
        mTotalSize = data.getFileSize();
        mProgress = initProgress();
        mStatus = data.getStatus();
        mTotalSize = data.getFileSize();
        mDownloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
        initSniffRepeortData();
        initWebView(ChaoJiShiPinApplication.getInstatnce().getApplicationContext());
    }

    public int initProgress() {
        return mTotalSize == 0 ? 0 : (int) ((mDownloadedSize * 100) / mTotalSize);
    }

    public int getmTotalRetryNum() {
        return mTotalRetryNum;
    }

    public boolean getAutoSnifferRetry() {
        return autoSnifferRetry;
    }

    public boolean getCanChangeSniffer() {
        boolean nodownLoad = DownloadHelper.getDownloadedFileSize(mEntity, mEntity.getPath()) == 0;

        return (mTotalRetryNum == 7) && nodownLoad;
    }

    public DownloadEntity getEntity() {
        return mEntity;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public void setListener(DownloadJobListener mListener) {
        this.mListener = mListener;
    }

    public void setRetryNum(int retryNum) {
        this.mRetryNum = retryNum;
    }

    public void setExceptionType(int mExceptionType) {
        this.mExceptionType = mExceptionType;
    }

    public int getExceptionType() {
        return mExceptionType;
    }

    public boolean isUserPauseWhen3G() {
        return isUserPauseWhen3G;
    }

    public void setUserPauseWhen3G(boolean isUserPauseWhen3G) {
        this.isUserPauseWhen3G = isUserPauseWhen3G;
    }

    public DownloadManager getDownloadManager() {
        return mDownloadManager;
    }

    public void setDownloadManager(DownloadManager mDownloadManager) {
        this.mDownloadManager = mDownloadManager;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getDestination() {
        return mDestination;
    }

    public boolean isDownloadcan3g() {
        return mDownloadManager.IsDownloadcan3g();
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(long mTotalSize) {
//        LogUtil.e("wulianshu","文件总大小为："+mTotalSize);
        this.mTotalSize = mTotalSize;
        DownloadProvider mDownloadProvider = mDownloadManager.getProvider();
        if (mTotalSize > 0)
            mDownloadProvider.updateDatabaseValue(getEntity(), "file_length", (int) mTotalSize);
    }

    public void setDownloadedSize(long mDownloadedSize) {
//        LogUtil.e("wulianshu","已近下载的文件大小为："+mDownloadedSize);
        this.mDownloadedSize = mDownloadedSize;
        int oldProgress = mProgress;
        if (mTotalSize == 0) {
            mProgress = 0;
        } else {
            mProgress = (int) ((mDownloadedSize * 100) / mTotalSize);
        }

        if (mProgress != oldProgress) {
            mDownloadManager.notifyObservers();
            notifyDownloadOnUpdate();
        }
    }

    public long getDownloadedSize() {
        return mDownloadedSize;
    }

    public void setRate() {
        long curTime = System.currentTimeMillis();

        if (((curTime - mOldTime) / 1000) >= 2) {

            mOldTime = curTime;
            this.mRate = getRate(mDownloadedSize - mOldBytes);
            System.out.println("rate source:" + this.mRate);
            mOldBytes = mDownloadedSize;
            mDownloadManager.notifyObservers();
            notifyDownloadOnUpdate();
        }
    }

    public void setM3u8Rate() {
        long curTime = System.currentTimeMillis();

        if (((curTime - mOldTime) / 1000) >= 2) {

            mOldTime = curTime;
            this.mRate = getRate(mM3u8DownloadedSize - mOldBytes);
            mOldBytes = mM3u8DownloadedSize;
            mDownloadManager.notifyObservers();
            notifyDownloadOnUpdate();
        }
    }

    public void setRate(String strRate) {
        mRate = strRate;
    }

    public String getRate() {
        if (TextUtils.isEmpty(mRate)) {
            mRate = "0.0KB/s";
        }
        return mRate;
    }

    public String getRate(long l) {
        System.out.println("l:" + l);

        float rate = (int) (l / 1024 / 1.5);
//		if (rate > 3000 || rate < 0.0)
        if (rate < 0.0)
            rate = 0.0f;
        return rate + "KB/s";
    }

	/*
     * public void setEntity(DownloadEntity mEntity) { this.mEntity = mEntity; }
	 * 
	 * public void setDestination(String mDestination) { this.mDestination =
	 * mDestination; }
	 */

    @SuppressLint("NewApi")
    public void start() {
        LogUtil.e("wulianshu", "DownLoadJob start 调用");
        //内存小于500M不下载
        if (ContainSizeManager.getInstance().getFreeSize() <= Utils.SDCARD_MINSIZE) {
            mStatus = NO_USER_PAUSE;
            ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce().getApplicationContext(), R.string.sdcard_nospace);
            return;
        }
        int num = mDownloadManager.getMaxDownloadNum();
        mExceptionType = 0;
        synchronized (DownloadJob.class) {
            if (mDownloadManager.DOWNLOADING_NUM < num) {
                LogUtil.e("wulianshu", "正在下载：" + gvid);
                mDownloadManager.DOWNLOADING_NUM = 1;
                currentdownloadpositon = 0;
                mDownloadTask = new DownloadTask(this);
                //走外站源
                LogUtil.e("v1.1.2","site "+this.getEntity().getSite());
                LogUtil.e("v1.1.2","src "+this.getEntity().getSrc());
                if (!"letv".equals(this.getEntity().getSite()) && !"nets".equals(this.getEntity().getSite())) {
                    LogUtil.e("v1.1.2","download source outiste! ");
                    if (outSiteDataInfo == null) {
                        mRetryNum = 0;
                    }
                    gvid = getEntity().getGlobaVid();
                    LogUtil.e("wulianshu", "走外站源下载的下载");
                    HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_DOWNLOADURL_TAG);
                    LogUtil.e("wulianshu", "走外站源 请求js截流详情");
                    HttpApi.requestOutSiteData(gvid, null, playid, mFormat).start(new OutSiteDataListener());

                } else {
                    LogUtil.e("v1.1.2","download source insite! ");
                    LogUtil.e("wulianshu","走乐视源下载。。。。。。。");
                    isjscut = false;
                    if (Utils.getAPILevel() >= 11) {
                        mDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        mDownloadTask.execute();
                    }
                }
                mStatus = DOWNLOADING;
                mDownloadManager.setStatus(mEntity, mStatus);
            } else {
                LogUtil.e("wulianshu", "A正在下载的数量大于0暂停当前的下载任务");
                mStatus = WAITING;
                mDownloadManager.setStatus(mEntity, mStatus);
            }

            if (downloadHandler != null) {
                downloadHandler.onStart();
            }

        }
    }

    public void onCompleted() {
//		LogUtils.i("dyf", "下载成功：onCompleted");
        //wulianshu
        mRetryNum = 0;
        mStatus = DownloadJob.COMPLETE;
        notifyDownloadOnPause();
        notifyDownloadEnded();
        mDownloadManager.DOWNLOADING_NUM = 0;
        mDownloadManager.startNextTask();
    }

    public boolean pauseByUser() {
        setRate("0");
        mStatus = PAUSE;
        mTotalRetryNum = 0;
        mDownloadManager.DOWNLOADING_NUM = 0;
        //wulianshu
        mRetryNum = 0;
        mDownloadManager.startNextTask();
        mDownloadManager.setStatus(mEntity, PAUSE);
        if (downloadHandler != null) {
            downloadHandler.onPause(this);
        }
        return mDownloadTask.cancel(true);
    }

    /**
     * 下载失败后，置为暂停状态。和点击暂停有区别：DOWNLOADING_NUM不自减
     */
    public boolean pauseByDownLoadFailure() {
        mStatus = PAUSE;
        //wulianshu
        mRetryNum = 0;
        mTotalRetryNum = 0;
//		mDownloadManager.DOWNLOADING_NUM--;
        mDownloadManager.startNextTask();
        mDownloadManager.setStatus(mEntity, PAUSE);
        if (downloadHandler != null) {
            downloadHandler.onPause(this);
        }
        return mDownloadTask.cancel(true);
    }

    public boolean allPause() {
        mStatus = PAUSE;
        mDownloadManager.DOWNLOADING_NUM = 0;
//		mDownloadManager.startNextTask();
        mDownloadManager.setStatus(mEntity, PAUSE);
        if (downloadHandler != null) {
            downloadHandler.onPause(this);
        }
        boolean result = false;
        if (null != mDownloadTask) {
            result = mDownloadTask.cancel(true);
        }
        return result;
    }

    /**
     * Waiting-->Pause
     */
    public void cancel() {
        mStatus = PAUSE;
        mDownloadManager.setStatus(mEntity, PAUSE);
    }

    public void onFailure() {
        LogUtil.e("wulianshu", "下载失败的返回   outSiteDataInfo:" + outSiteDataInfo);
        if (!"letv".equals(this.getEntity().getSite()) && !"nets".equals(this.getEntity().getSite())) {
            if (currentdownloadpositon >= outSiteDataInfo.getOutSiteDatas().size()) {
                //最终考虑 多StreamList的情况
                if(totalstreamlist.size() >0 && current_streamlistposition < totalstreamlist.size()){
                    mDownloadTask.cancel(true);
                    mDownloadTask = null;
                    mDownloadTask = new DownloadTask(this);
                    isjscut = false;
                    String downloadpath = totalstreamlist.get(current_streamlistposition).get(0);
                    mDownloadTask.setOutsidedownloadPath(downloadpath);
                    LogUtil.e("wulianshu", "多个SreamList情况下载："+downloadpath);
                    executeDownload();
                    current_streamlistposition ++;
                }else {
                    //1次重试的机会
                    if (mRetryNum < 2) {
                        isjscut = false;
                        LogUtil.e("wulianshu", "重试下载");
                        mRetryNum++;
                        currentdownloadpositon = 0;
                        mDownloadManager.DOWNLOADING_NUM = 0;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                start();
                            }
                        }, 500);

                    } else {
                        LogUtil.e("wulianshu", "都失败了下载下一个任务");
                        isjscut = false;
                        mRetryNum = 0;
                        currentdownloadpositon = 0;
                        mDownloadManager.DOWNLOADING_NUM = 0;
                        downloadfailure();
                    }
                }
            } else {
                LogUtil.e("wulianshu", "失败但是 OutSiteData 还有数据");
                mDownloadTask.cancel(true);
                mDownloadTask = null;
                mDownloadTask = new DownloadTask(this);
                if (isjscut) {
                    //下载 StreamList
                    LogUtil.e("wulianshu", "截流下载失败走Stream_list 下载2");
                    if (outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().size() > 0) {
                        if(isallEquals(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list())) {
                            LogUtil.e("wulianshu", "走stream_list下载地s址" + outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
                            isjscut = false;
                            mDownloadTask.setOutsidedownloadPath(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
                            executeDownload();
                        }else{
                            totalstreamlist.add(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list());
                            currentdownloadpositon++;
                            doOutsideDownload();
                        }
                    } else {
                        LogUtil.e("wulianshu", "截流下载和stream_list都失败了 试一试一下个");
                        currentdownloadpositon++;
                        doOutsideDownload();
                    }
                } else {
                    currentdownloadpositon++;
                    doOutsideDownload();
                }

            }
        } else {
            mDownloadManager.DOWNLOADING_NUM = 0;
            if (isCanReTry()) {
                mRetryNum++;
                mTotalRetryNum++;
                start();
            } else if (isSnifferCanReTry()) {
                addReportState(PlayerUtils.M400);
                autoSnifferRetry = false;
                mRetryNum = 0;
                start();
            } else if (isCanChangeSniffer()) {
                mRetryNum = 0;
                autoSnifferRetry = true;
                start();
            } else {
                downloadfailure();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },7000);
//                mRetryNum = 0;
//                mTotalRetryNum = 0;
//                autoSnifferRetry = true;
//                mStatus = DownloadJob.NO_USER_PAUSE;
//                if (downloadHandler != null) {
//                    downloadHandler.onPause(this);
//                }
//                notifyDownloadOnPause();
//                // mJob.setmExceptionType();
//                if (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == NetworkUtil.TYPE_ERROR) {
//                    mExceptionType = NET_SHUT_DOWN; // 无网络
//                } else if (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == NetworkUtil.TYPE_MOBILE) {
//                    //3g情况下，不允许下载，不做任何处理，和暂停相同
//                } else if (ContainSizeManager.getInstance().getFreeSize() <= Utils.SDCARD_MINSIZE) {
//
//                } else {
//                    mExceptionType = DOWNLOAD_FAILUER;//下载失败
//                    pauseByDownLoadFailure();//下载失败，相当于用户暂停
//                    //上报
//                    if (!autoSnifferRetry) {
//                        addReportState(PlayerUtils.M410);
//                    }
//                    if (mProgress > 0) {
//                        addReportState(PlayerUtils.M412);
//                    }
//                }
//                mDownloadManager.notifyObservers();
            }
        }

    }

    void downloadfailure() {

        mRetryNum = 0;
        mTotalRetryNum = 0;
        autoSnifferRetry = true;
        mStatus = DownloadJob.NO_USER_PAUSE;
        if (downloadHandler != null) {
            downloadHandler.onPause(this);
        }
        notifyDownloadOnPause();
        // mJob.setmExceptionType();
        if (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == NetworkUtil.TYPE_ERROR) {
            mExceptionType = NET_SHUT_DOWN; // 无网络
        } else if (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == NetworkUtil.TYPE_MOBILE) {
            //3g情况下，不允许下载，不做任何处理，和暂停相同
        } else if (ContainSizeManager.getInstance().getFreeSize() <= Utils.SDCARD_MINSIZE) {

        } else {
            mExceptionType = DOWNLOAD_FAILUER;//下载失败
            pauseByDownLoadFailure();//下载失败，相当于用户暂停
            //上报
            if (!autoSnifferRetry) {
                addReportState(PlayerUtils.M410);
            }
            if (mProgress > 0) {
                addReportState(PlayerUtils.M412);
            }
        }
        mDownloadManager.notifyObservers();

    }

    public boolean pauseOnOther(int status) {
        mStatus = status;
        mDownloadManager.DOWNLOADING_NUM = 0;
        if (mListener != null)
            mListener.downloadPaused(this);

        if (downloadHandler != null) {
            downloadHandler.onPause(this);
        }
        return mDownloadTask.cancel(true);
    }

    /**
     * 当不开启后台下载退出时的处理
     */
    public void pauseOnExit() {
        mStatus = PAUSEONSPEED;
        mDownloadManager.DOWNLOADING_NUM = 0;
        if (mListener != null)
            mListener.downloadPaused(this);
        mDownloadTask.cancel(true);
    }

    private boolean isCanReTry() {
//		LogUtils.i("dyf", "mExceptionType:" + mExceptionType);
        if (mExceptionType != 0 && mExceptionType != NET_TIMEOUT)
            return false;
        switch (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce())) {
            case 1:
                return mRetryNum < 3 ? true : false;
            case 2:
                return isDownloadcan3g();
            default:
                return false;
        }
    }

    /**
     * 截流时，需要自动重试3次
     */
    private boolean isSnifferCanReTry() {
//		LogUtils.i("dyf", "isSnifferCanReTry--mExceptionType:" + mExceptionType);
        if ("letv".equals(mEntity.getSite()))
            return false;
        if (mExceptionType != 0 && mExceptionType != NET_TIMEOUT)
            return false;
        switch (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce())) {
            case 1:
                return autoSnifferRetry;
            case 2:
                return isDownloadcan3g();
            default:
                return false;
        }
    }

    /**
     * 是否可以更换截流模式，mp4和m3u8之间的切换
     */
    private boolean isCanChangeSniffer() {
//		LogUtils.i("dyf", "isCanChangeSniffer");
        if ("letv".equals(mEntity.getSite()))
            return false;
        if (mExceptionType != 0 && mExceptionType != NET_TIMEOUT)
            return false;
        switch (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce())) {
            case 1:
                return mTotalRetryNum == 6 ? true : false;
            case 2:
                return isDownloadcan3g();
            default:
                return false;
        }
    }

    public boolean isCurrentPathExist() {
        if (!TextUtils.isEmpty(mDestination)) {
            File file = new File(mDestination);
            return file.exists();
        }
        return false;
//        if (!StringUtil.isEmpty(mDestination)) {
//            String tempPath = mDestination;
//            if (tempPath.contains("/" + Utils.getDownLoadFolder())) {
//                tempPath = tempPath.substring(0, tempPath.indexOf("/" + Utils.getDownLoadFolder()));
//            } else if (tempPath.contains("/" + "kuaikan")) {
//                tempPath = tempPath.substring(0, tempPath.indexOf("/" + "kuaikan"));
//            }
//            if (DownloadHelper.isSdcardExist(tempPath)) {
//                return true;
//            }
//        }
//        return false;
    }

    public void notifyDownloadAdded() {
        if (mListener != null)
            mListener.downloadStarted(this);
    }

    public void notifyDownloadStarted() {
        if (mListener != null)
            mListener.downloadOnDownloading(this);
    }

    public void notifyDownloadOnUpdate() {
        if (mListener != null)
            mListener.updateNotifyOnDownloading(this);
    }

    public void notifyDownloadOnPause() {
        if (mListener != null)
            mListener.downloadOnPause(this);
    }

    public void notifyDownloadEnded() {
        if (!mDownloadTask.isCancelled()) {
//			LogUtils.i("dyf", "下载成功：notifyDownloadEnded");
            if (mListener != null) {
//				LogUtils.i("dyf", "下载成功：notifyDownloadEnded---mListener");
                mListener.downloadEnded(this);
            } else {
//				LogUtils.i("dyf", "下载成功：notifyDownloadEnded---？？？？？");
                DownloadProvider mDownloadProvider = mDownloadManager.getProvider();
                mDownloadProvider.downloadCompleted(this);
            }
            mProgress = 100;
        }
    }

    public void updateDownloadEntity() {
        mDownloadManager.getProvider().updateDownloadEntity(this);
    }

    public boolean isCancelled() {
        return mDownloadTask.isCancelled();
    }

    public DownloadHandler getDownloadHandler() {
        return downloadHandler;
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public int downloadFile() throws Exception {
        return downloadHandler.downloadFile(this);
    }

    public String getmDestination() {
        return mDestination;
    }

    public void setmDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public boolean getCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public SnifferReport getmSnifferReport() {
        if (null == mSnifferReport)
            initSniffRepeortData();
        return mSnifferReport;
    }

    private void initSniffRepeortData() {
        // 只有站外源才需要劫流上报对象
        String site = mEntity.getSite();
        if (PlayerUtils.isOutSite(mEntity.getSite())) {
            mStateList = new ArrayList<String>();
            mSnifferReport = new SnifferReport();
            mSnifferReport.setAid(mEntity.getMid());
            mSnifferReport.setSite(site);
            mSnifferReport.setDefinition("StandardUrl");
            mSnifferReport.setPlayUrl(mEntity.getSnifferUrl());
            mSnifferReport.setDownload("1");
        }
    }

    /**
     * 添加当前上报的状态
     */
    public void addReportState(String state) {
        // 只有站外源才需要劫流上报对象
        String site = mEntity.getSite();
        if (PlayerUtils.isOutSite(site)) {
            if (null == mStateList) {
                mStateList = new ArrayList<String>();
            }
            if (!mStateList.contains(state)) {
                mStateList.add(state);
                getmSnifferReport().setmStateList(mStateList);
            }
        }
    }

    /**
     * 为m3u8下载提供的一个计算速度的变量
     */
    public void setmM3u8DownloadedSize(long mM3u8DownloadedSize) {
        this.mM3u8DownloadedSize += mM3u8DownloadedSize;
    }

    /**
     * wulianshu
     * <p/>
     * 发送js截流请求
     *
     * @param apiContent api-list 请求每一条api对应的api-content
     * @param requestUrl 防盗链接口返回播放地址
     * @param streamUrl  防盗链接口返回流数组对应的每一条url数据
     */
    void sendCutRequest(String requestUrl, String apiContent, String streamUrl, String mTs, String mTe) {
        com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
        LogUtil.e("wulianshu", "截流前数据准备 requestUrl:" + requestUrl + " streamUrl " + streamUrl + "apiContent:" + apiContent);
        obj.put("requestUrl", Base64.encodeToString(requestUrl.getBytes(), Base64.DEFAULT));
        obj.put("uStream", Base64.encodeToString(streamUrl.getBytes(), Base64.DEFAULT));
        obj.put("apiContent", Base64.encodeToString(apiContent.getBytes(), Base64.DEFAULT));
        //TODO rule 值获取
        if (mTs != null && mTe != null) {
            LogUtil.e("wulianshu", "NEW js has rule");
            com.alibaba.fastjson.JSONObject ruleObj = new com.alibaba.fastjson.JSONObject();

            if (!TextUtils.isEmpty(mTs)) {
                ruleObj.put("ts", mTs);
            }
            if (!TextUtils.isEmpty(mTe)) {
                ruleObj.put("te", mTe);
            }
            obj.put("rule", ruleObj);
            LogUtil.e("wulianshu", "截流前数据准备 rule " + ruleObj.toString());
        } else {
            LogUtil.e("wulianshu", "rule 为空");
        }

        String mSnifferParamter = obj.toString().replace("\\n", "");
        String fileName = "request4download.html";
        FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce(), fileName, mSnifferParamter);
        LogUtil.e("wulianshu", "截流数参数为" + mSnifferParamter);
        // 发送消息调用JS代码
        // 发送消息调用JS代码
        //mHandler.sendEmptyMessage(Utils.GET_JS_RESULT);
//        mWebView.clearCache(true);
        mWebView.loadUrl("javascript:MyJavaScriptInterface.startFunction(dealWithRequest('" + mSnifferParamter + "'));");
        LogUtil.e("wulianshu", "LoadUrl 之后@@@@@@@@@");
    }

    public class TestJavaScriptInterface {
        @JavascriptInterface
        public void startFunction(final String result) {
            LogUtil.e("wulianshu", "截流结束 结果为:" + result);
            try {
                JSONObject obj = new JSONObject(result);
                String stream = obj.getString("stream");
                LogUtil.e("wulianshu", "截流的下载路劲为：" + stream);
                if (stream != null && !"".equals(stream)) {
                    String fileName = "jscutresult4download.html";
                    FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce(), fileName, stream);
                    isjscut = true;
                    mDownloadTask.setOutsidedownloadPath(stream);
                    executeDownload();
                    //截流成功的上报
                    UploadStat.streamupload(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getUrl(), stream,outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getRequest_format());
                } else {
                    LogUtil.e("wulianshuaddUrl", "截流结果为空");
                    //继续解析下一条
                    //Todo 之后做具体的修改  现在：截流失败StreamList也不能使用了
                    if (currentdownloadpositon < outSiteDataInfo.getOutSiteDatas().size() - 1) {
                        LogUtil.e("wulianshu", "stream 和 截流 数据都为空  试 下一个OutSiteData");
                        currentdownloadpositon++;
                        doOutsideDownload();
                    } else {
                        LogUtil.e("wulianshu", "stream 和 截流 数据都为空  没得试了 直接失败");
                        onFailure();
                    }



//                    if (outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().size() > 0) {
//                        LogUtil.e("wulianshuaddUrl", "设置下载地址为 streamlist：" + outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
//                        if(isallEquals(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list())) {
//                        mDownloadTask.setOutsidedownloadPath(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
//                        isjscut = false;
//                        executeDownload();
//                        }else{
//                            totalstreamlist.add(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list());
//                            currentdownloadpositon++;
//                            doOutsideDownload();
//                        }
//                    } else {
//                        LogUtil.e("wulianshuaddUrl", "stream_list 也为空  重试下一条");
//                        currentdownloadpositon++;
//                        doOutsideDownload();
//                    }
                }
            } catch (Exception e) {
                LogUtil.e("wulianshuaddUrl", "截流结果解析");
                if (outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().size() > 0) {
                    if(isallEquals(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list())) {
                        mDownloadTask.setOutsidedownloadPath(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
                        isjscut = false;
                        executeDownload();
                    }else{
                        totalstreamlist.add(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list());
                        currentdownloadpositon++;
                        doOutsideDownload();
                    }
                } else {
                    LogUtil.e("wulianshuaddUrl", "stream_list 也为空  重试下一条");
                    currentdownloadpositon++;
                    doOutsideDownload();
                }
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Context activity) {
        mTestInterface = new TestJavaScriptInterface();
        setConfigCallback((WindowManager) activity.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE));
        mWebView = new WebView(ChaoJiShiPinApplication.getInstatnce());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);
        //请求jscode 接口
        UpdateSnifferManager.getInstance(activity).startUpdate();
        String htmlUrl = UpdateSnifferManager.getInstance(activity).getHtmlURL();
        LogUtil.e("wulianshu", "html的位置为" + htmlUrl);
        mWebView.loadUrl(htmlUrl);
        mWebView.addJavascriptInterface(mTestInterface, "MyJavaScriptInterface");
        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, " url:" + url);


                view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //
                LogUtil.e("wulianshu", " NEW js webview loaded ok");
            }
        });
    }

    /**
     * WebView防止内存泄露
     *
     * @param windowManager zhangshuo 2014年12月25日 下午5:38:12
     */
    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);
            if (null == configCallback) {
                return;
            }
            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {

        }
    }

    void batchRuquestApi(OutSiteData outSiteData) {
        if (outSiteData.getApi_list() != null && outSiteData.getApi_list().size() > 0) {
            LogUtil.e("wulianshu", "NEW js api size is " + outSiteData.getApi_list().size());
            // 多线请求api
            try {
                ThreadPoolManager.getInstanse().createPool();
                ThreadPoolManager.getInstanse().exeTaskTimOut(outSiteData.getApi_list(), outSiteData.getUrl(), outSiteData.getHeader(), 2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    void batchSendJsparam(OutSiteData outSiteData) {
        ThreadPoolManager.getInstanse().shutdown();
        while (!ThreadPoolManager.getInstanse().isTerminated()) {
            //等待执行完毕
            LogUtil.e("wulianshu", "获取 Api_contentList截流循环等待中 ");
        }
        List<String> mApi_contentlist = ThreadPoolManager.getInstanse().getResultList();
        LogUtil.e("wulianshu", "mApi_contentlist 内容:" + mApi_contentlist.toString());
        ThreadPoolManager.getInstanse().shutdown();
        if (mApi_contentlist != null && mApi_contentlist.size() > 0 && mApi_contentlist.size() > 0 && outSiteData.getStream_list().size() > 0 && outSiteData.getUrl() != null && outSiteData.getTs() != null && outSiteData.getTe() != null) {
            sendCutRequest(outSiteData.getUrl(), mApi_contentlist.get(0), outSiteData.getStream_list().get(0), outSiteData.getTs(), outSiteData.getTe());
//                }
//            }
        } else {
            LogUtil.e("wulianshu", "mApi_contentlist为空 走stream_list");
            if (outSiteData.getStream_list() != null && outSiteData.getStream_list().size() > 0) {
                if(isallEquals(outSiteData.getStream_list())) {
                    LogUtil.e("wulianshu", "下载路劲为：" + outSiteData.getStream_list().get(0));
                    mDownloadTask.setOutsidedownloadPath(outSiteData.getStream_list().get(0));
                    isjscut = false;
                    executeDownload();
                }else{
                    totalstreamlist.add(outSiteData.getStream_list());
                    currentdownloadpositon++;
                    doOutsideDownload();
                }
            } else {
                currentdownloadpositon++;
                doOutsideDownload();
            }
        }
    }

    public void doOutsideDownload() {
        LogUtil.e("wulianshu", "DownLoadJob start 外站源");
        getEntity().setUseUserAgent(true);
        if (outSiteDataInfo != null) {
            //外站截流
            if (outSiteDataInfo!=null && outSiteDataInfo.getOutSiteDatas() !=null && currentdownloadpositon < outSiteDataInfo.getOutSiteDatas().size()) {
                if (outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getApi_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getApi_list().size() > 0) {
                    final OutSiteData outSiteData = outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon);
                    batchRuquestApi(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("wulianshu", "DownLoadJob start 走截流");
                            batchSendJsparam(outSiteData);
                        }
                    }, 5000);
                } else {
                    LogUtil.e("wulianshu", "DownLoadJob start 走StreamList下载不走截流");
                    if (outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list() != null && outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().size() > 0) {
                        if(isallEquals(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list())) {
                            isjscut = false;
                            LogUtil.e("wulianshu", "streamlist 下载地址为：" + outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
                            mDownloadTask.setOutsidedownloadPath(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list().get(0));
                            executeDownload();
                        }else{
                            totalstreamlist.add(outSiteDataInfo.getOutSiteDatas().get(currentdownloadpositon).getStream_list());
                            if (currentdownloadpositon < outSiteDataInfo.getOutSiteDatas().size() - 1) {
                                LogUtil.e("wulianshu", "Stream 是多条先不下载 留到最后下载");
                                currentdownloadpositon++;
                                doOutsideDownload();
                            } else {
                                LogUtil.e("wulianshu", "stream 和 截流 数据都为空  没得试了 直接失败");
                                onFailure();
                            }
                        }
                    } else {
                        if (currentdownloadpositon < outSiteDataInfo.getOutSiteDatas().size() - 1) {
                            LogUtil.e("wulianshu", "stream 和 截流 数据都为空  试 下一个OutSiteData");
                            currentdownloadpositon++;
                            doOutsideDownload();
                        } else {
                            LogUtil.e("wulianshu", "stream 和 截流 数据都为空  没得试了 直接失败");
                            onFailure();
                        }
                    }
                }
            } else {
                onFailure();
            }
        } else {
            onFailure();
            return;
        }
    }

    /**
     * wulianshu 外站源下载列表请求
     */
    public class OutSiteDataListener implements RequestListener<OutSiteDataInfo> {
        @Override
        public void onResponse(OutSiteDataInfo result, boolean isCachedData) {
            LogUtil.e("wulianshu ", "获取 截流纤情 返回数据成功：" + result.toString());
            // 默认设置M3U8 有清晰度选项
            //executeOutSitePlayCore(mStreamIndex);
            //M3U8失败设置MP4
            //executeOutSitePlayCore(ConstantUtils.OutSiteDateType.MP4);
            //排列下载链接
            if (result != null && result.getOutSiteDatas() != null && result.getOutSiteDatas().size() > 0) {
                LogUtil.e("wulianshu ", "获取 截流纤情 返回数据成功  并且数据不为空");
                for (int i = 0; i < result.getOutSiteDatas().size(); i++) {
                    result.getOutSiteDatas().get(i).setPriority(Utils.getPriority4Download(result.getOutSiteDatas().get(i).getOs_type(), result.getOutSiteDatas().get(i).getRequest_format()));
                }
                Collections.sort(result.getOutSiteDatas());
//                for (OutSiteData outSiteData:result.getOutSiteDatas()){
//                    LogUtil.e("wulianshu","priority:"+outSiteData.getPriority());
//                }
                setOutSiteDataInfo(result);
                LogUtil.e("wulianshu ", "调用job的下载外站源方==doOutsideDownload");
                doOutsideDownload();
                return;
            } else {
                if (count < 2) {
                    LogUtil.e("wulianshu ", "获取数据为空 重试");
                    if (gvid != null && playid != null && mFormat != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HttpApi.requestOutSiteData(gvid, null, playid, mFormat).start(OutSiteDataListener.this);
                            }
                        }, 1000);
                    }
                } else {
                    LogUtil.e("wulianshu ", "获取 截流详情 两次后  获取的数据仍然为空");
                    //详情都获取不到不重试
                    mRetryNum = 2;
                    onFailure();
                }
                count++;
            }
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e("wulianshu ", " execute OutSite net error");
            onFailure();
        }

        @Override
        public void dataErr(int errorCode) {
            onFailure();
            LogUtil.e("wulianshu ", " execute OutSite data error");
        }
    }

    private void executeDownload() {
//        if (!"letv".equals(this.getEntity().getSite()) && !"nets".equals(this.getEntity().getSite())) {
//            LogUtil.e("wulianshu","*****外站源下载的地址为*******:"+mDownloadTask.getOutsidedownloadPath());
//        }
        if (getStatus() == DOWNLOADING) {
            if (Utils.getAPILevel() >= 11) {
                mDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mDownloadTask.execute();
            }
        }
        mDownloadManager.notifyObservers();
    }
    public boolean isallEquals(List<String> list){
        if(list.size() == 1){
            return true;
        }
        for(int i=1;i<list.size();i++){
            if(!list.get(i-1).equalsIgnoreCase(list.get(i))){
                return false;
            }
        }
        return true;
    }
}