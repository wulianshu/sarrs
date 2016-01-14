package com.chaojishipin.sarrs.thirdparty;


import com.letv.http.bean.LetvBaseBean;

public class WeiXinToken implements LetvBaseBean{
    /**
     *
     */
    private static final long serialVersionUID = 2770777152848280344L;

    public String access_token;
    public String refresh_token;
    public String openId;
}
