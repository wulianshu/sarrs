package com.chaojishipin.sarrs.listener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by wulianshu on 2015/10/13.
 */
public class UpoloadHistoryRecordListener  implements Response.Listener<String> ,Response.ErrorListener{
    @Override
    public void onErrorResponse(VolleyError error) {
    }

    @Override
    public void onResponse(String response) {
    }
}
