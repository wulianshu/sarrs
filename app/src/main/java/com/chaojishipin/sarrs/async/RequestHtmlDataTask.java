package com.chaojishipin.sarrs.async;

import android.content.Context;

import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.http.parser.HtmlParser;
import com.letv.http.bean.LetvDataHull;

public class RequestHtmlDataTask extends MoviesHttpAsyncTask <HtmlDataBean>{

    private String mUrl;
    
    private String mReferer;
    
    private String mUserAgent;
    
    private RequestResultListener <HtmlDataBean>mListener;

    public RequestHtmlDataTask(Context context) {
        super(context);
    }
    
    public String getmUserAgent() {
        return mUserAgent;
    }

    public void setmUserAgent(String mUserAgent) {
        this.mUserAgent = mUserAgent;
    }


    @Override
    public boolean onPreExecute() {
        if (null != mListener) {
            mListener.onPreRequest();
        }
        return super.onPreExecute();
    }

    @Override
    public LetvDataHull<HtmlDataBean> doInBackground() {
        return MoviesHttpApi.requestHtmlData(new HtmlParser(), mUrl, mReferer, mUserAgent);
    }

    @Override
    public void onPostExecute(int updateId, HtmlDataBean result) {
        if (null != mListener) {
            mListener.onRequestSuccess(updateId, result);
        }
    }
   /* @Override
    public void onPostExecute(int updateId, String result) {
        if (null != mListener) {
            mListener.onRequestFailed();
        }
    }*/
    @Override
    public void netNull() {
        super.netNull();
        if (null != mListener) {
            mListener.onRequestFailed();
        }
    }

    @Override
    public void netErr(int updateId, String errMsg) {
        super.netErr(updateId, errMsg);
        if (null != mListener) {
            mListener.onRequestFailed();
        }
    }

    @Override
    public void dataNull(int updateId, String errMsg) {
        super.dataNull(updateId, errMsg);
        if (null != mListener) {
            mListener.onRequestFailed();
        }
    }
    
    public RequestResultListener<HtmlDataBean> getmListener() {
        return mListener;
    }

    public void setmListener(RequestResultListener<HtmlDataBean> mListener) {
        this.mListener = mListener;
    }
    
    public String getmReferer() {
        return mReferer;
    }

    public void setmReferer(String mReferer) {
        this.mReferer = mReferer;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
