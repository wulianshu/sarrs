package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;

/**
 * Created by zhangshuo on 2015/6/17.
 * @des 首页item数组数据
 */
public class MainActivityData implements LetvBaseBean {

    private String reid;

    private String bucket;

    private String category;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String source;

    private ArrayList<MainActivityAlbum> albumList;

    public String getReid() {
        return reid;
    }

    public void setReid(String reid) {
        this.reid = reid;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<MainActivityAlbum> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(ArrayList<MainActivityAlbum> albumList) {
        this.albumList = albumList;
    }

}
