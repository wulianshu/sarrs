package com.chaojishipin.sarrs.thirdparty;


public interface LoginHelper {
    public void excuteLogin(LoginListener listener);
    public void logout();
    public void fetchTimeLines(TimeLineListener listener,int pageNum, int requestNum);
    public void removeCallbacks();
}
