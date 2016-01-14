package com.ibest.thirdparty.share.presenter;

/**
 * Created by vicky on 15/10/23.
 */
public class WXListenerManager {
    private static WXListener mListener;
    public static WXListener getListener() {
        return mListener;
    }

    public static void setListener(WXListener mListener) {
        WXListenerManager.mListener = mListener;
    }
}
