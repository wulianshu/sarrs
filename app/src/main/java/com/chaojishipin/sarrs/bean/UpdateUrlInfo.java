package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 播放器请求失败后，调用更新url接口
 * @author xll
 */
public class UpdateUrlInfo implements LetvBaseBean {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}
