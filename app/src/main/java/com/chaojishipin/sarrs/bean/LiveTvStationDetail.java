package com.chaojishipin.sarrs.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;

/**
 * 仅作为demo，需要删除
 */
public class LiveTvStationDetail implements LetvBaseBean {

    /**
     * zhangshuo 2015年1月8日 上午10:58:28
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String icon;

    private String url;
    
    private ArrayList<String> streams;

    private ArrayList<LivePlaytimeInfo> playTimeList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<LivePlaytimeInfo> getPlayTimeList() {
        return playTimeList;
    }

    public void setPlayTimeList(ArrayList<LivePlaytimeInfo> playTimeList) {
        this.playTimeList = playTimeList;
    }
    
    public ArrayList<String> getStreams() {
        return streams;
    }

    public void setStreams(ArrayList<String> streams) {
        this.streams = streams;
    }

}
