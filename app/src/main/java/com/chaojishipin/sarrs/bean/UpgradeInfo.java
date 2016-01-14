package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 版本升级
 * Created by wangyemin on 2015/8/27.
 */
public class UpgradeInfo implements LetvBaseBean {
    private String upgrade;
    private String ischeck;
    private String description;
    private String upgradelink;
    private String version;

    public String getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(String upgrade) {
        this.upgrade = upgrade;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
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

    @Override
    public String toString() {
        return "UpgradeInfo{" +
                "upgrade='" + upgrade + '\'' +
                ", ischeck='" + ischeck + '\'' +
                ", description='" + description + '\'' +
                ", upgradelink='" + upgradelink + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
