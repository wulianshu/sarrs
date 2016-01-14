package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 退出登陆类
 */
public class LogOutInfo implements LetvBaseBean {
    private static final long serialVersionUID = 6046632453129007556L;
   // 默认0 修改成功置为1
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
