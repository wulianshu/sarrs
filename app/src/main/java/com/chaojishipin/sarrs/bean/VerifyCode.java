package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 二维码
 */
public class VerifyCode implements LetvBaseBean {
    private static final long serialVersionUID = 6046632453129007556L;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

}
