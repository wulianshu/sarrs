package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * Created by xll on 2015/7/6.
 * @des 详情页获取单视频索引
 */
public class VideoDetailIndex implements LetvBaseBean {

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getGaid() {
        return gaid;
    }

    public void setGaid(String gaid) {
        this.gaid = gaid;
    }

    public String getGvid() {
        return gvid;
    }

    public void setGvid(String gvid) {
        this.gvid = gvid;
    }

    private String gaid;
    private String gvid;
    private int index;
    private int pn;

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    private int postion;


    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

}
