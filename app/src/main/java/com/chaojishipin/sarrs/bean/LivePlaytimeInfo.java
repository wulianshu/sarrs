package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

public class LivePlaytimeInfo implements LetvBaseBean{

    /**
     * 仅作为demo，需要删除
     */
    private static final long serialVersionUID = 1L;
    
    private String title;
    
    private String playTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

}
