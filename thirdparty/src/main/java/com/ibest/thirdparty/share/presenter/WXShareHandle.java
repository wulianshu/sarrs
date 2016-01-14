package com.ibest.thirdparty.share.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.ibest.thirdparty.share.model.Constants;
import com.ibest.thirdparty.share.model.ShareData;
//import com.ibest.thirdparty.share.view.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by vicky on 15/10/15.
 */
public class WXShareHandle implements WXListener, ShareHandle {

    public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final int THUMB_SIZE = 150;

    private IWXAPI api;

    private Activity mContext;
    private ShareListener mShareListener;

    public WXShareHandle(Activity activity, ShareListener shareListener)
    {
        mContext = activity;
        mShareListener = shareListener;
        api = WXAPIFactory.createWXAPI(mContext, Constants.WX_APP_ID, true);
        api.registerApp(Constants.WX_APP_ID);
        WXListenerManager.setListener(this);
    }

    public void share(ShareData shareData)
    {
        if (mContext == null || shareData == null) {
            return;
        }
        if(!api.isWXAppInstalled()){
            Toast.makeText(mContext, "抱歉，您未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        int wxSdkVersion = api.getWXAppSupportAPI();
        if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
            Toast.makeText(mContext, "微信升级咯，亲要更新了啦~", Toast.LENGTH_SHORT).show();
            return;
        }

        int contentType = shareData.getShareContentType();
        switch (contentType)
        {
            case ShareData.SHARE_CONTENT_TYPE_TEXT:
            {
                shareText(shareData);
            }
            break;
            case ShareData.SHARE_CONTENT_TYPE_IMAGE:
            {
                shareImage(shareData);
            }
            break;
            case ShareData.SHARE_CONTENT_TYPE_WEBPAGE:
            {
                shareWebPage(shareData);
//                shareText(shareData);
            }
            break;
            case ShareData.SHARE_CONTENT_TYPE_VIDEO:
            {
                shareVideo(shareData);
            }
            break;
        }
    }

    private void shareText(ShareData shareData)
    {
        try {
            // 初始化一个WXTextObject对象
            WXTextObject textObj = new WXTextObject();
            textObj.text = shareData.getShareText(shareData.getSharePlatform());

            // 用WXTextObject对象初始化一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            // 发送文本类型的消息时，title字段不起作用
            // msg.title = "Will be ignored";
            msg.description = shareData.getShareText(shareData.getSharePlatform());

            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
            req.message = msg;
            req.scene = getShareScene(shareData.getSharePlatform());

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void shareImage(ShareData shareData)
    {
        try {
            if (shareData.getShareImageUrl() != null) {
                //image url
                WXImageObject imgObj = new WXImageObject();
                imgObj.imageUrl = shareData.getShareImageUrl();

                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;

                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(new URL(imgObj.imageUrl).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = getShareScene(shareData.getSharePlatform());
                api.sendReq(req);
            } else {
                Bitmap bmp = shareData.getShareBitmap();
                WXImageObject imgObj = new WXImageObject(bmp);

                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;

                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = getShareScene(shareData.getSharePlatform());
                api.sendReq(req);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void shareWebPage(ShareData shareData)
    {
        try {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = shareData.getShareTargetUrl();
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = shareData.getShareTitle(shareData.getSharePlatform());
            msg.description = shareData.getShareText(shareData.getSharePlatform());
            Bitmap thumb = shareData.getShareBitmap();
            msg.thumbData = bmpToByteArray(thumb);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = getShareScene(shareData.getSharePlatform());
            api.sendReq(req);
        }catch (Exception e)
        {
            Log.e("exception", e.getMessage());
            e.printStackTrace();
        }

    }

    private void shareVideo(ShareData shareData)
    {
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = shareData.getShareVideoUrl();

        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = shareData.getShareTitle(shareData.getSharePlatform());
        msg.description = shareData.getShareText(shareData.getSharePlatform());
        Bitmap thumb = shareData.getShareBitmap();
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = getShareScene(shareData.getSharePlatform());
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 判断分享到朋友圈or微信好友
     * @param sharePlatform
     * @return
     */
    private int getShareScene(int sharePlatform)
    {
        return sharePlatform == ShareData.SHARE_PLATFORM_WX_FRIEND ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
    }

    private byte[] bmpToByteArray(final Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 微信函数public void onResp(BaseResp resp)返回信息
     */
    @Override
    public void onWXCancel(BaseResp resp) {
        new ShareResultManager(mContext, mShareListener).onCancel();
    }

    @Override
    public void onWXComplete(BaseResp resp) {
        new ShareResultManager(mContext, mShareListener).onComplete();
    }

    @Override
    public void onWXFailed(BaseResp resp) {
        new ShareResultManager(mContext, mShareListener).onError(new Exception("share to weixin fail"));
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
