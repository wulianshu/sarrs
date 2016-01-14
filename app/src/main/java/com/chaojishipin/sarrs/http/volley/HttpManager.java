package com.chaojishipin.sarrs.http.volley;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author daipei
 * http管理类
 * 初始化volley
 */
public class HttpManager {

    private static HttpManager mInstance;
    public Context mContext;
    public RequestQueue mQueue;

    private HttpManager(Context context) {
        this.mContext = context.getApplicationContext();
        mQueue = Volley.newRequestQueue(this.mContext);
    }

    public static synchronized HttpManager getInstance() {
        return mInstance;
    }

    public static synchronized void init(Context context) {
        if (mInstance == null) {
            mInstance = new HttpManager(context);
        }
    }

    public void postToQueue(SarrsRequest<?> request) {
        mQueue.add(request);
    }

    public void postToQueue(SarrsRequest<?> request, String tag) {
        mQueue.add(request);
    }

    public void cancelByTag(Object tag) {
        mQueue.cancelAll(tag);
    }

}
