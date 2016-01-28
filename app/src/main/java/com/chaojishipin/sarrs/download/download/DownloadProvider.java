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
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.PopupDialog;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DownloadProvider {

    private SparseArray<DownloadFolderJob> folderJobs = new SparseArray<DownloadFolderJob>();
    private ArrayList<DownloadJob> mCompleteList = new ArrayList<>();
    private ArrayList<DownloadJob> mUnCompleteList = new ArrayList<>();
    private Set<String> mCompleteGvidSet = new HashSet<>();
    private Set<String> mUnCompleteGvidSet = new HashSet<>();

    private DownloadDao mDownloadDao;
    private static DownloadProvider mProvider;

    public static synchronized DownloadProvider getInstance(){
        if(mProvider == null)
            mProvider = new DownloadProvider();
        return mProvider;
    }

    private DownloadProvider() {
        mDownloadDao = new DownloadDaoImpl();
        loadOldDownloads();
    }

    public ArrayList<DownloadJob> getAllDownloads() {
        ArrayList<DownloadJob> list = new ArrayList<>();
        list.addAll(mCompleteList);
        list.addAll(mUnCompleteList);
        return list;
    }

    public ArrayList<DownloadJob> getCompletedDownloads() {
        return mCompleteList;
    }

    public ArrayList<DownloadJob> getQueuedDownloads() {
        return mUnCompleteList;
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

    public boolean updateDownloadEntity(DownloadJob downloadJob) {
        return mDownloadDao.add(downloadJob.getEntity());
    }

    public void updateDownloads(DownloadJob job){
        if(folderJobs == null) {
            loadOldDownloads();
            return;
        }
        if(job.getStatus() == DownloadJob.COMPLETE) {
            SparseArrayUtils.put(job, folderJobs);
            mCompleteList.add(job);
            for(DownloadJob j : mUnCompleteList){
                if(j.getEntity().getId().equalsIgnoreCase(job.getEntity().getId())) {
                    mUnCompleteList.remove(j);
                    break;
                }
            }
            mCompleteGvidSet.add(job.getEntity().getGlobaVid());
            mUnCompleteGvidSet.remove(job.getEntity().getGlobaVid());
        }
    }

    public boolean loadOldDownloads() {
        ArrayList<DownloadJob> oldDownloads = mDownloadDao.getAllDownloadJobs();
        mCompleteList.clear();
        mUnCompleteList.clear();
        mCompleteList.addAll(oldDownloads);
        for (DownloadJob dJob : oldDownloads) {
            if(dJob.getStatus() == DownloadJob.COMPLETE) {
                SparseArrayUtils.put(dJob, folderJobs);
                mCompleteList.add(dJob);
                mCompleteGvidSet.add(dJob.getEntity().getGlobaVid());
            }else {
                mUnCompleteList.add(dJob);
                mUnCompleteGvidSet.add(dJob.getEntity().getGlobaVid());
            }
        }
        return true;
    }

    public boolean queueDownload(DownloadJob downloadJob) {
        // 判断是否是第一次下载
        if(mDownloadDao.isDownloaded(downloadJob.getEntity().getId()))
            return false;

        // 添加下载任务项到数据库中
        if (mDownloadDao.add(downloadJob.getEntity())) {
            if (ContainSizeManager.getInstance().getFreeSize() <= Utils.SDCARD_MINSIZE) {
                downloadJob.setStatus(DownloadJob.PAUSE);
            }
            mUnCompleteList.add(downloadJob);
            mUnCompleteGvidSet.add(downloadJob.getEntity().getGlobaVid());
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
    }

    public boolean selectDownloadJobByMid(String mid) {
        return mDownloadDao.selectDownloadJobByMid(mid);
    }

    public void removeDownload(DownloadJob job) {
        mDownloadDao.remove(job);
        List<DownloadJob> list;
        if(job.getStatus() == DownloadJob.COMPLETE) {
            list = mCompleteList;
            mCompleteGvidSet.remove(job.getEntity().getGlobaVid());
        }else {
            list = mUnCompleteList;
            mUnCompleteGvidSet.remove(job.getEntity().getGlobaVid());
        }
        for(DownloadJob j : list){
            if(j.getEntity().getId().equalsIgnoreCase(j.getEntity().getId())){
                list.remove(j);
                break;
            }
        }
//        SparseArrayUtils.remove(job, folderJobs);
    }

    public ArrayList<DownloadJob> getDownloadJobsByMid(String mid) {
        return mDownloadDao.getDownloadJobsByMid(mid);
    }

    public boolean needContinueDownload() {
        if (mUnCompleteList != null && mUnCompleteList.size() > 0)
            return true;
        return false;
    }

    public void updateDatabaseValue(DownloadEntity entity, String key, String value) {
        mDownloadDao.updateValue(entity, key, value);
    }

    public void updateDatabaseValue(DownloadEntity entity, String key, int value) {
        mDownloadDao.updateValue(entity, key, value);
    }

    public HashMap<String, DownloadJob> getDownloadJobsMap(){
        return mDownloadDao.getDownloadJobsMap();
    }

    public int getDownloadJobNum(){
        return mDownloadDao.getDownloadJobNum();
    }

    public int getAllDownloadJobNum(){
        return mDownloadDao.getAllDownloadJobNum();
    }

    public Set<String> getCompleteGvidSet(){
        return mCompleteGvidSet;
    }

    public Set<String> getmUnCompleteGvidSet(){
        return mUnCompleteGvidSet;
    }
}
