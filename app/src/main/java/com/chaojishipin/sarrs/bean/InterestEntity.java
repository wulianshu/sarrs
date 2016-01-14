package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 兴趣选择
 * Created by wangyemin on 2015/10/13.
 */
public class InterestEntity implements LetvBaseBean {
    private String id;
    private String title;
    private String image;
    private int category_id;
    private int content_type;

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
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    @Override
    public String toString() {
        return "InterestEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", category_id=" + category_id +
                ", content_type=" + content_type +
                '}';
    }
}
