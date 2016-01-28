package com.chaojishipin.sarrs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.async.PlayVideoTask;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.config.SettingManage;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadEntityDBBuilder;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadProvider;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.mylib.download.ShelfDownload;
import com.mylib.download.ShelfDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liuzhuo on 2016/1/21.
 */
public class DataUtils {

    private Context mContext;
    private static DataUtils util;
    private ShelfDownloadManager mDownloadManager;
    private DownloadProvider mDownloadProvider;
    private boolean isChange = false;

    public synchronized static DataUtils getInstance() {
        if (util == null)
            util = new DataUtils(ChaoJiShiPinApplication.getInstatnce());
        return util;
    }

    private DataUtils(Context context) {
        if(context == null)
            throw new RuntimeException("init DataUtil with null context");
        this.mContext = context.getApplicationContext();
        init();
    }

    private void init() {
        mDownloadManager = new ShelfDownloadManager(mContext);
        mDownloadProvider = DownloadProvider.getInstance();
    }

    public void download(DownloadJob job){
        ShelfDownload down = new ShelfDownload(mDownloadManager.getModule(), job);
        mDownloadManager.download(down);
    }

    public void addDownloadListener(ShelfDownloadManager.IShelfDownloadListener l) {
        if (mDownloadManager != null) {
            mDownloadManager.addDownloadListener(l);
        }
    }

    public void removeDownloadListener(ShelfDownloadManager.IShelfDownloadListener l) {
        if (mDownloadManager != null) {
            mDownloadManager.removeDownloadListener(l);
        }
    }

    public void startDownload(DownloadJob job){
        ShelfDownload down = new ShelfDownload(mDownloadManager.getModule(), job);
        mDownloadManager.startDownload(down);
    }

    public boolean download(DownloadEntity entry){
        String downloadPath = DownloadHelper.getDownloadPath();
        DownloadJob job = new DownloadJob(entry, downloadPath);
        job.setIndex(entry.getIndex());
        if (mDownloadProvider.queueDownload(job)) {
            startDownload(job);
            return true;
        }
        return false;
    }

    /**
     * 下载中的任务数
     * @return
     */
    public int getDownloadingJobNum(){
        return mDownloadManager.getDownloadingJobNum();
    }

    public void pauseAllDownload(){
        mDownloadManager.pauseAll();
    }

    private void pauseDownload(ShelfDownload down){
        mDownloadManager.pauseDownload(down);
    }

    public void deleteDownloadFile(DownloadJob job){
        if(job.getStatus() != DownloadJob.COMPLETE) {
            ShelfDownload down = new ShelfDownload(mDownloadManager.getModule(), job);
            pauseDownload(down);
        }
        deleteFolder(getLocalFile(job).getAbsolutePath());
        mDownloadProvider.removeDownload(job);
    }

