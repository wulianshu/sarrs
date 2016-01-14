package com.chaojishipin.sarrs.feedback;

import com.letv.http.bean.LetvBaseBean;

/**
 * 解析类
 * Created by wangyemin on 2015/10/10.
 */
public class DataReport implements LetvBaseBean {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
