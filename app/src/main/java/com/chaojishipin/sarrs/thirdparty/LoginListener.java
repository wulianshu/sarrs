package com.chaojishipin.sarrs.thirdparty;


public interface LoginListener {
    void onLoginSuccess();
    void onLoginComplete(BaseUserInfo user);
    void onLoginCancel();
    void onLoginFailed();
}
