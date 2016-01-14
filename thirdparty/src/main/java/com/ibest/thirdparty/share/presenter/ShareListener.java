package com.ibest.thirdparty.share.presenter;

/**
 * Created by vicky on 15/10/15.
 */
public interface ShareListener {
    /**
     * 分享完成
     */
    void onComplete();

    /**
     * 分享失败
     * @param e 失败原因
     */
    void onError(Exception e);

    /**
     * 取消分享
     */
    void onCancel();
}
