package com.chaojishipin.sarrs.exception;

import com.android.volley.ParseError;

/**
 *封装解析异常的各种状态码
 *@author daipei
 */
public class SarrsParseError extends ParseError {

    private int mErrorCode;

    public SarrsParseError(int errorCode) {
        this.mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
