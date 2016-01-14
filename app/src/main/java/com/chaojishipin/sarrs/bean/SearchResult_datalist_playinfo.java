package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * SearchResult里返回的play信息
 */
public class SearchResult_datalist_playinfo implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    private String vid;//视频id
    private String global_vid;//
    private String aid;//视频所在专辑id
    private String global_aid;//
    private String dataType;//数据类型 1=专辑 2=单视频

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getGlobal_vid() {
        return global_vid;
    }

    public void setGlobal_vid(String global_vid) {
        this.global_vid = global_vid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getGlobal_aid() {
        return global_aid;
    }

    public void setGlobal_aid(String global_aid) {
        this.global_aid = global_aid;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
