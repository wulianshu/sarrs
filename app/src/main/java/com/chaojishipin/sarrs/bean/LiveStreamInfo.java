package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 直播流地址信息类
 * Created by wangyemin on 2016/1/26.
 */
public class LiveStreamInfo implements LetvBaseBean {
    private List<LiveStreamEntity> rows;

    public List<LiveStreamEntity> getRows() {
        return rows;
    }

    public void setRows(List<LiveStreamEntity> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "LiveStreamInfo{" +
                "rows=" + rows +
                '}';
    }
}
