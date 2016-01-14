/**
 *
 */
package com.chaojishipin.sarrs.bean;

import java.util.Date;

/**
 * @author xll
 * @des 首页侧滑按钮点击状态存放类
 */
public class MainMenuItem {

    private  boolean isDelete;

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean isSare() {
        return isSare;
    }

    public void setIsSare(boolean isSare) {
        this.isSare = isSare;
    }

    private  boolean isSave;
    private  boolean isSare;
}
