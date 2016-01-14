package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 性别信息封装类
 */
public class GenderInfo implements LetvBaseBean {
    private String gender;
    private boolean isClick;
    public GenderInfo(String gender,boolean isClcik){
        this.gender=gender;
        this.isClick=isClcik;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setIsClick(boolean isClick) {
        this.isClick = isClick;
    }

}
