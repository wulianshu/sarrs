package com.chaojishipin.sarrs.download.download;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.manager.NetworkManager;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.PopupDialog;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;



public class DownloadProvider {
    private SparseArray<DownloadFolderJob> folderJobs;
    private ArrayList<DownloadJob> mQueuedJobs;
    private ArrayList<DownloadJob> mCompletedJobs;
    private ArrayList<DownloadJob> downloadingQueue;

    private DownloadManager mDownloadManager;

    private DownloadDao mDownloadDao;

    private Handler mHandler;
    private int num = 0;
    private Runnable mRunnable;
//    private Context context;

    public DownloadProvider(DownloadManager mDownloadManager) {
        this.mDownloadManager = mDownloadManager;

        mQueuedJobs = new ArrayList<DownloadJob>();
        mCompletedJobs = new ArrayList<DownloadJob>();
        mDownloadDao = new DownloadDaoImpl();

        loadOldDownloads();
    }

    public ArrayList<DownloadJob> getAllDownloads() {
        ArrayList<DownloadJob> allDownloads = new ArrayList<DownloadJob>();
        allDownloads.addAll(mCompletedJobs);
        allDownloads.addAll(mQueuedJobs);
//		mCompletedJobs.clear();
//		ArrayList<DownloadJob> list = mDownloadDao.getAllDownloadJobs();
//		for (DownloadJob dJob : list) {
//			if (dJob.getProgress() == 100) {
//				mCompletedJobs.add(dJob);
//			}
//		}
        return allDownloads;
    }

    public ArrayList<DownloadJob> getCompletedDownloads() {
        return mCompletedJobs;
    }

    public ArrayList<DownloadJob> getQueuedDownloads() {
        return mQueuedJobs;
    }

    public SparseArray<DownloadFolderJob> getFolderJobs() {
        return folderJobs;
    }

    public int getRemainNum() {
        if (null != folderJobs && folderJobs.size() > 0) {
            int size = folderJobs.size();
            int remainNum = 0;
            for (int i = 0; i < size; i++) {
                DownloadFolderJob downloadFolder = folderJobs.valueAt(i);
                SparseArray<DownloadJob> downloadJobs = downloadFolder
                        .getDownloadJobs();
                if (null != downloadJobs) {
                    remainNum = remainNum + downloadJobs.size();
                }
            }
            return remainNum;
        }
        return 0;
    }

    public boolean loadOldDownloads() {
        ArrayList<DownloadJob> oldDownloads = mDownloadDao.getAllDownloadJobs();
        if (null == folderJobs) {
            folderJobs = new SparseArray<DownloadFolderJob>();
        }

        downloadingQueue = new ArrayList<DownloadJob>();
        for (DownloadJob dJob : oldDownloads) {
            if (dJob.getProgress() == 100) {
                mCompletedJobs.add(dJob);
                SparseArrayUtils.put(dJob, folderJobs);
            } else {
                if (dJob.getStatus() == DownloadJob.PAUSE) {
//					SparseArrayUtils.put(dJob, folderJobs);
                    dJob.setDownloadManager(mDownloadManager);
                    mQueuedJobs.add(dJob);
                } else {
                    dJob.getEntity().setIndex(dJob.getIndex());
                    /**
                     *
                     */
                    downloadingQueue.add(dJob);
//						mDownloadManager.download(dJob.getEntity());
                }
            }
        }
        if (downloadingQueue.size() > 0) {
            if (NetworkUtil.reportNetType(ChaoJiShiPinApplication.getInstatnce()) == NetworkUtil.TYPE_WIFI) {
                addToDownload();
            } else {
//				showConfirm();
                showMobileNetworkDialog();


                return false;
            }
        }
        mDownloadManager.notifyObservers(); // 更新UI界面
        return true;
    }

