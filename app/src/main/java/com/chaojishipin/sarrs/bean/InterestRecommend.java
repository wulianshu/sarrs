package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 兴趣推荐
 * Created by wangyemin on 2015/10/13.
 */
public class InterestRecommend implements LetvBaseBean {
    private String status;
    private String message;
    private String reid;
    private String bucket;
    private SarrsArrayList<InterestEntity> items;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public SarrsArrayList<InterestEntity> getItems() {
        return items;
    }

    public void setItems(SarrsArrayList<InterestEntity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "InterestRecommend{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", reid='" + reid + '\'' +
                ", bucket='" + bucket + '\'' +
                ", items=" + items +
                '}';
    }
}
