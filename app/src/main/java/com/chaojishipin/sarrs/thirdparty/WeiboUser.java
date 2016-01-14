package com.chaojishipin.sarrs.thirdparty;


import com.letv.http.bean.LetvBaseBean;

public class WeiboUser implements LetvBaseBean {

    /**
     * weibo user info
     */
    private static final long serialVersionUID = -6128362701952941902L;
    private String name;
    private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

