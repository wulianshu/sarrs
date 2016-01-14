package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 评论消息体
 * Created by wangyemin on 2015/10/10.
 */
public class CommentEntity implements LetvBaseBean {
    public static final long NULL_PARENT = (long) -1;
    private long id;// 评论信息id
    private long parentId;     // 盖楼楼层中第一层id
    private long uid;//用户id
    private String head;
    private String name;
    private Long time;
    private String content;
    private int isLike;
    private int type;
    private String soundUrl;
    private int soundSecond;
    private String likeCount;  // 点赞数
    private int floorNum;      //楼层数目

    public CommentEntity(long id, long parentId, long uid, String head, String name, Long time, String content, int isLike, int type, String soundUrl, int soundSecond, String likeCount, int floorNum) {
        this.id = id;
        this.parentId = parentId;
        this.uid = uid;
        this.head = head;
        this.name = name;
        this.time = time;
        this.content = content;
        this.isLike = isLike;
        this.type = type;
        this.soundUrl = soundUrl;
        this.soundSecond = soundSecond;
        this.likeCount = likeCount;
        this.floorNum = floorNum;
    }

    public static long getNullParent() {
        return NULL_PARENT;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public long getUid() {
        return uid;
    }

    public String getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public Long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public int getIsLike() {
        return isLike;
    }

    public int getType() {
        return type;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public int getSoundSecond() {
        return soundSecond;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public int getFloorNum() {
        return floorNum;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", uid=" + uid +
                ", head='" + head + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", isLike=" + isLike +
                ", type=" + type +
                ", soundUrl='" + soundUrl + '\'' +
                ", soundSecond=" + soundSecond +
                ", likeCount='" + likeCount + '\'' +
                ", floorNum=" + floorNum +
                '}';
    }
}
