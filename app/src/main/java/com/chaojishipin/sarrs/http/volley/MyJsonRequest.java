package com.chaojishipin.sarrs.http.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.chaojishipin.sarrs.listener.BaseSarrsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by wulianshu on 2015/10/14.
 */
public class MyJsonRequest extends JsonRequest<String> {

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public MyJsonRequest(int method, String url, String jsonRequest,
                             Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest, listener,
                errorListener);
    }
    public MyJsonRequest(int method, String url, String jsonRequest,
                         BaseSarrsResponseListener lis) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest, lis,
                lis);
    }
    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     */
    public MyJsonRequest(String url, String json, Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        this(json == null ? Method.GET : Method.POST, url, json,
                listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
