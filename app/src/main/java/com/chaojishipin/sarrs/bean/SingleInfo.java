package com.chaojishipin.sarrs.bean;

import java.util.HashMap;

import com.letv.component.player.LetvVideoViewBuilder.Type;
import com.letv.http.bean.LetvBaseBean;

/**
 * 单机信息
 * @author zhangshuo
 *
 */
public class SingleInfo implements LetvBaseBean {

    /**
     * zhangshuo 2014年12月9日 下午4:35:46
     */
    private static final long serialVersionUID = 1L;
    
    private HashMap<String, String> m3u8apiMap;
    
    private HashMap<String, String> mp4apiMap;
    
    private HashMap<String, String> m3u8PlayMap;
    
    private HashMap<String, String> mp4PlayMap;
    
    private HashMap<String, String> m3u8paramMap;
    
    private HashMap<String, String> mp4paramMap;

    private String snifferUrl;

    public HashMap<String, String> getApiMap(Type type) {
        switch (type) {
            case MOBILE_H264_M3U8:
                return getM3u8apiMap();
            case MOBILE_H264_MP4:
                return getMp4apiMap();
            default:
                return null;
        }
    }

    public HashMap<String, String> getM3u8apiMap() {
        return m3u8apiMap;
    }

    public void setM3u8apiMap(HashMap<String, String> m3u8apiMap) {
        this.m3u8apiMap = m3u8apiMap;
    }

    public HashMap<String, String> getMp4apiMap() {
        return mp4apiMap;
    }

    public void setMp4apiMap(HashMap<String, String> mp4apiMap) {
        this.mp4apiMap = mp4apiMap;
    }
    
    public HashMap<String, String> getPlayUrlMap(Type type) {
        switch (type) {
            case MOBILE_H264_M3U8:
                return getM3u8PlayMap();
            case MOBILE_H264_MP4:
                return getMp4PlayMap();
            default:
                return null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
    public HashMap<String, String> getM3u8PlayMap() {
        return m3u8PlayMap;
    }

    public void setM3u8PlayMap(HashMap<String, String> m3u8PlayMap) {
        this.m3u8PlayMap = m3u8PlayMap;
    }

    public HashMap<String, String> getMp4PlayMap() {
        return mp4PlayMap;
    }

    public void setMp4PlayMap(HashMap<String, String> mp4PlayMap) {
        this.mp4PlayMap = mp4PlayMap;
    }

    public HashMap<String, String> getRuleMap(Type type) {
        switch (type) {
            case MOBILE_H264_M3U8:
                return getM3u8paramMap();
            case MOBILE_H264_MP4:
                return getMp4paramMap();
            default:
                return null;
        }
    }
    
    public HashMap<String, String> getM3u8paramMap() {
        return m3u8paramMap;
    }

    public void setM3u8paramMap(HashMap<String, String> m3u8paramMap) {
        this.m3u8paramMap = m3u8paramMap;
    }

    public HashMap<String, String> getMp4paramMap() {
        return mp4paramMap;
    }

    public void setMp4paramMap(HashMap<String, String> mp4paramMap) {
        this.mp4paramMap = mp4paramMap;
    }

    public String getSnifferUrl() {
        return snifferUrl;
    }

    public void setSnifferUrl(String snifferUrl) {
        this.snifferUrl = snifferUrl;
    }

}
