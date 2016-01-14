package com.chaojishipin.sarrs.bean;

import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/7/6.
 * @des 详情页接口数据 最外层item元素
 */
public class VideoDetailItem implements LetvBaseBean {

    private int page_index;
    private String publish_date;
    private String play_count;
    private String episo_num;
    private String score;
    private String sub_category_name;
    private String category_id;  //频道ID：电影(category_id: 2)，电视剧(category_id: 1)， 动漫(category_id: 3)
    private String play_status;
    private String publisher;
    private String id; //aid

    public String getFtitle() {
        ftitle = title;
        return ftitle;
    }

    protected String title;
    protected String ftitle;
    private String description;
    private String area_name;
    private String metadata;
    private String episo_latest;
    private String source;
    private List<VideoItem> videoItems;
    private String fromMainContentType;
    private String detailImage;  //专辑图片
    private String is_end;
    //本地播放剧集列表 点击项 索引
    private String porder;
    //推荐id
    private String bucket;

    public String getReid() {
        return reid;
    }

    public void setReid(String reid) {
        this.reid = reid;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    //搜索id
    private String reid;


    public String getPorder() {
        return porder;
    }

    public void setPorder(String porder) {
        this.porder = porder;
    }




    private ArrayList<LocalVideoEpisode> localVideoEpisodes;
    public ArrayList<LocalVideoEpisode> getLocalVideoEpisodes() {
        return localVideoEpisodes;
    }

    public void setLocalVideoEpisodes(ArrayList<LocalVideoEpisode> localVideoEpisodes) {
        this.localVideoEpisodes = localVideoEpisodes;
    }



    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    private String img;

    private String content_type; //内容类型（目前搜索结果中，所有content_type都采用同样的一种样式-小图）1：专辑 2：单视频视频，专辑区分

    public String getFromMainContentType() {
        return fromMainContentType;
    }

    public void setFromMainContentType(String fromMainContentType) {
        this.fromMainContentType = fromMainContentType;
    }

    public String getIs_end() {
        return is_end;
    }

    public void setIs_end(String is_end) {
        this.is_end = is_end;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    private String actor;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    private String director;

    public List<String> getMataList() {
        return mataList;
    }

    public void setMataList(List<String> mataList) {
        this.mataList = mataList;
    }

    private List<String> mataList;

    public int getPage_index() {
        return page_index;
    }

    public void setPage_index(int page_index) {
        this.page_index = page_index;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getPlay_count() {
        return play_count;
    }

    public void setPlay_count(String play_count) {
        this.play_count = play_count;
    }

    public String getEpiso_num() {
        return episo_num;
    }

    public void setEpiso_num(String episo_num) {
        this.episo_num = episo_num;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getPlay_status() {
        return play_status;
    }

    public void setPlay_status(String play_status) {
        this.play_status = play_status;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.ftitle = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getEpiso_latest() {
        return episo_latest;
    }

    public void setEpiso_latest(String episo_latest) {
        this.episo_latest = episo_latest;
    }

    public List<VideoItem> getVideoItems() {
        return videoItems;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    public List<String> getPage_titles() {
        return page_titles;
    }

    public void setPage_titles(List<String> page_titles) {
        this.page_titles = page_titles;
    }

    private List<String> page_titles;

    @Override
    public String toString() {
        return "VideoDetailItem{" +
                "page_index=" + page_index +
                ", publish_date='" + publish_date + '\'' +
                ", play_count='" + play_count + '\'' +
                ", episo_num='" + episo_num + '\'' +
                ", score='" + score + '\'' +
                ", sub_category_name='" + sub_category_name + '\'' +
                ", category_id='" + category_id + '\'' +
                ", play_status='" + play_status + '\'' +
                ", publisher='" + publisher + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", area_name='" + area_name + '\'' +
                ", metadata='" + metadata + '\'' +
                ", episo_latest='" + episo_latest + '\'' +
                ", source='" + source + '\'' +
                ", videoItems=" + videoItems +
                ", fromMainContentType='" + fromMainContentType + '\'' +
                ", content_type='" + content_type + '\'' +
                ", actor='" + actor + '\'' +
                ", director='" + director + '\'' +
                ", mataList=" + mataList +
                ", page_titles=" + page_titles +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDetailImage() {
        return detailImage;
    }

    public void setDetailImage(String detailImage) {
        this.detailImage = detailImage;
    }

}
