package com.chaojishipin.sarrs.bean;

import com.chaojishipin.sarrs.utils.StringUtil;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuo on 2015/6/17.
 */
public class MainActivityAlbum implements LetvBaseBean,Cloneable{


    private String id;
    private String title;
    private String play_count;
    private String description;
    private String imgage;
    private String lable;
    private String contentType;
    private String source;

    //

    private String bucket;



    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    private String reId;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    private String category_id;





    public List<VideoItem> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoItem> videos) {
        this.videos = videos;
    }

    private List<VideoItem> videos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlay_count() {
        return play_count;
    }

    public void setPlay_count(String play_count) {
        this.play_count = play_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgage() {
        return imgage;
    }

    public void setImgage(String imgage) {
        this.imgage = imgage;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReId() {
        return reId;
    }

    public void setReId(String reId) {
        this.reId = reId;
    }
}
