package com.ibest.thirdparty.share.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.ibest.thirdparty.share.model.Constants;
import com.ibest.thirdparty.share.model.ShareData;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vicky on 15/10/15.
 */
public class QQShareHandle implements ShareHandle {

    private int mExtarFlag = 0x00;
    private Tencent mTencent;
    private Activity mContext;
    private ShareListener mShareListener;

    public QQShareHandle(Activity activity, ShareListener shareListener) {
        mContext = activity;
        mShareListener = shareListener;
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, mContext);
    }

    public void share(ShareData shareData) {
        if (shareData.getSharePlatform() == ShareData.SHARE_PLATFORM_QQ) {
            shareToQQ(shareData);
        } else {
            shareToQQZone(shareData);
        }
    }

    private void shareToQQ(ShareData shareData) {
        final Bundle params = new Bundle();

        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getShareTitle(shareData.getSharePlatform()));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getShareTargetUrl());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getShareText(shareData.getSharePlatform()));
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareData.getShareImageUrl());
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        doShareToQQ(params);
    }

    private void shareToQQZone(ShareData shareData) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareData.getShareTitle(shareData.getSharePlatform()));
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareData.getShareText(shareData.getSharePlatform()));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareData.getShareTargetUrl());

        // 支持传多个imageUrl
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(shareData.getShareImageUrl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        doShareToQzone(params);
    }

    /**
     * 用异步方式启动分享
     *
     * @param params
     */
    private void doShareToQQ(final Bundle params) {
        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQQ(mContext, params, qqShareListener);
            }
        });
    }

    /**
     * 用异步方式启动分享
     *
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        // QZone分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQzone(mContext, params, qqShareListener);
                }
            }
        });
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            releaseQQResource();
            new ShareResultManager(mContext, mShareListener).onCancel();
        }

        @Override
        public void onComplete(Object response) {
            releaseQQResource();
            // TODO Auto-generated method stub
            new ShareResultManager(mContext, mShareListener).onComplete();
        }

        @Override
        public void onError(UiError e) {
            releaseQQResource();
            // TODO Auto-generated method stub
            new ShareResultManager(mContext, mShareListener).onError(new Exception(e.errorMessage));
        }
    };

    private void releaseQQResource() {
        mTencent.releaseResource();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE) {
            if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, qqShareListener);
            }
        }
    }
}
