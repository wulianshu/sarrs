package com.chaojishipin.sarrs.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;

/**
 * 仅作为demo，需要删除
 */
public class LiveClassifyInfos implements LetvBaseBean {

    /**
     * zhangshuo 2015年1月8日 上午10:50:12
     */
    private static final long serialVersionUID = 1L;
    
    private String cheineseName;

    private String identifier;

    public String getCheineseName() {
        return cheineseName;
    }

    public void setCheineseName(String cheineseName) {
        this.cheineseName = cheineseName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    private ArrayList<LiveTvStationDetail> mChannels;

    public ArrayList<LiveTvStationDetail> getmChannels() {
        return mChannels;
    }

    public void setmChannels(ArrayList<LiveTvStationDetail> mChannels) {
        this.mChannels = mChannels;
    }

}
