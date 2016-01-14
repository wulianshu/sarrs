package com.chaojishipin.sarrs.download.dao;

import android.text.TextUtils;
import android.util.SparseArray;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadJob;

import java.util.ArrayList;

/**
 * Created by vicky on 15/9/5.
 */
public class DownloadDao {
    // 根据下载数据库中的数据更新已下载标记
    public static SparseArray<Boolean> updateDownloadedFlagByDB(ArrayList<VideoItem> episodeList,SparseArray<Boolean> isSelected) {
        ArrayList<DownloadJob> downloadList = ChaoJiShiPinApplication.getInstatnce()
                .getDownloadManager().getAllDownloads();
        if (null != downloadList && downloadList.size() > 0) {
            DownloadEntity entity;
            String downloadSerialsId;
            for (int i = 0; i < downloadList.size(); i++) {
                entity = downloadList.get(i).getEntity();
                if (null != entity) {
                    downloadSerialsId = entity.getGlobaVid();
                    if (null != episodeList && episodeList.size() > 0 && !TextUtils.isEmpty(downloadSerialsId)) {
                        String serialId;
                        for (int j = 0; j < episodeList.size(); j++) {
                            serialId = episodeList.get(j).getGvid();
                            if (downloadSerialsId.equals(serialId)) {
                                isSelected.put(j,true);
                            }
                        }
                    }
                }
            }
        }
        return isSelected;
    }
}