    public boolean queueDownload(DownloadJob downloadJob) {
        // 判断是否是第一次下载
        for (DownloadJob dJob : mCompletedJobs) {
            if (dJob.getEntity().getId()
                    .equals(downloadJob.getEntity().getId()))
                return false;
        }

        for (DownloadJob dJob : mQueuedJobs) {
            if (downloadJob.getEntity().getId()
                    .equals(dJob.getEntity().getId()))
                return false;
        }
        // 添加下载任务项到数据库中
        if (mDownloadDao.add(downloadJob.getEntity())) {
//			SparseArrayUtils.put(downloadJob, folderJobs);
            if (ContainSizeManager.getInstance().getFreeSize() <= Utils.SDCARD_MINSIZE) {
                downloadJob.setStatus(DownloadJob.PAUSE);
            }
            mQueuedJobs.add(downloadJob);
            mDownloadManager.notifyObservers();
            return true;
        } else {
            return false;
        }
    }

    public void setStatus(DownloadEntity entity, int status) {
        mDownloadDao.setStatus(entity, status);
    }

    public void setIfWatch(DownloadEntity entity, String ifWatch) {
        mDownloadDao.setIfWatch(entity, ifWatch);
//		LogUtils.d("dd", "原来=="+getAllDownloads().size());
        updateDownloads();
    }

    public boolean selectDownloadJobByMid(String mid) {
        return mDownloadDao.selectDownloadJobByMid(mid);
    }

    private void updateDownloads() {
        ArrayList<DownloadJob> oldDownloads = mDownloadDao.getAllDownloadJobs();
        if (null == folderJobs) {
            folderJobs = new SparseArray<DownloadFolderJob>();
        }
//		LogUtils.d("dd",mCompletedJobs.size()+"");
        folderJobs.clear();
        for (DownloadJob dJob : oldDownloads) {
            if (dJob.getProgress() == 100) {
//				int index = mCompletedJobs.indexOf(dJob);
//				if(-1 != index)
//					mCompletedJobs.set(index, dJob);
//					
//				ArrayList<DownloadJob> downloadList = MoviesApplication.getInstance()
//						.getDownloadManager().getAllDownloads();
//				LogUtils.d("dd","11---"+downloadList.size()+"");
                SparseArrayUtils.put(dJob, folderJobs);
//				ArrayList<DownloadJob> downloadList2 = MoviesApplication.getInstance()
//						.getDownloadManager().getAllDownloads();
//				LogUtils.d("dd","22----"+downloadList2.size()+"");
//				LogUtils.d("dd", "现在=="+getAllDownloads().size());
            } else {
//				if(dJob.getStatus()==DownloadJob.PAUSE){
//					SparseArrayUtils.put(dJob, folderJobs);
//					dJob.setDownloadManager(mDownloadManager);
//					mQueuedJobs.add(dJob);
//				}else{
//					dJob.getEntity().setIndex(dJob.getIndex());
//					mDownloadManager.download(dJob.getEntity());
//				}

            }
        }
        mDownloadManager.notifyObservers(); // 更新UI界面
    }

    public boolean updateDownloadEntity(DownloadJob downloadJob) {
        return mDownloadDao.add(downloadJob.getEntity());
    }

    public void removeDownload(DownloadJob job) {
        if (job.getProgress() < 100) {
            if (job.getStatus() == DownloadJob.DOWNLOADING)
                job.pauseOnOther(DownloadJob.PAUSE);
            mQueuedJobs.remove(job);
        } else {
            for (DownloadJob downloadJob : mCompletedJobs) {
                if (job.getEntity().getGlobaVid().equals(downloadJob.getEntity().getGlobaVid())) {
                    mCompletedJobs.remove(downloadJob);
                    break;
                }
            }
        }
        mDownloadDao.remove(job);
    }

    public void downloadCompleted(DownloadJob job) {
//		LogUtils.i("dyf", "下载成功：downloadCompleted");
        mQueuedJobs.remove(job);
        mCompletedJobs.add(job);
        SparseArrayUtils.put(job, folderJobs);
        mDownloadDao.setStatus(job.getEntity(), DownloadJob.COMPLETE);
        mDownloadManager.notifyObservers();
//		if(job.getCheck()){//如果下载完成的job，是删除界面中被勾选的，那么回调，通知被勾选数量自减。
        mDownloadManager.notifyDownloadEnd(job);
//		}
    }

    public ArrayList<DownloadJob> getDownloadJobsByMid(String mid) {
        return mDownloadDao.getDownloadJobsByMid(mid);
    }

