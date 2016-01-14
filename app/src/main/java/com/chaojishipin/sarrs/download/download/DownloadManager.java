package com.chaojishipin.sarrs.download.download;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;


import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.config.SettingManage;
import com.chaojishipin.sarrs.download.activity.DownloadJobActivity;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.service.DownloadService;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.utils.NetWorkTypeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class DownloadManager {
    private Context mContext;
    private DownloadProvider mProvider;
    private ArrayList<DownloadObserver> mObservers;
    public int DOWNLOADING_NUM = 0;
    public ArrayList<DownloadEntity> entitys;
    private final String FROM = "download";

    // private PlayHistoryDao mPlayHistoryDao;

    public DownloadManager(Context context) {
        this.mContext = context;
        mObservers = new ArrayList<DownloadObserver>();
        this.mProvider = new DownloadProvider(this);

    }

    public DownloadManager() {

    }

    public boolean loadOldDownloads() {
        return mProvider.loadOldDownloads();
    }

    public void continueDownload() {
        mProvider.continueDownload();
    }

    public void pauseDownloadingJob() {
        mProvider.pauseDownloadingJob();
    }

    public void pauseAllJobs()
    {
        mProvider.pauseAllJobs();
    }

    public void addToDownload()
    {
        mProvider.addToDownload();
    }

    public boolean needContinueDownload() {
        return mProvider.needContinueDownload();
    }

    public void download(DownloadEntity entity) {
        entity.setDisplayName(DownloadHelper.constructName(entity));
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.ACTION_ADD_TO_DOWNLOAD);
        intent.putExtra(DownloadService.EXTRA_MEDIAITEM_ENTRY, entity);
        mContext.startService(intent);
    }

    public void download() {
        if (entitys == null)
            return;
        for (DownloadEntity entity : entitys) {
            download(entity);
        }
        entitys = null;
    }

    public void add(DownloadEntity entity) {
        if (entitys == null) {
            entitys = new ArrayList<DownloadEntity>();
        }
        entity.setDisplayName(DownloadHelper.constructName(entity));
        entitys.add(entity);
    }

    public boolean cancelAdd(DownloadEntity entity) {
        boolean hasRemoved = false;
        if (null != entity && null != entitys && entitys.size() > 0) {
            DownloadEntity addedEntity = null;
            for (int i = 0; i < entitys.size(); i++) {
                addedEntity = entitys.get(i);
                if (entity.getId().equals(addedEntity.getId())) {
                    entitys.remove(i);
                    hasRemoved = true;
                    break;
                }
            }
        }
        return hasRemoved;
    }

    public void cancel(DownloadEntity entity) {
        entitys = null;

    }

    public void download(ArrayList<DownloadJob> jobs, SparseArray<DownloadFolderJob> floderJobs) {

    }

    public DownloadProvider getProvider() {
        return mProvider;
    }

    public ArrayList<DownloadJob> getAllDownloads() {
        return mProvider.getAllDownloads();
    }

    public ArrayList<DownloadJob> getCompletedDownloads() {
        return mProvider.getCompletedDownloads();
    }

    public int getRemainNum() {
        return mProvider.getRemainNum();
    }


    public ArrayList<DownloadJob> getQueuedDownloads() {
        return mProvider.getQueuedDownloads();
    }

    public SparseArray<DownloadFolderJob> getDownloadFolderJobs() {
        return mProvider.getFolderJobs();
    }

    public synchronized void deregisterDownloadObserver(DownloadObserver observer) {
        mObservers.remove(observer);
    }

    public synchronized void registerDownloadObserver(DownloadObserver observer) {
        mObservers.add(observer);
    }

    public synchronized void notifyObservers() {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadChanged(this);
        }
    }

    public synchronized void notifyDownloadEnd(DownloadJob job) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadEnd(this, job);
        }
    }

    public void deleteDownload(DownloadJob job) {
        mProvider.removeDownload(job);
//        mProvider.
        job.notifyDownloadOnPause();
        DownloadEntity entity = job.getEntity();
        // add by xj:删除物理文件
        if (entity.getMid() != null) {
            deleteHistoryDb(job.getEntity().getMid(), job.getEntity().getId(), job);

        }
    }

    private void deleteHistoryDb(final String mid, final String hashid, final DownloadJob job) {
        new Thread() {
            public void run() {
                removeDownloadFromDisk(job);
                // if (mPlayHistoryDao == null) {
                // mPlayHistoryDao = PlayHistoryDao.getInstance(mContext);
                // }
                // PlayHistoryInfo playHistoryInfo = mPlayHistoryDao.queryByHashid(mid);
                // if (playHistoryInfo != null) {
                // LogUtils.e("SWS", "deleteHistoryDb - historyId == " + playHistoryInfo.getHashid()
                // + " , " + hashid);
                // String purl = playHistoryInfo.getPurl();
                // if (purl != null && !purl.equals("") && !purl.contains("jobsfe.funshion.com")
                // && !purl.contains("p.funshion.com")) {
                // mPlayHistoryDao.deleteHashId(hashid);
                // }
                // }
            };
        }.start();
    }

    private void removeDownloadFromDisk(DownloadJob job) {
        DownloadEntity Entity = job.getEntity();
        String downloadPath = null;
        String path = null;
        if (null != Entity) {
            downloadPath = Entity.getPath();
        }
        if (!TextUtils.isEmpty(downloadPath)) {
            path = DownloadHelper.getAbsolutePath(Entity, downloadPath);
        } else {
            path = DownloadHelper.getAbsolutePath(Entity, DownloadHelper.getDownloadPath());
        }
        try {
            File file = new File(path);
            if (file != null && file.exists()) {
                // file.delete();
                delete(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 递归，删除m3u8文件
     */
    public void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 下载m3u8/mp4之前，删除0字节的mp4/m3u8文件。
     * 因为在mp4下载时，会先创建空的mp4文件，但是mp4无法下载，切换到m3u8下载，这样，就冗余了一个空的mp4文件，需要删除.
     */
    public void deleteEmptyFile(String path) {
        File file = new File(path);
        if (null != file && file.isFile() && file.length() == 0) {
            file.delete();
        } else if (null != file && file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length <= 1) {
                delete(file);
            }
        }
    }

    // start next task
    public synchronized void startNextTask() {
        ArrayList<DownloadJob> queuedDownloads = mProvider.getQueuedDownloads();

        synchronized (queuedDownloads) {
            int num = getMaxDownloadNum();
            if (DOWNLOADING_NUM < num) {
                for (DownloadJob job : queuedDownloads) {
                    if ((job.getStatus() == DownloadJob.WAITING || job.getStatus() == DownloadJob.NO_USER_PAUSE)
                            && job.getExceptionType() != DownloadJob.NO_SD
                            && job.getExceptionType() != DownloadJob.FILE_NOT_FOUND) {
                        job.start();
                    }

                    if (DOWNLOADING_NUM >= num)
                        break;
                }
            }
        }
    }

    public void setStatus(DownloadEntity entity, int status) {
        mProvider.setStatus(entity, status);
    }

    public void setIfWatch(DownloadEntity entity, String ifWatch) {
        mProvider.setIfWatch(entity, ifWatch);
    }

    public boolean selectDownloadJobByMid(String mid) {
        return mProvider.selectDownloadJobByMid(mid);
    }

    public int getMaxDownloadNum() {
        return 1;
//        SharedPreferences bgDownloadSharePreference =
//                mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE,
//                        Context.MODE_PRIVATE);
//        String download_num =
//                bgDownloadSharePreference.getString(SettingManage.DOWNLOAD_NUM_SAME_TIME, "1");
//        return Integer.parseInt(download_num);
    }

    public boolean IsDownloadcan3g() {
        SharedPreferences bgDownloadSharePreference =
                mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE,
                        Context.MODE_PRIVATE);
        Boolean IsDownloadcan3g =
                bgDownloadSharePreference.getBoolean(SettingManage.IS_DOWNLOAD_CAN_3G, false);
        return IsDownloadcan3g;
    }

    public void playVideo(DownloadJob job) {
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
        mPlayData.setFrom(FROM);
        mPlayData.setmViewName(entity.getMedianame());

        // double watchableTime = job.getProgress() / 100.0;
        // downloadInfo.setWatchablePercent(watchableTime);
        // downloadInfo.setLocalFile(true);
        // downloadInfo.setSize(job.getTotalSize() + "");
        new PlayVideoTask().execute(mContext, mPlayData);
    }

    public void playVideoList(List<DownloadJob> jobs, DownloadJob job, String title) {
        ArrayList<LocalVideoEpisode> localVideoEpisodeList =
                new ArrayList<LocalVideoEpisode>(jobs.size());

        // 创建播放本地视频的类对象
        for (int i = 0; i < jobs.size(); i++) {
            DownloadEntity eDownloadEntity = jobs.get(i).getEntity();
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
            localVideoEpisode.setPlay_url(path);
            localVideoEpisode.setCid(eDownloadEntity.getCid());
            localVideoEpisode.setSite(eDownloadEntity.getSite());
            localVideoEpisode.setId(eDownloadEntity.getId());
            localVideoEpisode.setVt(eDownloadEntity.getVt());
            localVideoEpisode.setDownType(eDownloadEntity.getDownloadType());
            localVideoEpisodeList.add(localVideoEpisode);

        }

        DownloadEntity entity = job.getEntity();
        PlayData mPlayData = new PlayData();
        // 设置播放的是本地的文件
        mPlayData.setIsLocalVideo(true);
        mPlayData.setmLocalDataLists(localVideoEpisodeList);
        mPlayData.setPorder(entity.getPorder());// 知道播的是哪一集播放器
        mPlayData.setAid(entity.getMid());
        mPlayData.setFrom(FROM);
        mPlayData.setmViewName(title);
        new PlayVideoTask().execute(mContext, mPlayData);
    }

    public class PlayVideoTask extends AsyncTask<Object, integer, Object> {
        private Context context;
        private PlayData playData;
        // private PlayHistoryInfo historyInfo;
        private boolean isSendLocalHistory;

        @Override
        protected Object doInBackground(Object... params) {
            if (null != params[0] && params[0] instanceof Context) {
                context = (Context) params[0];
            }
            if (null != params[1] && params[1] instanceof PlayData) {
                playData = (PlayData) params[1];
            }

//            PlayRecord playRecord = null;
//            String aid = playData.getAid();
//
//            if (!TextUtils.isEmpty(aid)) {
//                if (playData.getIsLocalVideo()) {
//                    DownLoadPlayRecordDao downloadPlayRecord =
//                            DownLoadPlayRecordDao.getInstance(context);
//                    playRecord = downloadPlayRecord.getByAid(aid);
//                } else {
//                    PlayRecordDao playRecordDao = new PlayRecordDao(context);
//                    playRecord = playRecordDao.getByAid(aid);
//                }
//            }
//
//            if (null == playRecord) {
//                playRecord = new PlayRecord();
//            }
//
//            if (!TextUtils.isEmpty(playRecord.getPorder())
//                    && !TextUtils.isEmpty(playData.getPorder())
//                    && !TextUtils.isEmpty(playRecord.getPorder())
//                    && !playData.getPorder().equals(playRecord.getPorder())) {
//                // 如果当前播放的剧集与播放历史不一致则归零
//                playRecord.setSeekHistory(0);
//            }
//            playData.setmPlayRecord(playRecord);
            // PlayHistoryDao localPlayDao = PlayHistoryDao.getInstance(context);
            // if (downInfo.getMid() == null || "".equals(downInfo.getMid())) {
            // isSendLocalHistory = false;
            // } else {
            // isSendLocalHistory = true;
            // // 通过mid获取播放历史数据库中的数据
            // historyInfo = localPlayDao.queryByHashid(downInfo.getMid());
            // // 如果当前的类型为电影
            // // 判断之前是否有过播放历史
            // if (historyInfo == null) {
            // historyInfo = new PlayHistoryInfo();
            // } else {
            // if (!TextUtils.isEmpty(historyInfo.getHashid()) &&
            // !TextUtils.isEmpty(downInfo.getId())
            // && !historyInfo.getHashid().equals(downInfo.getId())) {
            // historyInfo.setPosition(0);
            // } else {
            // downInfo.setPosition(historyInfo.getPosition());
            // }
            // }
            // }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (isXIAOMI(mContext)) {
                LocalVideoEpisode localVideoEpisode = getCurrLocalEisode(playData);
                if (PlayerUtils.DOWNLOAD_M3U8.equalsIgnoreCase(localVideoEpisode.getDownType())) {
                    jumpToSystemPlayer(context, localVideoEpisode);
                } else {
                    jumpToSelfPlayer(context, playData);
                }
            } else {
                LocalVideoEpisode localVideoEpisode = getCurrLocalEisode(playData);
                //jumpToSystemPlayer(context, localVideoEpisode);
                if (!TextUtils.isEmpty(localVideoEpisode.getPorder())) {
                    playData.setIndex(localVideoEpisode.getIndex());
                }
                jumpToSelfPlayer(context, playData);
            }
        }
    }

    /**
     * 播放本地视频
     */

    private void jumpToSelfPlayer(Context context, PlayData playData) {
        //

    /*    Intent finishIntent = new Intent();
        finishIntent.setAction("com.chaojishipin.mediaplayer.page.finish");
        context.sendBroadcast(finishIntent);*/
        playData.setIsLocalVideo(true);
        // ToastUtil.showShortToast(context,"playData is "+playData.toString());
        Intent intent = new Intent(context, ChaoJiShiPinVideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utils.PLAY_DATA, playData);
        LocalVideoEpisode episode=playData.getmLocalDataLists().get(0);
        VideoDetailItem videoDetailItem = new VideoDetailItem();
        List<VideoItem> items=new ArrayList<>();
        VideoItem item=new VideoItem();
        item.setGvid(episode.getGvid());
        items.add(item);
        videoDetailItem.setLocalVideoEpisodes(playData.getmLocalDataLists());
        videoDetailItem.setTitle(episode.getName());
        videoDetailItem.setPorder(episode.getPorder());
        videoDetailItem.setDescription(episode.getDes());
        videoDetailItem.setId(episode.getAid());
        videoDetailItem.setCategory_id(episode.getCid());
        videoDetailItem.setPlay_count(episode.getPlayCount());
        videoDetailItem.setVideoItems(items);
        videoDetailItem.setFromMainContentType(item.getFromMainContentType());
       // videoDetailItem.setDetailImage(item.getImage());
        intent.putExtra("videoDetailItem", videoDetailItem);
        intent.putExtra("ref", DownloadJobActivity.pageid);
        bundle.putString(Utils.Medea_Mode, ConstantUtils.MediaMode.LOCAL);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 发送数据
        //EventBus.getDefault().post(playData);
        LogUtil.e("Local Media", "from downLoadManager " + playData);
        context.startActivity(intent);
    }


    /**
     * 当前播放的视频
     *
     * @param
     */
    private LocalVideoEpisode getCurrLocalEisode(PlayData playData) {
        ArrayList<LocalVideoEpisode> localDataLists = playData.getmLocalDataLists();
        String porder = playData.getPorder();
        if (!TextUtils.isEmpty(porder) && null != localDataLists && localDataLists.size() > 0) {
            int playSize = localDataLists.size();
            boolean isFindEpisode = false;
            // 查找当前影片位置
            for (int i = 0; i < playSize; i++) {
                LocalVideoEpisode localVideoEpisode = localDataLists.get(i);
                if (null != localVideoEpisode) {
                    if (porder.equals(localVideoEpisode.getPorder())) {
                        isFindEpisode = true;
                        return localVideoEpisode;
                    }
                }
            }
            // 如果没找到则取第一个
            if (!isFindEpisode) {
                return localDataLists.get(0);
            }
        }
        return null;
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

    /**
     * 检查是否已下载相同的视频，若有下载返回已存在信息
     * @param entity
     * @return
     */
    public DownloadEntity getDownloadEntity(DownloadEntity entity)
    {
        ArrayList<DownloadJob> jobs = getProvider().getAllDownloads();
        for (DownloadJob job:jobs) {
            if (job.getEntity().getGlobaVid().equals(entity.getGlobaVid()))
                return job.getEntity();
        }
        return null;
    }



    public void jumpToSystemPlayer(Context context, LocalVideoEpisode localVideoEpisode) {
        String playUrl = "file://" + localVideoEpisode.getPlay_url();
        if (!TextUtils.isEmpty(playUrl)) {
            playUrl.replace(" ", "%20");
            Uri uri = Uri.parse(playUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "video/mp4");
            // Activity activity = MoviesApplication.getInstance().getActivity();
            // if (null != activity) {
            context.startActivity(intent);
            // }
        }
    }

    public boolean isXIAOMI(Context context) {
        String deviceMode = Utils.getDeviceMode();
        String deviceVersion = Utils.getDeviceVersion();
        return ((deviceMode.contains(PlayerUtils.MI) || deviceMode.contains(PlayerUtils.XIAOMI))
                && deviceVersion.contains(PlayerUtils.XIAOMI_LOCAL_VERSION) && !NetWorkTypeUtils
                .isNetAvailable(context));
    }

}
