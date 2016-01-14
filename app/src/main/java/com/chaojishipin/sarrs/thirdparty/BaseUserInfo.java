package com.chaojishipin.sarrs.thirdparty;


import com.letv.http.bean.LetvBaseBean;

public class BaseUserInfo implements LetvBaseBean {
    /**
     *
     */
    private static final long serialVersionUID = 18946619153837266L;
    private String name;
    private String avatar;
    private String type;
    private String uid;
    private String token;
    private String phone;
    private int gender;
    private String openId;
    private String signature;
    private boolean isFirst;  //标示用户是否第一次登录(手机登录)
    private String errorCode;  // 请求状态码

    public boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "BaseUserInfo{" +
                "name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", type='" + type + '\'' +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", openId='" + openId + '\'' +
                ", signature='" + signature + '\'' +
                ", isFirst=" + isFirst +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
