package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;


public class SearchSuggestDataList implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    private int view_type;//adapter中getItemViewType的类型，默认为0，只有tip界面为1或2

    private String category;//频道名
    private String title;//搜索提示词 <>表示标红区域
    private String episoCount;//剧集数量
    private String images;//海报图地址
    private String global_aid;//全局id
    private String aid;//专辑id
    private SearchSuggest_playinfo searchSuggest_playinfo;//播放信息


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisoCount() {
        return episoCount;
    }

    public void setEpisoCount(String episoCount) {
        this.episoCount = episoCount;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getGlobal_aid() {
        return global_aid;
    }

    public void setGlobal_aid(String global_aid) {
        this.global_aid = global_aid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public int getView_type() {
        return view_type;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public SearchSuggest_playinfo getSearchSuggest_playinfo() {
        return searchSuggest_playinfo;
    }

    public void setSearchSuggest_playinfo(SearchSuggest_playinfo searchSuggest_playinfo) {
        this.searchSuggest_playinfo = searchSuggest_playinfo;
    }

    @Override
    public String toString() {
        return "SearchSuggestDataList{" +
                "view_type=" + view_type +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", episoCount='" + episoCount + '\'' +
                ", images='" + images + '\'' +
                ", global_aid='" + global_aid + '\'' +
                ", aid='" + aid + '\'' +
                ", searchSuggest_playinfo=" + searchSuggest_playinfo +
                '}';
    }

}
