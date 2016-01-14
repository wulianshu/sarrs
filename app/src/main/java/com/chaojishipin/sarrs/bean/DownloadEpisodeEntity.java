package com.chaojishipin.sarrs.bean;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * 全屏下载视频对象
 * Created by wangyemin on 2015/9/11.
 */
public class DownloadEpisodeEntity {
    private int key;
    private int index;
    private SparseArray<ArrayList<VideoItem>> mEpisodes;


    public DownloadEpisodeEntity(int key, int index, SparseArray<ArrayList<VideoItem>> mEpisodes) {
        this.key = key;
        this.index = index;
        this.mEpisodes = mEpisodes;
    }

    public int getKey() {
        return key;
    }

    public int getIndex() {
        return index;
    }

    public SparseArray<ArrayList<VideoItem>> getmEpisodes() {
        return mEpisodes;
    }

    @Override
    public String toString() {
        return "DownloadEpisodeEntity{" +
                "key=" + key +
                ", index=" + index +
                ", mEpisodes=" + mEpisodes +
                '}';
    }
}
