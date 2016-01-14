package com.chaojishipin.sarrs.thirdparty;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.feedback.DataHttpApi;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

public class QQLoginHelper implements LoginHelper {
    private LoginListener mListener;
    private Activity mActivity;
    private static Tencent mTencent;
    private UserInfo userInfo;
    private BaseUserInfo user;
//    private IUiListener iUiListener;
//    private IUiListener iUiListener2;

    private IUiListener listener;

    public QQLoginHelper(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void excuteLogin(LoginListener listener) {

        mTencent = Tencent.createInstance(ShareConstants.QQ_APP_ID, mActivity.getApplicationContext());
        mListener = listener;
        if (!mTencent.isSessionValid()) {
            // 此接口仅支持移动端应用调用 get_simple_userinfo
            mTencent.login(mActivity, "get_simple_userinfo", loginListener);
        }
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
            mTencent.logout(mActivity);
            mTencent.releaseResource();
        }
    };

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            if (mListener != null) {
                mListener.onLoginFailed();
            }
            mTencent.releaseResource();
        }

        @Override
        public void onCancel() {
            if (mListener != null) {
                mListener.onLoginCancel();
            }
            mTencent.releaseResource();
        }
    }

    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            user = new BaseUserInfo();
            user.setOpenId(mTencent.getQQToken().getOpenId());
            listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    if (mListener != null) {
                        mListener.onLoginFailed();
                    }
                    mTencent.releaseResource();
                }

                @Override
                public void onComplete(final Object response) {
                    if (mListener == null) {
                        return;
                    }
                    ACache.get(mActivity).remove(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION);
                    JSONObject data = (JSONObject) response;
                    user.setName(data.optString("nickname"));
                    user.setGender(setUserGender(data.optString("gender")));
                    user.setType(LoginManager.TYPE_QQ);
                    user.setAvatar(data.optString("figureurl_qq_2"));
//                    UserLoginState.getInstance().setLogin(true);
                    UserLoginState.getInstance().setUserInfo(user);
                    mListener.onLoginComplete(user);
                    LoginUtils.setLastLoginType(LoginManager.TYPE_QQ);
                }

                @Override
                public void onCancel() {
                    if (mListener != null) {
                        mListener.onLoginCancel();
                    }
                    mTencent.releaseResource();
                }
            };
            userInfo = new UserInfo(mActivity, mTencent.getQQToken());
            userInfo.getUserInfo(listener);
        }
    }

    // 设置拉取的用户性别信息
    private int setUserGender(String gender) {
        if (!TextUtils.isEmpty(gender) && "男".equals(gender))
            return 0;
        if (!TextUtils.isEmpty(gender) && "女".equals(gender))
            return 1;
        return 2;
    }

    @Override
    public void fetchTimeLines(TimeLineListener listener, int pageNum, int requestNum) {
        listener.onTimeLineSuccess(new ArrayList<WeiboInfo>());
    }

    @Override
    public void logout() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mTencent.onActivityResultData(requestCode, resultCode, data,
//                loginListener);
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == Constants.ACTIVITY_OK) {
                mTencent.handleResultData(data, loginListener);
            }
        }
    }

    @Override
    public void removeCallbacks() {
        mListener = null;
    }
}