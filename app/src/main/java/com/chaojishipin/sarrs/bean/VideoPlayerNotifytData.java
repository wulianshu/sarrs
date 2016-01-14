package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * Created by xll on 2015/7/6.
 * @des 播放器对半屏页剧集回调数据类型
 */
public class VideoPlayerNotifytData implements LetvBaseBean {

    //分页索引从0开始  展示数据使用  当前播放的数据所在分页

    private int key;
    //每个分页对应列表的点击位置 从0开始 展示数据使用 当前播放数据所在分页的位置index
    private int position;
    // 记录上一次 key

    public int getReqKey() {
        return reqKey;
    }

    public void setReqKey(int reqKey) {
        this.reqKey = reqKey;
    }

    public int getReqPosition() {
        return reqPosition;
    }

    public void setReqPosition(int reqPosition) {
        this.reqPosition = reqPosition;
    }

    private int reqKey;

    private int reqPosition;
    /**
     *   是否更新点击位置
     *   更新：
     *   1点击剧集更新 更新 key position
     *   2点击下一集按钮在当前分页中，只更新key position
     *   3.点击下一集按钮&最后一集，更新key position
     *   4.自动联播时播放&最新一集结束需要更新 key position
     *   5.
     *   不需要更新：
     *   3点击分页Tag不需要更新 position，但要更新key
     *
     *
     *
     * */
     private int updateMode;







    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
    // 第一次进入半屏页播放器回调参数
    private String gvid;

    public boolean isFirst() {
        return isFirst;
    }
    /**
     * @param isFirst 是否首页进入模式
     * */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getGvid() {
        return gvid;
    }

    public void setGvid(String gvid) {
        this.gvid = gvid;
    }

    private  boolean isFirst;

    private String type;

    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }
    /**
     *  @param isLocal 播放器是否播放本地视频
     * */
    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNeedNextPage() {
        return isNeedNextPage;
    }

    public void setIsNeedNextPage(boolean isNeedNextPage) {
        this.isNeedNextPage = isNeedNextPage;
    }

    private boolean isNeedNextPage;

}
