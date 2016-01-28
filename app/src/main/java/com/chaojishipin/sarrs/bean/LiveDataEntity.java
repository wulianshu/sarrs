package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 直播数据类
 * Created by wangyemin on 2016/1/24.
 */
public class LiveDataEntity implements LetvBaseBean {
    private String icon;
    private List<LiveProgramEntity> programs;
    private String channelName;
    private String rec;
    private String channelId;
    private String poster;
    private String channelEname;
    private List<LiveStreamEntity> streams;
    private String cid;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<LiveProgramEntity> getPrograms() {
        return programs;
    }

    public void setPrograms(List<LiveProgramEntity> programs) {
        this.programs = programs;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getRec() {
        return rec;
    }

    public void setRec(String rec) {
        this.rec = rec;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getChannelEname() {
        return channelEname;
    }

    public void setChannelEname(String channelEname) {
        this.channelEname = channelEname;
    }

    public List<LiveStreamEntity> getStreams() {
        return streams;
    }

    public void setStreams(List<LiveStreamEntity> streams) {
        this.streams = streams;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "LiveDataEntity{" +
                "icon='" + icon + '\'' +
                ", programs=" + programs +
                ", channelName='" + channelName + '\'' +
                ", rec='" + rec + '\'' +
                ", channelId='" + channelId + '\'' +
                ", poster='" + poster + '\'' +
                ", channelEname='" + channelEname + '\'' +
                ", streams=" + streams +
                ", cid='" + cid + '\'' +
                '}';
    }
}
