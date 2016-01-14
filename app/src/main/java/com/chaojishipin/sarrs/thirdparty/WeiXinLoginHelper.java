package com.chaojishipin.sarrs.thirdparty;


import android.app.Activity;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.wxapi.WXEntryActivity;
import com.ibest.thirdparty.share.presenter.WXListener;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.SendAuth;

import java.util.ArrayList;

public class WeiXinLoginHelper implements LoginHelper, WXListener {
    private LoginListener mListener;
    private Activity mActivity;

    @Override
    public void excuteLogin(LoginListener listener) {
        mListener = listener;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "sarrs_login";
        WXEntryActivity.setListener(this);
        ChaoJiShiPinApplication.getInstatnce().getmApi().sendReq(req);
    }

    public WeiXinLoginHelper(Activity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    @Override
    public void logout() {
        LoginUtils.clearAllLoginInfo(mActivity);
    }

    @Override
    public void fetchTimeLines(TimeLineListener listener, int pageNum, int requestNum) {
        listener.onTimeLineSuccess(new ArrayList<WeiboInfo>());
    }

    @Override
    public void onWXCancel(BaseResp resp) {
        if (null != mListener) {
            mListener.onLoginCancel();
        }
    }

    @Override
    public void onWXComplete(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            String token = authResp.token;
            // 依据token去openId
            requestWXToken(token);
        }
    }


    @Override
    public void onWXFailed(BaseResp resp) {
        if (null != mListener) {
            mListener.onLoginFailed();
        }
    }


    @Override
    public void removeCallbacks() {
        mListener = null;
    }

    /**
     * 请求WX 微信 token
     */
    private void requestWXToken(String code) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_WXLOGIN_TAG);

        HttpApi.
                getWeixinAccessToken(code)
                .start(new RequestWXTokenListener(), ConstantUtils.REQUEST_WXLOGIN_TAG);


    }

    /**
     * 请求WX userInfo
     */
    private void requestWXUserInfo(String accessToken, String openId) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_WXLOGIN_TAG);
        HttpApi.
                getWeixinUserInfo(accessToken, openId)
                .start(new RequestUserInfoListener(), ConstantUtils.REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG);
    }

    /**
     * 请求token
     */
    class RequestWXTokenListener implements RequestListener<WeiXinToken> {
        //

        public RequestWXTokenListener() {


        }

        @Override
        public void onResponse(WeiXinToken result, boolean isCachedData) {

            requestWXUserInfo(result.access_token, result.openId);


        }

        @Override
        public void dataErr(int errorCode) {


        }

        @Override
        public void netErr(int errorCode) {

        }
    }

    /**
     * 请求openId
     */
    private class RequestUserInfoListener implements RequestListener<BaseUserInfo> {
        //
        public RequestUserInfoListener() {


        }

        @Override
        public void onResponse(BaseUserInfo result, boolean isCachedData) {
            if (null != mListener) {
                ACache.get(mActivity).remove(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION);
//              UserLoginState.getInstance().setLogin(true);
                UserLoginState.getInstance().setUserInfo(result);
                mListener.onLoginComplete(result);
                LoginUtils.setLastLoginType(LoginManager.TYPE_WEIXIN);
            }
        }

        @Override
        public void dataErr(int errorCode) {

            if (null != mListener) {
                mListener.onLoginFailed();
            }
        }

        @Override
        public void netErr(int errorCode) {
            if (null != mListener) {
                mListener.onLoginFailed();
            }
        }
    }


}





