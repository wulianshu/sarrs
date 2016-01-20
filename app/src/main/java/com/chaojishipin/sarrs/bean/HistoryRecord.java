package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 专题列表
 * Created by wulianshu on 2015/8/24.
 */
public class HistoryRecord implements LetvBaseBean {

    public final static String tablename="history_record";
    private String timestamp;
    private String id;
    private String title;
    private String source;
    private String category_name;
    private String play_time;
    private String gvid;
    private String image;
    private String category_id;
    private String content_type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    private boolean isCheck = false;
    private int durationTime;

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final String FIELD_ID = "id";
    public static final String FIELD_TIMESTMAP = "timestamp";
    public static final String FIELD_AID = "aid";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_SOURCE = "source";
    public static final String FIELD_CATEGORY_NAME = "category_name";
    public static final String FIELD_PLAY_TIME = "play_time";
    public static final String FIELD_GVID = "gvid";
    public static final String FIELD_IMAGE = "image";
    public static final String FIELD_CATEGORY_ID = "category_id";
    public static final String FIELD_CONTENT_TYPE = "content_type";
    
    public HistoryRecord(String timestamp, String title, String source, String category_name, String play_time, String gvid, String image, String category_id, String content_type) {
        this.timestamp = timestamp;
        this.title = title;
        this.source = source;
        this.category_name = category_name;
        this.play_time = play_time;
        this.gvid = gvid;
        this.image = image;
        this.category_id = category_id;
        this.content_type = content_type;
    }

    public HistoryRecord() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

}
