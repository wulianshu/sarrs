package com.ibest.thirdparty.share.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.ibest.thirdparty.R;
import com.ibest.thirdparty.share.model.ShareData;


/**
 * Created by vicky on 15/10/15.
 */
public class ShareManager {
    Activity context;
    /**
     * 分享结果，如需处理需要定义，可以为null
     */
    ShareListener shareListener;
    /**
     * 分享数据
     */
    ShareData shareData;

    /**
     * 新浪分享授权后，activity 需要重写
     * protected void onActivityResult(int requestCode, int resultCode, Intent data)
     * 该方法需要调用SinaShareHandle授权回调方法才能完成授权
     */
    private static ShareHandle shareHandle;

    public ShareManager(Activity activity, ShareData shareData, ShareListener listener)
    {
        this.context = activity;
        this.shareData = shareData;
        this.shareListener = listener;
    }

    public void share()
    {
        shareHandle = null;
        int platform = shareData.getSharePlatform();
        switch (platform)
        {
            case ShareData.SHARE_PLATFORM_WX_FRIENDS:
            case ShareData.SHARE_PLATFORM_WX_FRIEND:
            {
                shareToWX();
            }
            break;
            case ShareData.SHARE_PLATFORM_SINA:
            {
                shareToSina();
            }
            break;
            case ShareData.SHARE_PLATFORM_QQ:
            case ShareData.SHARE_PLATFORM_QQ_ZONE:
            {
                shareToQQ();
            }
            break;
            case ShareData.SHARE_PLATFORM_SMS:
            {
                shareToSMS();
            }
            break;
            case ShareData.SHARE_PLATFORM_COPY_LINKING:
            {
                shareToLinking();
            }
            break;
            default:
            {
            }
            break;
        }
    }

    /**
     * 分享到朋友圈/微信好友
     */
    void shareToWX()
    {
        WXShareHandle handle = new WXShareHandle(context, shareListener);
        handle.share(shareData);
    }

    /**
     * 分享到新浪微博
     */
    void shareToSina()
    {
        SinaShareHandle handle = new SinaShareHandle(context, shareListener);
        handle.share(shareData);
        shareHandle = handle;
    }

    /**
     * 分享到qq好友/qq空间
     */
    void shareToQQ()
    {
        QQShareHandle handle = new QQShareHandle(context, shareListener);
        handle.share(shareData);
        shareHandle = handle;
    }

    /**
     * 分享到短信
     */
    void shareToSMS()
    {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", shareData.getShareText(ShareData.SHARE_PLATFORM_SMS));

        context.startActivity(intent);
    }

    /**
     * 复制链接
     */
    void shareToLinking()
    {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, shareData.getShareTargetUrl()));
        Toast.makeText(context, context.getResources().getText(R.string.copy_linking_success), Toast.LENGTH_LONG);
        if (shareListener != null)
            shareListener.onComplete();
    }

    /**
     * 授权回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void authorCallback(int requestCode, int resultCode, Intent data)
    {
        if (shareHandle != null)
        {
            shareHandle.onActivityResult(requestCode, resultCode, data);
        }
    }
}
