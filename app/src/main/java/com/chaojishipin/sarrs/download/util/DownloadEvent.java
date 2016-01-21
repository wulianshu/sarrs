package com.chaojishipin.sarrs.download.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.VideoInfoAdapter;
import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadManager;
import com.chaojishipin.sarrs.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by vicky on 15/9/1.
 */
public class DownloadEvent {
    private VideoDetailItem mVideoDetailItem;
    //    private static ContainSizeManager mSizeManager;
    private DownloadManager downLoadManager;
    private int addTime;
    private Activity mCurrentActivity;

    /**
     * click download button, start download
     */
    public boolean downloadFile(Activity activity, VideoDetailItem item, int index) {
        mCurrentActivity = activity;
        mVideoDetailItem = item;
//        mSizeManager = new ContainSizeManager(activity);
//        ChaoJiShiPinApplication.getInstatnce().setActivityStack(activity);
        downLoadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();

        if (mVideoDetailItem != null) {
            //sd卡容量大于500m，可以添加
//            if (ContainSizeManager.getFreeSize() > Utils.SDCARD_MINSIZE) {
            Episode episode = null;
            episode = VideoInfoAdapter.wrapVideoDetail(mVideoDetailItem, index);
            if (downLoadManager.selectDownloadJobByMid(episode.getSerialid())) {
                String content = mCurrentActivity.getResources().getString(R.string.down_exists);
                ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), content);
            } else {
                String time = "" + System.currentTimeMillis();//时间戳，记录点击时间，在下载中界面排序
                String temp = time.substring(2); //去掉前两位
                addTime = Integer.parseInt(1 + temp.substring(2, temp.length()));//去掉最后一位

//                if (ContainSizeManager.getFreeSize() > Utils.SDCARD_MINSIZE) {
                //判断是否3g下载
                if (NetworkUtil.reportNetType(activity) == 2) {
                    checkIfContinueDownloadDialog(index, null, mVideoDetailItem.getId(), episode, mVideoDetailItem.getSource(), addTime);
                } else {
                    updateAddedDownloadItem(index, null, mVideoDetailItem.getId(), episode, mVideoDetailItem.getSource(), addTime);
                    confirmDownload(index);
                }
//                } else {
//
//                }
            }
            return true;
        }
        return false;
    }

    //打开3g下载开关对话框
    private void checkIfContinueDownloadDialog(final int position, final SparseArray<Boolean> selectedList
            , final String mid, final Episode episode, final String site, final int addTime) {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(mCurrentActivity);
        customBuilder
                .setTitle(R.string.tip)
                .setMessage(R.string.wireless_tip)
                .setPositiveButton(R.string.continue_download,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateAddedDownloadItem(position, selectedList, mid, episode, site, addTime);
                                confirmDownload(position);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.pause_download,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            dialog.dismiss();
                        }
                        return false;
                    }
                });
        Dialog dialog = customBuilder.create();
        dialog.show();
    }

    //确认下载
    public void confirmDownload(int position) {
        boolean addDownloadSuccess = false;
        long curTime = System.currentTimeMillis();
        Log.d("useTime", "start ");
        addDownloadSuccess = addToDownload();
        Log.d("useTime", "use time is " + (System.currentTimeMillis() - curTime));
        Log.d("useTime", "end ");
        if (!addDownloadSuccess) {
            ToastUtil.showLongToast(ChaoJiShiPinApplication.getInstatnce(), R.string.addfailure, position + 1);
            return;
        }
        ToastUtil.showLongToast(mCurrentActivity, R.string.addok);
        Log.d("order","add");
    }

    public boolean addToDownload() {
        boolean addSuccess = false;
        ArrayList<DownloadEntity> downloadList = ChaoJiShiPinApplication.getInstatnce().getDownloadManager().entitys;
        if (null != downloadList && downloadList.size() > 0) {
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().entitys = downloadList;
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().download();
            addSuccess = true;
        }
        return addSuccess;
    }

    // 创建下载任务实体
    public boolean updateAddedDownloadItem(int position, SparseArray<Boolean> selectedList
            , String mid, Episode episode, String site, int addTime) {
        boolean addSuccess = false;
        DownloadEntity entity = buildDownloadEntity(position, mid, episode, site, addTime);
        if (null != entity) {
            ChaoJiShiPinApplication.getInstatnce().getDownloadManager().add(entity);
        } else {
//            LogUtils.i(TAG, "添加失败 -- position == " + position);
        }
        return addSuccess;
    }

    private DownloadEntity buildDownloadEntity(int position, String mediaid, Episode episode, String site, int addTime) {
        DownloadEntity entity = null;
        if ((!TextUtils.isEmpty(mediaid) && null != episode)
                || (TextUtils.isEmpty(mediaid) && null != episode && episode.getVid() != null && episode.getVid().length() > 0)) {  //单视频
//				String mediaName = episode.getName();
            entity = new DownloadEntity();
            entity.setPorder(episode.getPorder());
            if (mediaid != null)         //专辑
            {
                entity.setMid(mediaid);
            }else {                      //单视频
                entity.setMid(episode.getVid());
            }

            if (mVideoDetailItem.getVideoItems() != null && mVideoDetailItem.getVideoItems().get(position) != null) {
                VideoItem videoItem = mVideoDetailItem.getVideoItems().get(position);

                if(!TextUtils.isEmpty(videoItem.getSource())){
                    entity.setSite(videoItem.getSource());
                    entity.setSrc(videoItem.getSource());
                }
                if(!TextUtils.isEmpty(videoItem.getCategory_id())){
                    entity.setCid(videoItem.getSource());
                }
                if (videoItem.getTitle() != null && videoItem.getTitle().length() > 0)
                    entity.setMedianame(mVideoDetailItem.getVideoItems().get(position).getTitle());
                else
                    entity.setMedianame(mVideoDetailItem.getTitle());
            } else {
                entity.setMedianame(mVideoDetailItem.getTitle());
            }
            if(!TextUtils.isEmpty(site)){
                entity.setSite(site);
            }

            entity.setLetvMid(episode.getMid());
            entity.setPath(DownloadHelper.getDownloadPath());
            entity.setAddTime(addTime);
            entity.setSnifferUrl(episode.getPlay_url());
            entity.setFolderName(episode.getName());
//            entity.setCurrClarity(mdefaultClarity);
            entity.setGlobaVid(episode.getGlobaVid());
            if(!TextUtils.isEmpty(mVideoDetailItem.getSource())){
                entity.setSrc(mVideoDetailItem.getSource());
                entity.setSite(mVideoDetailItem.getSource());
            }

//            if ("nets".equals(mVideoDetailItem.getSource())) {//云盘下载id
//                entity.setCloudId(episode.getCloudId());
//            }
            if (null != episode) {
                entity.setId(episode.getSerialid());
                entity.setTaskname(episode.getPorder());
                entity.setIndex(position);
            }
            entity.setImage(episode.getImage());
            entity.setDesc(episode.getIntro());
            entity.setCid(episode.getCid());
        }
        return entity;
    }
}
