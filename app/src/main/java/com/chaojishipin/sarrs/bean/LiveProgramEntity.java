package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 直播节目实体类
 * Created by wangyemin on 2016/1/24.
 */
public class LiveProgramEntity implements LetvBaseBean {
    private String title;
    private String beginTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public String toString() {
        return "LiveProgramEntity{" +
                "title='" + title + '\'' +
                ", beginTime='" + beginTime + '\'' +
                '}';
    }
}