    /**
     * 没下载完的所有任务
     * @return
     */
    public SparseArray<DownloadJob> getDownloadJobArray(){
        SparseArray<DownloadJob> array = new SparseArray<DownloadJob>();
        HashMap<String, DownloadJob> map = mDownloadProvider.getDownloadJobsMap();
        ArrayList<DownloadJob> list = mDownloadManager.getAllDownloadingJobs();
        for(DownloadJob job : list){
            map.remove(job.getEntity().getId());
            array.put(job.getEntity().getAddTime(), job);
        }
        Iterator<Map.Entry<String, DownloadJob>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, DownloadJob> e = it.next();
            DownloadJob j = e.getValue();
            array.put(j.getEntity().getAddTime(), j);
        }
        return array;
    }

    /**
     * 下载中的任务
     * @return
     */
    public ArrayList<DownloadJob> getDownloadingJobs(){
        return mDownloadManager.getAllDownloadingJobs();
    }

    /**
     * 没有下载完的任务数
     * @return
     */
    public int getDownloadJobNum(){
        return mDownloadProvider.getDownloadJobNum();
    }

    /**
     * 下载过的任务，包括未完成和已完成
     * @return
     */
    public int getAllDownloadJobNum(){
        return mDownloadProvider.getAllDownloadJobNum();
    }

    public void updateDownloadStatus(DownloadJob job, int status){
        mDownloadProvider.setStatus(job.getEntity(), status);
        job.setStatus(status);
        mDownloadProvider.updateDownloads(job);
    }

    public static File getLocalFile(DownloadJob job){
        File f = new File(DownloadHelper.getAbsolutePath(job.getEntity(), job.getEntity().getPath()));
        if(f.getParentFile().exists() && f.getParentFile().isDirectory())
            ;
        else
            f.getParentFile().mkdirs();
        return f;
    }

    public void setView(DownloadJob job, ProgressBar bar, TextView speed, TextView percent){
        switch (job.getStatus()) {
            case DownloadJob.INIT:
                break;
            case DownloadJob.DOWNLOADING:
                bar.setVisibility(View.VISIBLE);
                bar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download));
                bar.setProgress((int) (job.getDownloadedSize() * 100F / job.getTotalSize()));
                percent.setVisibility(View.VISIBLE);
                speed.setText(job.getRate());
                final String len;
                if (DownloadInfo.MP4.equals(job.getEntity().getDownloadType())) {
                    len = DownloadUtils.getDownloadedSize(job.getDownloadedSize()) + "M/" + DownloadUtils.getDownloadedSize(job.getTotalSize()) + "M";
                } else {
                    len = mContext.getString(R.string.compulate_size);
                }
                percent.setText(len);
                break;
            case DownloadJob.FAIL:
                bar.setVisibility(View.VISIBLE);
                bar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                speed.setText(mContext.getResources().getString(R.string.download_faile));
                percent.setVisibility(View.INVISIBLE);
                break;
            case DownloadJob.PAUSE:
            case DownloadJob.NO_USER_PAUSE:
                bar.setVisibility(View.VISIBLE);
                bar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                speed.setText(mContext.getResources().getString(R.string.already_pause_download));
                percent.setVisibility(View.INVISIBLE);
                break;
            case DownloadJob.WAITING:
                bar.setVisibility(View.VISIBLE);
                bar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                speed.setText("等待中");
                percent.setVisibility(View.INVISIBLE);
                break;
            case DownloadJob.COMPLETE:
                bar.setVisibility(View.INVISIBLE);
                speed.setText("下载完成");
                percent.setVisibility(View.INVISIBLE);
                if (DownloadInfo.M3U8.equals(job.getEntity().getDownloadType())) {
                    if (job.getEntity().getFileSize() < 1024 * 1024) {
                        job.getEntity().setFileSize(DownloadHelper.getDownloadedFileSize(job.getEntity(), job.getEntity().getPath()));
                    }
                    percent.setText(DownloadUtils.getTotalSize(job.getEntity().getFileSize()) + "MB");
                } else {
                    percent.setText(DownloadUtils.getTotalSize(DownloadHelper.getDownloadedFileSize(job.getEntity(), job.getEntity().getPath())) + "MB");
                }
                break;
            default:
                break;
        }
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param sPath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    private boolean deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            File tmp = new File(file.getAbsolutePath() + ".tmp");
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    private boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        if(files == null)
            return true;
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateDownloadType(DownloadJob job, String type){
        mDownloadProvider.updateDatabaseValue(job.getEntity(), DownloadEntityDBBuilder.DOWNLOAD_TYPE, type);
    }

    public void setChange(boolean bo){
        isChange = bo;
    }

    public void startAllDownload(){
        if(!isChange)
            return;
        ArrayList<DownloadJob> list = mDownloadProvider.getQueuedDownloads();
        for(DownloadJob j : list)
            startDownload(j);
    }

    public boolean selectDownloadJobByMid(String mid) {
        return mDownloadProvider.selectDownloadJobByMid(mid);
    }

    public SparseArray<DownloadFolderJob> getFolderJobs(){
        return mDownloadProvider.getFolderJobs();
    }

    public void setIfWatch(DownloadEntity entity, String ifWatch) {
        mDownloadProvider.setIfWatch(entity, ifWatch);
    }

    public ArrayList<LocalVideoEpisode> getLocalVideoEpisodes(DownloadFolderJob folderJob) {
        SparseArray<DownloadJob> jobs = folderJob.getDownloadJobs();
        ArrayList<LocalVideoEpisode> localVideoEpisodeList =
                new ArrayList<LocalVideoEpisode>(jobs.size());
        DownloadJob job = jobs.valueAt(0);
        // 创建播放本地视频的类对象
        for (int i = 0; i < jobs.size(); i++) {
            DownloadEntity eDownloadEntity = jobs.valueAt(i).getEntity();
            String path;
            if (DownloadInfo.M3U8.equals(eDownloadEntity.getDownloadType())) {
                path =
                        DownloadHelper.getAbsolutePath(eDownloadEntity, eDownloadEntity.getPath())
                                + "/" + eDownloadEntity.getSaveName() + ".m3u8";
            } else {
                path =
                        "file://"
                                + DownloadHelper.getAbsolutePath(eDownloadEntity,
                                eDownloadEntity.getPath());
            }


            LocalVideoEpisode localVideoEpisode = new LocalVideoEpisode();
            localVideoEpisode.setAid(eDownloadEntity.getMid());
            localVideoEpisode.setPorder(eDownloadEntity.getPorder());
            localVideoEpisode.setName(eDownloadEntity.getMedianame());
            localVideoEpisode.setTitle(eDownloadEntity.getMedianame());
            localVideoEpisode.setPlay_url(path);
            localVideoEpisode.setCid(eDownloadEntity.getCid());
            localVideoEpisode.setTitle(eDownloadEntity.getMedianame());
            localVideoEpisode.setSite(eDownloadEntity.getSite());
            localVideoEpisode.setSource(eDownloadEntity.getSite());
            localVideoEpisode.setId(eDownloadEntity.getId());
            localVideoEpisode.setGvid(eDownloadEntity.getGlobaVid());
            localVideoEpisode.setVt(eDownloadEntity.getVt());
            localVideoEpisode.setDownType(eDownloadEntity.getDownloadType());
            localVideoEpisodeList.add(localVideoEpisode);

        }
        return localVideoEpisodeList;
    }

    public void playVideo(Activity ac, DownloadJob job) {
        String path;
        if (DownloadInfo.M3U8.equals(job.getEntity().getDownloadType())) {
            path =
                    DownloadHelper.getAbsolutePath(job.getEntity(), job.getEntity().getPath())
                            + "/" + job.getEntity().getSaveName() + ".m3u8";
        } else {
            path =
                    "file://"
                            + DownloadHelper.getAbsolutePath(job.getEntity(), job.getEntity()
                            .getPath());
        }

        // DownloadPlayInfo downloadInfo = new DownloadPlayInfo();
        DownloadEntity entity = job.getEntity();
        // 创建播放本地视频的类对象
        LocalVideoEpisode localVideoEpisode = new LocalVideoEpisode();
        // TODO 单视频时 mid=gvid
        PlayData mPlayData = new PlayData();
        if(!entity.getMid().equalsIgnoreCase(entity.getGlobaVid())){
            localVideoEpisode.setAid(entity.getMid());
            localVideoEpisode.setId(entity.getId());
            mPlayData.setAid(entity.getMid());
        }
        localVideoEpisode.setDownType(entity.getDownloadType());
        localVideoEpisode.setPorder(entity.getPorder());
        localVideoEpisode.setCid(entity.getCid());
        localVideoEpisode.setName(entity.getMedianame());
        localVideoEpisode.setPlay_url(path);
        localVideoEpisode.setGvid(entity.getGlobaVid());
        localVideoEpisode.setVt(entity.getVt());
        localVideoEpisode.setSource(entity.getSite());
        ArrayList<LocalVideoEpisode> localVideoEpisodeList = new ArrayList<LocalVideoEpisode>(1);
        localVideoEpisodeList.add(localVideoEpisode);

        // 设置播放的是本地的文件
        mPlayData.setSource(entity.getSite());
        mPlayData.setIsLocalVideo(true);
        mPlayData.setmLocalDataLists(localVideoEpisodeList);
        mPlayData.setPorder(entity.getPorder());// 知道播的是哪一集播放器
        mPlayData.setFrom("download");
        mPlayData.setmViewName(entity.getMedianame());

        new PlayVideoTask(ac).execute(ac, mPlayData);
    }

    public int getRemainNum() {
        return mDownloadProvider.getRemainNum();
    }

    public ArrayList<DownloadJob> getCompletedDownloads() {
        return mDownloadProvider.getCompletedDownloads();
    }

    public boolean IsDownloadcan3g() {
        SharedPreferences bgDownloadSharePreference =
                mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE,
                        Context.MODE_PRIVATE);
        Boolean IsDownloadcan3g =
                bgDownloadSharePreference.getBoolean(SettingManage.IS_DOWNLOAD_CAN_3G, false);
        return IsDownloadcan3g;
    }

    public void updateDownloadEntity(DownloadJob job){
        mDownloadProvider.updateDownloadEntity(job);
    }

    /**
     * 检查是否已下载相同的视频，若有下载返回已存在信息
     * @param entity
     * @return
     */
    public DownloadEntity getDownloadEntity(DownloadEntity entity)
    {
        ArrayList<DownloadJob> jobs = mDownloadProvider.getAllDownloads();
        for (DownloadJob job:jobs) {
            if (job.getEntity().getGlobaVid().equals(entity.getGlobaVid()))
                return job.getEntity();
        }
        return null;
    }

    public ArrayList<DownloadJob> getAllDownloads() {
        return mDownloadProvider.getAllDownloads();
    }

    public boolean needContinueDownload() {
        return mDownloadProvider.needContinueDownload();
    }
}
