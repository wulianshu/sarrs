package com.chaojishipin.sarrs.bean;

/**
 * 评论信息
 * Created by wangyemin on 2015/9/29.
 * wiki 接口：http://wiki.letv.cn/pages/viewpage.action?pageId=45547650
 */
public class CommentsInfo extends LayersInfo {

    private String likeCount;
    private String head;
    private int isLike; // 用户是否点赞，0 没有
    private SarrsArrayList<LayersInfo> layers;

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public SarrsArrayList<LayersInfo> getLayers() {
        return layers;
    }

    public void setLayers(SarrsArrayList<LayersInfo> layers) {
        this.layers = layers;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", likeCount='" + likeCount + '\'' +
                ", head='" + head + '\'' +
                ", isLike=" + isLike +
                ", layersList=" + layers +
                '}';
    }
}
