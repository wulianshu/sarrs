package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * suggest里返回的play信息
 */
public class SearchSuggest_playinfo implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    private String vid;//视频id
    private String episoIndex;//

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getEpisoIndex() {
        return episoIndex;
    }

    public void setEpisoIndex(String episoIndex) {
        this.episoIndex = episoIndex;
    }

}
