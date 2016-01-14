package com.ibest.thirdparty.share.presenter;

import com.tencent.mm.sdk.openapi.BaseResp;

/**
 * Created by vicky on 15/10/20.
 */
public interface WXListener {
    void onWXCancel(BaseResp resp);

    void onWXComplete(BaseResp resp);

    void onWXFailed(BaseResp resp);
}
