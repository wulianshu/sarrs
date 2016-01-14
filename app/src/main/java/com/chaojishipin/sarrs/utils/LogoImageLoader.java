package com.chaojishipin.sarrs.utils;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by xulinlin on 2015/6/24.
 */
public class LogoImageLoader extends ImageLoader {
    private volatile static LogoImageLoader instance;

    /** Returns singleton class instance */
    public static LogoImageLoader getInstance() {
        if (instance == null) {
            synchronized (LogoImageLoader.class) {
                if (instance == null) {
                    instance = new LogoImageLoader();
                }
            }
        }
        return instance;
    }
}
