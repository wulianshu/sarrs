package com.chaojishipin.sarrs.bean;


/**
 * Created by xll on 2015/6/17.
 * 详情页 item元素里面video数组
 */
public class VideoItem extends VideoDetailItem {

    private String title;

    @Override
    public String toString() {
        return "VideoItem{" +
                "title='" + title + '\'' +
                ", order='" + order + '\'' +
                ", gvid='" + gvid + '\'' +
                ", image='" + image + '\'' +
                ", source='" + source + '\'' +
                ", isLocal=" + isLocal +
                ", play_url='" + play_url + '\'' +
                ", from='" + from + '\'' +
                ", isPlay=" + isPlay +
                ", key=" + key +
                ", index=" + index +
                '}';
    }

    private String order;  //级数
    private String gvid;
    private String image;
    private String source;
    private boolean isLocal;

    private String play_url;
    /**
     * 从首页或者搜索页进入播放器，的标识，用来给详情页传递gvid
     */
    private String from;

    private boolean isPlay;

    @Override
    public String getBucket() {
        return super.getBucket();
    }

    @Override
    public String getReid() {
        return super.getReid();
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int key;
    private int index;


    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }


    public String getSupertitle(){
      return  super.getTitle();
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getGvid() {
        return gvid;
    }

    public void setGvid(String gvid) {
        this.gvid = gvid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


}
