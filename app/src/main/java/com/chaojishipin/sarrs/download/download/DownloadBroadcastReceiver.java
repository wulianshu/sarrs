package com.chaojishipin.sarrs.download.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.download.fragment.DownloadFragment;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;


public class DownloadBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "DownloadBroadcastReceiver";
    public static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static String DOWNLOADCAN3G = "com.funshion.video.DOWNLOADCAN3G";
    public static final String SPEED_ACTION = "com.funshion.video.CUTDOWNLOADSPEED";
    public static final String SDCARD_NOSPACE_ACTION = "com.chaojishipin.SDCARDNOSPACE";
    private final static int FAILURE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != context && null != intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            // 网络发生变化时
            if (NET_ACTION.equals(action)) {
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null == manager) {
                    return;
                }
                NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (null == wifi || null == gprs) {
                    return;
                }
                if (wifi.isConnected()) {
                    for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                        if (null != job && job.getStatus() == DownloadJob.NO_USER_PAUSE) {
                            if (job.getExceptionType() != DownloadJob.NO_SD
                                    && job.getExceptionType() != DownloadJob.FILE_NOT_FOUND) {
                                job.start();
                            }
                        }
                    }
                    // wifi连接，自动继续下载升级包文件
//					AutoDownloadApkSupporter mDownloadApkInstance = AutoDownloadApkSupporter.getInstance();
//					if (mDownloadApkInstance.getAutoDownloadUpdateFileState() == FAILURE) {
//						mDownloadApkInstance.reDownloadUpgradeApk(context, mDownloadApkInstance.getmUpgradeUrl());
//					}

//					AdShowUtils.mApkDownloadPauseFlag = false;
//					reDownloadApk(context);
                }
                if (gprs.isConnected()) {
//					P2pHelper p2pHelper =P2pHelper.getInstance();
//					p2pHelper.stopTaskOnPauseOrWait();
//					p2pHelper.stopAllTaskOn3G();
                    DownloadManager downloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
                    for (DownloadJob job : downloadManager.getQueuedDownloads()) {
//						p2pHelper.stopDownloadTask(job);
                        if (job.getStatus() == DownloadJob.DOWNLOADING) {
                            job.pauseOnOther(DownloadJob.NO_USER_PAUSE);
                        }
                    }
                }
            }

            if (DOWNLOADCAN3G.equals(action)) {
                boolean isDownloadcan3g = intent.getBooleanExtra("isDownloadcan3g", false);
                // 当非移动网络时
                if (!(NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == 2))
                    return;
                if (!isDownloadcan3g) {
                    for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                        if (null != job && job.getStatus() == DownloadJob.DOWNLOADING)
                            job.pauseOnOther(DownloadJob.NO_USER_PAUSE);
                    }
                } else {
                    for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                        if (null != job && job.getStatus() == DownloadJob.NO_USER_PAUSE) {
                            if (job.getExceptionType() != DownloadJob.NO_SD
                                    && job.getExceptionType() != DownloadJob.FILE_NOT_FOUND) {
                                job.start();
                            }
                        }
                    }
                }
            }

            if (SDCARD_NOSPACE_ACTION.equals(action)) {
                for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                    if (null != job && job.getStatus() == DownloadJob.DOWNLOADING) {
                        job.pauseOnOther(DownloadJob.NO_USER_PAUSE);
                        ToastUtil.showShortToast(context, R.string.sdcard_nospace);
                        Log.d("order", "space no 1");
                    }
                }
            }

            if (SPEED_ACTION.equals(action)) {
                boolean cutSpeed = intent.getBooleanExtra("player", false);
                ChaoJiShiPinApplication.getInstatnce().setSpeedCut(cutSpeed);
                if (cutSpeed) {
                    for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                        if (null != job && job.getStatus() == DownloadJob.DOWNLOADING) {
                            job.pauseOnOther(DownloadJob.NO_USER_PAUSE);
                        }
                        ChaoJiShiPinApplication.getInstatnce().getDownloadManager().notifyObservers();
                    }
//					AdShowUtils.mApkDownloadPauseFlag = true;
//					LogUtils.i(TAG, "AdShowUtils.mApkDownloadPauseFlag被赋值为 " + AdShowUtils.mApkDownloadPauseFlag);
                } else {
                    for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                        if (null != job && job.getStatus() == DownloadJob.NO_USER_PAUSE) {
                            if (job.getExceptionType() != DownloadJob.NO_SD
                                    && job.getExceptionType() != DownloadJob.FILE_NOT_FOUND) {
                                job.start();
                            }
                        }
                    }
//					ConnectivityManager manager = (ConnectivityManager) context
//							.getSystemService(Context.CONNECTIVITY_SERVICE);
//					if (null == manager) {
//						return;
//					}
//					NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//					if (null != gprs && !gprs.isConnected()) {
////						AdShowUtils.mApkDownloadPauseFlag = false;
////						reDownloadApk(context);
//					}
//					P2pHelper.getInstance().stopTaskOnPauseOrWait();
                }
            }

            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
//					if (null != job && job.getStatus() == DownloadJob.NO_USER_PAUSE)
//						job.start();
//                    if (DownloadFragment.mSizeManager != null) {
//						DownloadActivity.mSizeManager.ansynHandlerSdcardSize();
//                    }
                }
            }

            if (Intent.ACTION_MEDIA_REMOVED.equals(action) || Intent.ACTION_MEDIA_EJECT.equals(action)
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
                for (DownloadJob job : ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getQueuedDownloads()) {
                    if (null != job && job.getStatus() == DownloadJob.DOWNLOADING) {
                        job.setExceptionType(DownloadJob.NO_SD);
                        job.pauseOnOther(DownloadJob.NO_USER_PAUSE);
                    }
                }
            }

            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {

            }
            if (Intent.ACTION_MEDIA_EJECT.equals(action)) {

            }
        }
    }

//	private void reDownloadApk(Context context) {
//		ArrayList<AdApkEntity> apkList = MoviesApplication.getInstance().getmAdDownloadAppList();
//		if (null != apkList && apkList.size() > 0) {
//			// for(AdApkEntity entity : apkList) {
//			for (int i = 0; i < apkList.size(); i++) {
//				AdApkEntity entity = apkList.get(i);
//				AdShowUtils.startDownload(context, entity.getDownloadUrl(), entity.getNotifyId());
//				LogUtils.i(TAG, "start apk download -- url == " + entity.getDownloadUrl());
//			}
//		}
//		LogUtils.i(TAG, "start download -- mApkDownloadPauseFlag == " + AdShowUtils.mApkDownloadPauseFlag);
//	}

}
