package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 用户评论
 * Created by wangyemin on 2015/9/29.
 */
public class UserCommentInfo implements LetvBaseBean {
    private int total;
    private SarrsArrayList<CommentsInfo> hotComments;
    private SarrsArrayList<CommentsInfo> comments;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public SarrsArrayList<CommentsInfo> getHotComments() {
        return hotComments;
    }

    public void setHotComments(SarrsArrayList<CommentsInfo> hotComments) {
        this.hotComments = hotComments;
    }

    public SarrsArrayList<CommentsInfo> getComments() {
        return comments;
    }

    public void setComments(SarrsArrayList<CommentsInfo> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "UserCommentInfo{" +
                "total=" + total +
                ", hotCommentsList=" + hotComments +
                ", commentsList=" + comments +
                '}';
    }
}
