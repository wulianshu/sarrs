package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class RankList implements LetvBaseBean {
    private String title;
    private String play_count;
    private String rid;
    private String image;
    private String label;
    private String titleitems;

    public String getTitleitems() {
        return titleitems;
    }

    public void setTitleitems(String titleitems) {
        this.titleitems = titleitems;
    }

    private SarrsArrayList items;

    public void setItems(SarrsArrayList items) {
        this.items = items;
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

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
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

    public SarrsArrayList getItems() {
        return items;
    }
//
//    public void setItems(String items) {
//        this.items = items;
//    }

}
