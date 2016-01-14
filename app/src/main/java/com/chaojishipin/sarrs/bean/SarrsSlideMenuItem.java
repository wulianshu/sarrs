package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 *
 */
public class SarrsSlideMenuItem implements LetvBaseBean {


    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int resId;
    private String title;


}
