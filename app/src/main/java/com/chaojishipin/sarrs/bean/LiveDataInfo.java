package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 首页直播数据信息
 * 接口：http://play.chaojishipin.com/sarrs/livehall?pl=1000010&appv=1.0.0&appfrom=appstore
 * Created by wangyemin on 2016/1/24.
 */
public class LiveDataInfo implements LetvBaseBean {
    private List<LiveDataEntity> rows;

    public List<LiveDataEntity> getRows() {
        return rows;
    }

    public void setRows(List<LiveDataEntity> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "LiveDataInfo{" +
                "rows=" + rows +
                '}';
    }
}
