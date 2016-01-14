package com.ibest.thirdparty.share.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.ibest.thirdparty.R;


/**
 * Created by vicky on 15/10/16.
 */
public class ShareResultManager {
    private ShareListener mShareListener;
    private Activity mContext;

    public ShareResultManager(Activity activity, ShareListener shareListener)
    {
        this.mContext = activity;
        this.mShareListener = shareListener;
    }

    public void onComplete()
    {
        Toast.makeText(this.mContext, this.mContext.getResources().getString(R.string.share_success), Toast.LENGTH_LONG).show();
        if (this.mShareListener != null)
        {
            this.mShareListener.onComplete();
        }
    }

    public void onError(Exception e)
    {
        Toast.makeText(this.mContext, this.mContext.getResources().getString(R.string.share_fail), Toast.LENGTH_LONG).show();
        if (this.mShareListener != null)
        {
            this.mShareListener.onError(e);
        }
    }

    public void onCancel()
    {
        if (this.mShareListener != null)
        {
            this.mShareListener.onCancel();
        }
    }
}
