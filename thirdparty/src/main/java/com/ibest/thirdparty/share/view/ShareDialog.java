package com.ibest.thirdparty.share.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibest.thirdparty.R;
import com.ibest.thirdparty.share.model.ShareData;
import com.ibest.thirdparty.share.presenter.ShareListener;
import com.ibest.thirdparty.share.presenter.ShareManager;


/**
 * Created by vicky on 15/10/15.
 */
public class ShareDialog extends BottomDialog implements View.OnClickListener {
    View shareView;
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
     * 竖屏显示
     * @param activity 上下文
     * @param shareData 分享数据
     * @param listener  分享反馈结果,可以为null
     */
    public ShareDialog(Activity activity, ShareData shareData, ShareListener listener)
    {
        this.context = activity;
        this.shareData = shareData;
        this.shareListener = listener;
        shareView = LayoutInflater.from(activity).inflate(R.layout.share_popup_menu, null);
        super.init(activity, shareView);
    }

    /**
     * 根据gravity显示，目前只支持竖屏和屏幕右侧显示
     * @param activity 上下文
     * @param shareData 分享数据
     * @param listener 分享反馈结果
     * @param gravity 布局位置
     */
    public ShareDialog(Activity activity, ShareData shareData, ShareListener listener, int gravity)
    {
        this.context = activity;
        this.shareData = shareData;
        this.shareListener = listener;
        if (gravity == GRAVITY_RIGHT) {
            shareView = LayoutInflater.from(activity).inflate(R.layout.share_popup_right_menu, null);
            super.init(activity, shareView, gravity, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else {
            shareView = LayoutInflater.from(activity).inflate(R.layout.share_popup_menu, null);
            super.init(activity, shareView);
        }
    }

    @Override
    protected void setListener(View view) {
        view.findViewById(R.id.share_to_wx).setOnClickListener(this);
        view.findViewById(R.id.share_to_wx_friends).setOnClickListener(this);
        view.findViewById(R.id.share_to_sina).setOnClickListener(this);
        view.findViewById(R.id.share_to_qq).setOnClickListener(this);
        view.findViewById(R.id.share_to_qzone).setOnClickListener(this);
        view.findViewById(R.id.share_to_sms).setOnClickListener(this);
        view.findViewById(R.id.share_to_linking).setOnClickListener(this);
        if (view.findViewById(R.id.share_close) != null)
        {
            view.findViewById(R.id.share_close).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.share_to_wx) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_WX_FRIEND);

        } else if (i == R.id.share_to_wx_friends) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_WX_FRIENDS);

        } else if (i == R.id.share_to_sina) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_SINA);

        } else if (i == R.id.share_to_qq) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_QQ);


        } else if (i == R.id.share_to_qzone) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_QQ_ZONE);


        } else if (i == R.id.share_to_sms) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_SMS);

        } else if (i == R.id.share_to_linking) {
            shareData.setSharePlatform(ShareData.SHARE_PLATFORM_COPY_LINKING);

        } else if (i == R.id.share_close) {
            dismiss();
            return;
        }
        ShareManager shareManager = new ShareManager(this.context, shareData, shareListener);
        shareManager.share();
        dismiss();
    }
}
