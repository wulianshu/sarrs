package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 添加评论
 * Created by wangyemin on 2015/10/8.
 */
public class AddComment implements LetvBaseBean {
    // 默认0 评论成功置为1
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AddComment{" +
                "state=" + state +
                '}';
    }
}
