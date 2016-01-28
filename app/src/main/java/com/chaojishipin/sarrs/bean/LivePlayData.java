package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播信息类
 * Created by wangyemin on 2016/1/26.
 */
public class LivePlayData implements LetvBaseBean {
    private String title;
    private List<String> liveStreams;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLiveStreams() {
        return liveStreams;
    }

    public void setLiveStreams(List<String> liveStreams) {
        this.liveStreams = liveStreams;
    }
}
