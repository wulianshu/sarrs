package com.chaojishipin.sarrs.bean;

/**
 * Created by xulinlin on 2016/1/27.
 */
public class StorageBean {

    private boolean isClick;
    private String name;
    private boolean isEnable;
    private String path;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setIsClick(boolean isClick) {
        this.isClick = isClick;
    }



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type; // 0,1,2

    public boolean isEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }
}
