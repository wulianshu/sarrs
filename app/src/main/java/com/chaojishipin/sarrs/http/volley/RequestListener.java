package com.chaojishipin.sarrs.http.volley;

import com.letv.http.bean.LetvBaseBean;

public interface RequestListener<T extends LetvBaseBean> {
    public static final int ERROR_NET_ERROR = 0;
    public static final int ERROR_DATA_ERROR = 1;
    public static final int ERROR_SERVER_ERROR = 2;
    public static final int ERROR_UNKNOWN = 2999;

    /**
     * 数据回调
     * @param result
     * @param isCachedData
     */
    public void onResponse(T result, boolean isCachedData);
    /**
     * 网络异常和服务端错误，回调
     * */
    public void netErr(int errorCode);

    /**
     * 数据错误，回调
     * */
    public void dataErr(int errorCode);

}
