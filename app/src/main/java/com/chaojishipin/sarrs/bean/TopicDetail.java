package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * Created by wulianshu on 2015/8/24.
 */
public class TopicDetail implements LetvBaseBean {

    private String title;
    private String play_count;
    private String episo_num;
    private String gaid;
    private String description;
    private String image;
    private Integer category_id;
    private String label;
    private String episo_latest;
    private String content_type;
    private String source;

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private List<VideoItem> videos;

    public String getEpiso_latest() {
        return episo_latest;
    }

    public void setEpiso_latest(String episo_latest) {
        this.episo_latest = episo_latest;
    }

    public TopicDetail() {
    }

    public TopicDetail(String title, String play_count, String episo_num, String gaid, String description, String image, Integer category_id, String label,List<VideoItem> videos,String episo_latest) {
        this.title = title;
        this.play_count = play_count;
        this.episo_num = episo_num;
        this.gaid = gaid;
        this.description = description;
        this.image = image;
        this.category_id = category_id;
        this.label = label;
        this.videos = videos;
        this.episo_latest = episo_latest;
    }

    public List<VideoItem> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoItem> videos) {
        this.videos = videos;
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

    public String getEpiso_num() {
        return episo_num;
    }

    public void setEpiso_num(String episo_num) {
        this.episo_num = episo_num;
    }

    public String getGaid() {
        return gaid;
    }

    public void setGaid(String gaid) {
        this.gaid = gaid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}