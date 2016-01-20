package com.chaojishipin.sarrs.bean;

import android.util.SparseArray;

import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/7/6.
 */
public class PlayData implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    /**
     * 影片名称
     */
    private String mTitle;
    private String mGvid;
    private int recordposition;//播放记录


   // 展示使用
    private int mKey;//SparseArray中的key，实际就是第几个分页的key
    private int mIndex;//SparseArray中key对应的Arraylist的index
    //点击全屏 tag 记录下 index
    private int tagIndex;
    // 半屏tagIndex
    public int getTagIndex2() {
        return tagIndex2;
    }

    public void setTagIndex2(int tagIndex2) {
        this.tagIndex2 = tagIndex2;
    }

    private int tagIndex2;


    public int getTagIndex() {
        return tagIndex;
    }

    public void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
    }



    // 当且仅当剧集中只有本地剧集时才返回true
    private boolean isLocalVideo;
    // source 播放源
    private String  source;

    private SparseArray<ArrayList<VideoItem>> mEpisodes;
    /**
     * 跳转来源页面
     */
    private String mFrom;
    /**
     * 视频源
     */
    private String mSite = "letv";
    /**
     * 剧集分页List，播放器剧集面板使用
     */
    private List<String> mPage_titles;
    /**
     * 剧集面板分页展示需要
     */
    private String mCid;

    private ArrayList<LocalVideoEpisode> mLocalDataLists;
    private String mViewName;
    private String aid;
    private String porder;

    public int getRecordposition() {
        return recordposition;
    }

    public void setRecordposition(int recordposition) {
        this.recordposition = recordposition;
    }


    public PlayData()
    {

    }

    /**
     * 首页及搜索页跳转
     */
    public PlayData(String mTitle, String mGvid, String from,String source) {
        this.mTitle = mTitle;
        this.mGvid = mGvid;
        this.mFrom = from;
        this.source=source;
    }
    /**
     * 下载页跳转跳转
     */
    public PlayData(ArrayList<LocalVideoEpisode> mLocal, String from,String source,String  porder) {
        this.mFrom = from;
        this.source=source;
        this.mLocalDataLists=mLocal;
        this.porder=porder;
    }
    /**
     * 详情页传递剧集数据  刚进入详情页时调用
     *
     * @param episodes
     * @param page_titles
     * @param cid  剧集面板分页展示需要
     */
    public PlayData(SparseArray<ArrayList<VideoItem>> episodes, List<String> page_titles ,String cid,String from) {

        this.mEpisodes = episodes;
        this.mFrom = from;
        this.mPage_titles = page_titles;
        this.mCid = cid;

    }

  /*  *//**
     * 全屏剧集分页请求
     *
     * @param key
     * @param from
     *
     *//*
    public PlayData(int key, String from) {

        this.mFrom = from;
        this.mKey=key;

    }*/




    /**
     * 详情页点击剧集面板
     *
     * @param key
     * @param position
     */
    public PlayData(SparseArray<ArrayList<VideoItem>> episodes, int key, int position, String from) {
        this.mEpisodes = episodes;
        this.mIndex = position;
        this.mKey = key;
        this.mFrom = from;
    }



    /**
     * 播放器全屏点击剧集分页tag
     *
     * @param episodes
     * @param key
     * @param index
     * @param tagIndex
     * @param from
     */
    public PlayData(SparseArray<ArrayList<VideoItem>> episodes,int key,int index,int tagIndex,String from) {
       this.mEpisodes = episodes;
        this.mIndex=index;
        this.mKey=key;
       this.mFrom = from;
        this.tagIndex=tagIndex;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmGvid() {
        return mGvid;
    }

    public void setmGvid(String mGvid) {
        this.mGvid = mGvid;
    }

    public SparseArray<ArrayList<VideoItem>> getmEpisodes() {
        return mEpisodes;
    }

    public void setmEpisodes(SparseArray<ArrayList<VideoItem>> mEpisodes) {
        this.mEpisodes = mEpisodes;
    }

    public Boolean getIsLocalVideo() {
        return isLocalVideo;
    }

    public void setIsLocalVideo(Boolean isLocalVideo) {
        this.isLocalVideo = isLocalVideo;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        this.mKey = key;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }
    public String getSite() {
        return mSite;
    }

    public void setSite(String mSite) {
        this.mSite = mSite;
    }
    public List<String> getPage_titles() {
        return mPage_titles;
    }

    public void setPage_titles(List<String> page_titles) {
        this.mPage_titles = page_titles;
    }
    public String getCid() {
        return mCid;
    }

    public void setCid(String mCid) {
        this.mCid = mCid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<LocalVideoEpisode> getmLocalDataLists() {
        return mLocalDataLists;
    }

    public void setmLocalDataLists(ArrayList<LocalVideoEpisode> mLocalDataLists) {
        this.mLocalDataLists = mLocalDataLists;
    }

    public String getPorder() {
        return porder;
    }

    public void setPorder(String porder) {
        this.porder = porder;
    }

    public String getAid() {
        return aid;
    }
    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getmViewName() {
        return mViewName;
    }



    public void setmViewName(String mViewName) {
        this.mViewName = mViewName;
    }

}
