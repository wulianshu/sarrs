package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 专题列表
 * Created by wulianshu on 2015/8/24.
 */
public class Topic implements LetvBaseBean {

    private String title;
    private String play_count;
    private Integer data_count;
    private String description;
    private String image;
    private String label;
    private String tid;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public SarrsArrayList getItems() {
        return items;
    }

    public void setItems(SarrsArrayList items) {
        this.items = items;
    }

    private SarrsArrayList items;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String source;

    public Topic() {
    }

    public Topic(String title, String play_count, Integer data_count, String description, String image, String label, String tid) {
        this.title = title;
        this.play_count = play_count;
        this.data_count = data_count;
        this.description = description;
        this.image = image;
        this.label = label;
        this.tid = tid;
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

    public Integer getData_count() {
        return data_count;
    }

    public void setData_count(Integer data_count) {
        this.data_count = data_count;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

}
