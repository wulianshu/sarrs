package com.ibest.thirdparty.share.model;

import android.graphics.Bitmap;

import java.util.Map;

/**
 * Created by vicky on 15/10/15.
 */
public class ShareData {
    /**
     * 分享平台类型
     */
    public final static int SHARE_PLATFORM_WX_FRIEND = 0;
    public final static int SHARE_PLATFORM_WX_FRIENDS = 1;
    public final static int SHARE_PLATFORM_SINA = 2;
    public final static int SHARE_PLATFORM_QQ = 3;
    public final static int SHARE_PLATFORM_QQ_ZONE = 4;
    public final static int SHARE_PLATFORM_SMS = 5;
    public final static int SHARE_PLATFORM_COPY_LINKING = 6;

    private int sharePlatform;

    /**
     * 分享内容类型
     */
    public final static int SHARE_CONTENT_TYPE_TEXT = 0;
    public final static int SHARE_CONTENT_TYPE_IMAGE = 1;
    public final static int SHARE_CONTENT_TYPE_WEBPAGE = 2;
    public final static int SHARE_CONTENT_TYPE_VIDEO = 3;

    private int shareContentType;

    /**
     * 分享文案内容, 根据平台类型获取，每个平台分享文案可能不一样，故使用数组
     */
    private Map<String, String> shareText;

    /**
     * 分享图片，若不提供该数据则使用shareImageUrl提供的地址
     * 微信分享SHARE_CONTENT_TYPE_WEBPAGE, SHARE_CONTENT_TYPE_MUSIC, SHARE_CONTENT_TYPE_VIDEO必须提供
     */
    private Bitmap shareBitmap;

    /**
     * 分享图片地址
     */
    private String shareImageUrl;

    /**
     * 分享webpage url
     */
    private String shareWebPage;

    /**
     * 分享音乐url
     */
    private String shareMusicUrl;

    /**
     * 分享视频url
     */
    private String shareVideoUrl;

    /**
     * 分享title
     */
    private Map<String, String> shareTitle;

    /**
     * 分享targetUrl，链接url
     */
    private String shareTargetUrl;

    public int getSharePlatform() {
        return sharePlatform;
    }

    public void setSharePlatform(int sharePlatform) {
        this.sharePlatform = sharePlatform;
    }

    public int getShareContentType() {
        return shareContentType;
    }

    public void setShareContentType(int shareContentType) {
        this.shareContentType = shareContentType;
    }

    public String getShareText(int sharePlatform) {
        String key = "" + sharePlatform;
        String value = shareText.get(key);
        return value == null ? "" : value;
    }

    public void setShareText(Map<String, String>shareText) {
        this.shareText = shareText;
    }

    public String getShareTitle(int sharePlatform) {
        String key = "" + sharePlatform;
        String value = shareTitle.get(key);
        return value == null ? "" : value;
    }

    public void setShareTitle(Map<String, String>shareTitle) {
        this.shareTitle = shareTitle;
    }

    public Bitmap getShareBitmap() {
        return shareBitmap;
    }

    public void setShareBitmap(Bitmap shareBitmap) {
        this.shareBitmap = shareBitmap;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public String getShareWebPage() {
        return shareWebPage;
    }

    public void setShareWebPage(String shareWebPage) {
        this.shareWebPage = shareWebPage;
    }

    public String getShareMusicUrl() {
        return shareMusicUrl;
    }

    public void setShareMusicUrl(String shareMusicUrl) {
        this.shareMusicUrl = shareMusicUrl;
    }

    public String getShareVideoUrl() {
        return shareVideoUrl;
    }

    public void setShareVideoUrl(String shareVideoUrl) {
        this.shareVideoUrl = shareVideoUrl;
    }

    public String getShareTargetUrl() {
        return shareTargetUrl;
    }

    public void setShareTargetUrl(String shareTargetUrl) {
        this.shareTargetUrl = shareTargetUrl;
    }
}
