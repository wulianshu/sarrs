package com.chaojishipin.sarrs.interfaces;

/**
 * Created by zhangshuo on 2015/6/1.
 */
public interface INetWorkObServe {

    /**
     * @param netName 网络名称
     * @param netType 网络类型
     * @param isHasNetWork  当前是否有网络
     */
    public abstract void observeNetWork(String netName, int netType, boolean isHasNetWork);

}