    public void addToDownload()
    {
        if (downloadingQueue != null && downloadingQueue.size() > 0)
        {
            for (DownloadJob job : downloadingQueue) {
                mDownloadManager.download(job.getEntity());
            }
            downloadingQueue.clear();
        }
    }

    public boolean needContinueDownload() {
        if (downloadingQueue != null && downloadingQueue.size() > 0)
            return true;

        if (mQueuedJobs != null && mQueuedJobs.size() > 0)
        {
            for (DownloadJob job : mQueuedJobs
                    ) {
                if (job.getStatus() == DownloadJob.NO_USER_PAUSE
                        || job.getStatus() == DownloadJob.WAITING
                        || job.getStatus() == DownloadJob.DOWNLOADING)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void continueDownload() {
        if (downloadingQueue != null) {
            int noUserPauseCount = 0;
//            DownloadJob firstWaitingJob = null;
            for (DownloadJob job : downloadingQueue) {
                if (job.getStatus() == DownloadJob.NO_USER_PAUSE) {

                    if (noUserPauseCount == 0) {
//                        mDownloadManager.download(job.getEntity());
                        job.start();
                    }else {
                        job.setStatus(DownloadJob.WAITING);
                    }
                    noUserPauseCount++;
                }
            }
            downloadingQueue.clear();
            mDownloadManager.notifyObservers();
        }
    }

    public void pauseDownloadingJob() {

//        if (downloadingQueue != null) {
//            for (DownloadJob job : downloadingQueue) {
//                job.setStatus(DownloadJob.NO_USER_PAUSE);
//                mQueuedJobs.add(job);
//            }
//        }

        if (mQueuedJobs != null && mQueuedJobs.size() > 0)
        {
            if (downloadingQueue == null)
            {
                downloadingQueue = new ArrayList<>();
            }
            for (DownloadJob job: mQueuedJobs
                 ) {
                if (job.getStatus() == DownloadJob.DOWNLOADING)
                {
                    job.allPause();
                    job.setStatus(DownloadJob.NO_USER_PAUSE);
                    downloadingQueue.add(job);
                }else if (job.getStatus() == DownloadJob.NO_USER_PAUSE)
                {
                    downloadingQueue.add(job);
                }
            }
        }
//        if (downloadingQueue != null && downloadingQueue.size() > 0)
//        {
//            for (DownloadJob job : downloadingQueue
//                 ) {
//                mQueuedJobs.remove(job);
//            }
//        }

        mDownloadManager.notifyObservers();
    }

    public void pauseAllJobs()
    {
        if (mQueuedJobs != null && mQueuedJobs.size() > 0)
        {
            for (DownloadJob job : mQueuedJobs
                    ) {
                job.setStatus(DownloadJob.PAUSE);
                mDownloadDao.setStatus(job.getEntity(), DownloadJob.PAUSE);
//                mQueuedJobs.add(job);
            }
        }else if (mQueuedJobs == null || mQueuedJobs.size() == 0) {
            if (downloadingQueue != null && downloadingQueue.size() > 0) {
                for (DownloadJob job : downloadingQueue
                     ) {
                    job.setStatus(DownloadJob.PAUSE);
                    mDownloadDao.setStatus(job.getEntity(), DownloadJob.PAUSE);
                    mQueuedJobs.add(job);
                }
                downloadingQueue.clear();
            }

        }
        mDownloadManager.notifyObservers();
    }

    public void updateDatabaseValue(DownloadEntity entity, String key, String value) {
        mDownloadDao.updateValue(entity, key, value);
    }

    public void updateDatabaseValue(DownloadEntity entity, String key, int value) {
        mDownloadDao.updateValue(entity, key, value);
    }

    private void showMobileNetworkDialog()
    {
        NetworkInfo networkInfo = NetWorkUtils.getAvailableNetWorkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
            PopupDialog.showMobileNetworkAlert(buttonClick);
        }
    }

    PopupDialog.PopupButtonClickInterface buttonClick = new PopupDialog.PopupButtonClickInterface() {
        @Override
        public void onLeftClick() {
            addToDownload();
        }

        @Override
        public void onRightClick() {
            pauseAllJobs();
        }
    };

}
