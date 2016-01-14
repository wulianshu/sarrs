package com.chaojishipin.sarrs.thirdparty;


import java.util.List;

public interface TimeLineListener {
    public static final int NET_ERROR = 0;
    public static final int DATA_INVALID = 1;
    public static final int DATA_EMPTY = 2;
    public static final int AUTH_FAILED = 3;
    public static final int REQUEST_FAILED = 4;
    public void onTimeLineSuccess(List<WeiboInfo> data);
    public void onTimeLineFailed(int errCode, String errMsg);
}
