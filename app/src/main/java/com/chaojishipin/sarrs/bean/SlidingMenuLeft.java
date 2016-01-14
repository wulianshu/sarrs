package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * Created by zhangshuo on 2015/6/6.
 * 左侧侧边栏数据Javabean
 */
public class SlidingMenuLeft implements LetvBaseBean {

    private boolean isChange;

    private String content_type;

    private String icon;

    private String title;

    private String cid;

    private String sid;

    public String getIcon_select() {
        return icon_select;
    }

    public void setIcon_select(String icon_select) {
        this.icon_select = icon_select;
    }

    private String icon_select;
    //个人信息

    // 推荐列表
    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String type) {
        this.content_type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}

