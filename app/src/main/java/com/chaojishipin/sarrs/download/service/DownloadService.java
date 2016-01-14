package com.chaojishipin.sarrs.download.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.OutSiteData;
import com.chaojishipin.sarrs.bean.OutSiteDataInfo;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadBroadcastReceiver;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadJobListener;
import com.chaojishipin.sarrs.download.download.DownloadProvider;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint("NewApi")
public class DownloadService extends Service {

    public static final String ACTION_ADD_TO_DOWNLOAD = "add_to_download";

    public static final String EXTRA_MEDIAITEM_ENTRY = "download_entity";

    public static final String CHECK_SDCARD_FREESIZE = "check_sdcard_freesize";

    NotificationManager mNotificationManager = null;

    private DownloadProvider mDownloadProvider;

    private Timer mTimer;

    private int count = 0;

    String mFormat="0,1,2,3,4";//获取所有的清晰度的流地址

    String  playid = "2";//播放类型：0:点播 1:直播 2:下载
    String  gvid = "";
    DownloadJob downloadJob;
    //    private ContainSizeManager mSizeManager;
    //private static final int DOWNLOAD_NOTIFY_ID = 667668;
    private Notification n;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDownloadProvider = ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getProvider();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("wym", "service is running");
        if (intent != null) {
            String action = intent.getAction();
//			LogUtils.e("dyf", "进入服务----"+action);
            if (action.equals(ACTION_ADD_TO_DOWNLOAD)) {
                DownloadEntity entry = (DownloadEntity) intent.getSerializableExtra(EXTRA_MEDIAITEM_ENTRY);
                //每次添加新的下载，都开启sd卡检查
                ContainSizeManager.getInstance().checkSDCard();
                addToDownloadQueue(entry, startId);
                if (null != mTimer) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void addToDownloadQueue(DownloadEntity entry, int startId) {
//         String downloadPath = StringUtil.isEmpty(entry.getPath()) ? DownloadHelper.getDownloadPath() : entry.getPath();
        String downloadPath =DownloadHelper.getDownloadPath();

        LogUtil.e("xll_storage"," service "+downloadPath);
         downloadJob = new DownloadJob(entry, downloadPath);
         downloadJob.setIndex(entry.getIndex());
         if (mDownloadProvider.queueDownload(downloadJob)) {
            downloadJob.setListener(mDownloadJobListener);
            downloadJob.start();
         }

//        if (mDownloadProvider.queueDownload(downloadJob)) {
//            downloadJob.setListener(mDownloadJobListener);
//            if (!"letv".equals(downloadJob.getEntity().getSite()) && !"nets".equals(downloadJob.getEntity().getSite())) {
//                gvid = downloadJob.getEntity().getGlobaVid();
//                LogUtil.e("wulianshu", "downloadservice gvid :" + gvid);
//                HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_DOWNLOADURL_TAG);
//                HttpApi.requestOutSiteData(gvid, null, playid, mFormat).start(new OutSiteDataListener());
//            }else {
//                downloadJob.start();
//            }
//        }
    }

//    public void startCheckSDCard() {
//    }

    /**
     * do some action
     */
    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    ToastUtil.showShortToast(getApplicationContext(), "no");
//                	mTimer.cancel();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private DownloadJobListener mDownloadJobListener = new DownloadJobListener() {

        @Override
        public void downloadEnded(DownloadJob job) {
//			LogUtils.i("dyf", "下载成功：downloadEnded");
            String displayName = job.getEntity().getDisplayName();
            Toast.makeText(
                    DownloadService.this,
                    displayName + " 已下载完成",
                    Toast.LENGTH_SHORT).show();
            mDownloadProvider.downloadCompleted(job);
//			int icon = R.drawable.notification_start;
//			int contentTitle = R.string.download_complete;
//			displayNotifcationOnCompleted(icon, contentTitle,
//					displayName, job);
//			if (DownloadActivity.mSizeManager != null) {
//				DownloadActivity.mSizeManager.ansynHandlerSdcardSize();
//			}
            int uncompleteCount = mDownloadProvider.getQueuedDownloads().size();
//			if(PersonalCenterFragment.mPersonalCenterFragment!=null)
//				PersonalCenterFragment.mPersonalCenterFragment.showDownloadTip(uncompleteCount);
            if (uncompleteCount == 0) {
                if (null != mTimer)
                    mTimer.cancel();
                stopSelf();
            }

        }

        @Override
        public void downloadStarted(DownloadJob job) {
            // TODO Auto-generated method stub

        }

        @Override
        public void downloadPaused(DownloadJob job) {
            // TODO Auto-generated method stub

        }

        @Override
        public void downloadOnDownloading(DownloadJob job) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateNotifyOnDownloading(DownloadJob job) {
            // TODO Auto-generated method stub

        }

        @Override
        public void downloadOnPause(DownloadJob job) {
            if (job == null)
                return;
            //notifications.remove(job.getNotifyIdByHashId());
            //mNotificationManager.cancel(job.getNotifyIdByHashId());
            mNotificationManager.cancel((int) job.getTotalSize()); // 删除下载完成的任务时，清除相应的下载完成通知提示
        }


        private void playFromNotify(Context context, String path, String name,
                                    String size, double downPercent, DownloadJob job, int notifyId) {

            if (null == job || null == job.getEntity()) {
                return;
            }

        }

    };
    /**
     * wulianshu 外站源下载列表请求
     */
    public class OutSiteDataListener implements RequestListener<OutSiteDataInfo> {
        @Override
        public void onResponse(OutSiteDataInfo result, boolean isCachedData) {
            LogUtil.e("wulianshu ", "获取 outsideDatainfo ok");
            // 默认设置M3U8 有清晰度选项
            //executeOutSitePlayCore(mStreamIndex);
            //M3U8失败设置MP4
            //executeOutSitePlayCore(ConstantUtils.OutSiteDateType.MP4);
            //排列下载链接
            if(result != null && result.getOutSiteDatas()!=null &&  result.getOutSiteDatas().size()>0) {
                    for (int i = 0; i < result.getOutSiteDatas().size(); i++) {
                        result.getOutSiteDatas().get(i).setPriority(Utils.getPriority4Download(result.getOutSiteDatas().get(i).getOs_type(), result.getOutSiteDatas().get(i).getRequest_format()));
                    }
                    Collections.sort(result.getOutSiteDatas());
                    downloadJob.setOutSiteDataInfo(result);
                    LogUtil.e("wulianshu ", "调用job的start方法");
                    downloadJob.start();
                    return;
            }else{

                if(count<2) {
                    LogUtil.e("wulianshu ", "获取数据为空 重试");
                    if(gvid !=null && playid !=null && mFormat !=null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HttpApi.requestOutSiteData(gvid, null, playid, mFormat).start(OutSiteDataListener.this);
                            }
                        },2000);
                    }
                }else{
                    LogUtil.e("wulianshu ", "获取 两次后仍然为  获取的数据为空");
                    downloadJob.setStatus(DownloadJob.DOWNLOAD_FAILUER);
                    mDownloadJobListener.downloadOnPause(downloadJob);
                }
                count++;
            }
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e("wulianshu ", " execute OutSite net error");
//            showNoNetView();
        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e("wulianshu ", " execute OutSite data error");
//            showTimeOutLayout();
        }
    }

}
