package com.ibest.thirdparty.share.presenter;

import android.content.Intent;

import com.ibest.thirdparty.share.model.ShareData;

/**
 * Created by vicky on 15/10/22.
 */
public interface ShareHandle {
    public void share(ShareData shareData);
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
