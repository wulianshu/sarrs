package com.ibest.thirdparty.share.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ibest.thirdparty.sdk.sina.AccessTokenKeeper;
import com.ibest.thirdparty.share.model.Constants;
import com.ibest.thirdparty.share.model.ShareData;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;

/**
 * Created by vicky on 15/10/15.
 */
public class SinaShareHandle implements ShareHandle {

    private IWeiboShareAPI  mWeiboShareAPI = null;
    private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    private Activity mContext;
    private ShareListener mShareListener;
    private ShareData mShareData;

    public SinaShareHandle(Activity activity, ShareListener shareListener)
    {
        mContext = activity;
        mShareListener = shareListener;
        // 创建微博分享接口实例
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this.mContext, Constants.SINA_APP_KEY);
//
//        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
//        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
//        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
//        mWeiboShareAPI.registerApp();


    }

    public void share(ShareData shareData)
    {
        try {
            mShareData = shareData;
            mAuthInfo = new AuthInfo(mContext, Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SCOPE);
            mSsoHandler = new SsoHandler(mContext, mAuthInfo);
            // 获取当前已保存过的 Token
            mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
            if (mAccessToken == null || !mAccessToken.isSessionValid())
            {
                mSsoHandler.authorize(new AuthListener());
            }else {
                shareWeibo();
            }
        }catch (Exception e)
        {
            Log.e("exception", e.getMessage());
        }


    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);

            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                shareWeibo();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                String code = values.getString("code");
                new ShareResultManager(mContext, mShareListener).onError(new Exception("sina share fail"));
            }
        }

        @Override
        public void onCancel() {
            new ShareResultManager(mContext, mShareListener).onCancel();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            new ShareResultManager(mContext, mShareListener).onError(new Exception("sina share fail"));
        }
    }

    private void shareWeibo()
    {
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(mContext, Constants.SINA_APP_KEY, mAccessToken);
        //图文
        if (mShareData.getShareBitmap() != null)
        {
            mStatusesAPI.upload(mShareData.getShareText(ShareData.SHARE_PLATFORM_SINA), mShareData.getShareBitmap(), null, null, mListener);
        }else {  //只分享文字
            mStatusesAPI.update(mShareData.getShareText(ShareData.SHARE_PLATFORM_SINA), null, null, mListener);
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            new ShareResultManager(mContext, mShareListener).onComplete();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            new ShareResultManager(mContext, mShareListener).onError(e);
        }
    };

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage(ShareData shareData) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (shareData.getShareText(shareData.getSharePlatform()) != null && shareData.getShareText(shareData.getSharePlatform()).length() > 0) {
            weiboMessage.textObject = getTextObj(shareData);
        }

        if (shareData.getShareBitmap() != null) {
            weiboMessage.imageObject = getImageObj(shareData);
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (shareData.getShareWebPage() != null) {
            weiboMessage.mediaObject = getWebpageObj(shareData);
        }

        if (shareData.getShareVideoUrl() != null) {
            weiboMessage.mediaObject = getVideoObj(shareData);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        AuthInfo authInfo = new AuthInfo(this.mContext, Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this.mContext);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this.mContext, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException( WeiboException arg0 ) {
                new ShareResultManager(mContext, mShareListener).onError(arg0);
            }

            @Override
            public void onComplete( Bundle bundle ) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(mContext, newToken);
//                Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
                new ShareResultManager(mContext, mShareListener).onComplete();
            }

            @Override
            public void onCancel() {
                new ShareResultManager(mContext, mShareListener).onCancel();
            }
        });

    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(ShareData shareData) {
        TextObject textObject = new TextObject();
        textObject.text = shareData.getShareText(shareData.getSharePlatform());
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(ShareData shareData) {
        ImageObject imageObject = new ImageObject();
        //        设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = shareData.getShareBitmap();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(ShareData shareData) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareData.getShareTitle(shareData.getSharePlatform());
        mediaObject.description = shareData.getShareText(shareData.getSharePlatform());

        Bitmap  bitmap = shareData.getShareBitmap();
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = shareData.getShareWebPage();
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj(ShareData shareData) {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareData.getShareTitle(shareData.getSharePlatform());
        videoObject.description = shareData.getShareText(shareData.getSharePlatform());
        Bitmap  bitmap = shareData.getShareBitmap();
        videoObject.setThumbImage(bitmap);
        videoObject.actionUrl = shareData.getShareVideoUrl();
        videoObject.dataUrl = shareData.getShareVideoUrl();
        videoObject.dataHdUrl = shareData.getShareVideoUrl();
        videoObject.duration = 10;
        videoObject.defaultText = shareData.getShareText(shareData.getSharePlatform());
        return videoObject;
    }

}
