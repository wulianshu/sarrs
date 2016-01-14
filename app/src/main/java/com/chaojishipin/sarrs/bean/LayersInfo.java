package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 盖楼信息
 * Created by wangyemin on 2015/9/29.
 */
public class LayersInfo implements LetvBaseBean {
    protected Long uid;
    protected String text;
    protected String name;
    protected int type; // 评论类型 0 文字 1 语音
    protected long commentTime;
    protected int soundSecond;
    protected String soundUrl;
    protected long commentId;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public int getSoundSecond() {
        return soundSecond;
    }

    public void setSoundSecond(int soundSecond) {
        this.soundSecond = soundSecond;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        return "LayersInfo{" +
                "uid='" + uid + '\'' +
                ", text='" + text + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", commentTime='" + commentTime + '\'' +
                ", soundSecond='" + soundSecond + '\'' +
                ", soundUrl='" + soundUrl + '\'' +
                ", commentId='" + commentId + '\'';
    }
}
