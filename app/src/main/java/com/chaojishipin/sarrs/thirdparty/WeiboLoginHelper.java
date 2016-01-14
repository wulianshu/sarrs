package com.chaojishipin.sarrs.thirdparty;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.chaojishipin.sarrs.http.parser.SinaWeiboParser;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

//import com.sina.weibo.sdk.auth.WeiboAuth;

public class WeiboLoginHelper implements WeiboAuthListener, LoginHelper {
    private SsoHandler mSsoHandler;
    private AuthInfo mAuth;
    private Activity mActivity;
    private UsersAPI mUsersAPI;
    private LoginListener mListener;
    private Oauth2AccessToken accessToken;
    private UserInfoListener mUserInfoListener = new UserInfoListener();

    public WeiboLoginHelper(Activity activity) {
        super();
        this.mActivity = activity;

//        mAuth = new WeiboAuth(mActivity, Constant.APP_KEY, Constant.REDIRECT_URL, Constant.SCOPE);
        mAuth = new AuthInfo(mActivity, Constant.APP_KEY, Constant.REDIRECT_URL, Constant.SCOPE);
        mSsoHandler = new SsoHandler(mActivity, mAuth);
    }

    public void excuteLogin(LoginListener listener) {
        this.mListener = listener;
        mSsoHandler.authorize(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void requestUserInfo(Oauth2AccessToken accessToken, LoginListener listener) {

        mListener = listener;
        requestUserInfo(accessToken, mUserInfoListener);
    }

    public void requestUserInfo(Oauth2AccessToken accessToken, RequestListener listener) {
        this.accessToken = accessToken;
//        mUsersAPI = new UsersAPI(accessToken);
        mUsersAPI = new UsersAPI(this.mActivity, Constant.APP_KEY, accessToken);
        long uid = Long.parseLong(accessToken.getUid());
        mUsersAPI.show(uid, listener);
    }

    @Override
    public void onCancel() {
        if (null != mListener) {
            mListener.onLoginCancel();
        }
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
        if (accessToken != null && accessToken.isSessionValid()) {
            AccessTokenKeeper.writeAccessToken(mActivity, accessToken);
            mUsersAPI = new UsersAPI(this.mActivity, Constant.APP_KEY, accessToken);
            requestUserInfo(accessToken, mUserInfoListener);
        }
    }

    @Override
    public void onWeiboException(WeiboException arg0) {
        if (null != mListener) {
            mListener.onLoginFailed();
        }
    }

    public class UserInfoListener implements RequestListener {

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null && mListener != null) {
                    ACache.get(mActivity).remove(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION);
                    BaseUserInfo userInfo = new BaseUserInfo();
                    userInfo.setOpenId(user.id);
                    userInfo.setName(user.name);
                    userInfo.setGender(setUserGender(user.gender));
                    userInfo.setType(LoginManager.TYPE_SINA_WEIBO);
                    userInfo.setAvatar(user.avatar_hd);
//                    UserLoginState.getInstance().setLogin(true);
                    UserLoginState.getInstance().setUserInfo(userInfo);
                    mListener.onLoginComplete(userInfo);
                    LoginUtils.setLastLoginType(LoginManager.TYPE_SINA_WEIBO);
                }
            } else {
                if (null != mListener) {
                    mListener.onLoginFailed();
                }
                UIs.showToast(response);
            }
        }

        // 设置拉取的用户性别信息
        private int setUserGender(String gender) {
            if (!TextUtils.isEmpty(gender) && "m".equals(gender))
                return 0;
            if (!TextUtils.isEmpty(gender) && "f".equals(gender))
                return 1;
            return 2;
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            if (null != mListener) {
                mListener.onLoginFailed();
            }
            if (null != info)
                AccessTokenKeeper.clear(mActivity);
        }

    }

    @Override
    public void logout() {
        AccessTokenKeeper.clear(mActivity);
    }

    @Override
    public void fetchTimeLines(final TimeLineListener listener, int pageNum, int requestNum) {
        WeiboParameters params = new WeiboParameters(Constant.APP_KEY);
        params.put("access_token", UserLoginState.getInstance().getToken());
        params.put("count", requestNum);
        params.put("page", pageNum);
        params.put("feature", 3);
//    public void requestAsync(String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        new AsyncWeiboRunner(mActivity).requestAsync("https://api.weibo.com/2/statuses/home_timeline.json",
                params, "GET", new RequestListener() {

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(mActivity);
                        if (null == token || !token.isSessionValid()) {
                            listener.onTimeLineFailed(TimeLineListener.AUTH_FAILED, e.getMessage());
                        } else {
                            listener.onTimeLineFailed(TimeLineListener.REQUEST_FAILED, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete(String json) {
                        try {
                            List<WeiboInfo> infoList = SinaWeiboParser.parse(json);
                            listener.onTimeLineSuccess(infoList);
                        } catch (Exception e) {
                            listener.onTimeLineFailed(TimeLineListener.DATA_INVALID, e.getMessage());
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void removeCallbacks() {
        mListener = null;
    }
}
