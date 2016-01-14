package com.chaojishipin.sarrs.feedback;

/**
 * Created by wangyemin on 2015/10/10.
 */
public interface DataReportListener {
    /**
     * 上报成功
     */
    public void reportSucess();

    /**
     * 上报失败
     */
    public void reportFail();
}
