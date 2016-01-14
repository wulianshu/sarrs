package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * Created by vicky on 15/11/15.
 */
public class UploadFile implements LetvBaseBean {
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    private String file;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state;
}
