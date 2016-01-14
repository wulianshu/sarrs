package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 头像信息封装类
 */
public class ImageInfo implements LetvBaseBean {
    private int id;
    private String url;
    private int type;
    private int resId;
    private int defaultResId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getDefaultResId() {
        return defaultResId;
    }

    public void setDefaultResId(int defaultResId) {
        this.defaultResId = defaultResId;
    }

}
