package com.chaojishipin.sarrs.bean;

/**
 * Created by wulianshu on 2016/1/11.
 */
public class Upgradinfo {
    private int upgrad;
    private String title;
    private int ischeck;
    private String status;
    private String description;
    private String upgradelink ;
    private String version;

    public int getUpgrad() {
        return upgrad;
    }

    public void setUpgrad(int upgrad) {
        this.upgrad = upgrad;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIscheck() {
        return ischeck;
    }

    public void setIscheck(int ischeck) {
        this.ischeck = ischeck;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpgradelink() {
        return upgradelink;
    }

    public void setUpgradelink(String upgradelink) {
        this.upgradelink = upgradelink;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
