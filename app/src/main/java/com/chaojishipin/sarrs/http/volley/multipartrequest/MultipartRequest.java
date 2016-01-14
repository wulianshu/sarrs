package com.chaojishipin.sarrs.http.volley.multipartrequest;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.chaojishipin.sarrs.http.parser.ResponseBaseParser;
import com.chaojishipin.sarrs.http.parser.UploadFileParser;
import com.chaojishipin.sarrs.http.volley.SarrsRequest;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15-9-16.
 */
public class MultipartRequest extends SarrsRequest {
//    private Response.ErrorListener errorListener = null;
    private MultipartRequestParams params = null;
    private HttpEntity httpEntity = null;

    public MultipartRequest(int method,
                            MultipartRequestParams params,
                            String url,
                            Response.ErrorListener errorListener,
                            @Nullable UploadFileParser parser) {
        super(method, url, errorListener, parser);
        this.params = params;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        // TODO Auto-generated method stub
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(params != null) {
            httpEntity = params.getEntity();
            try {
                httpEntity.writeTo(baos);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String str = new String(baos.toByteArray());
            Log.e("test", "bodyString is :" + str);
        }
        return baos.toByteArray();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // TODO Auto-generated method stub
        Map<String, String> headers = super.getHeaders();
        if (null == headers || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    public String getBodyContentType() {
        // TODO Auto-generated method stub
        String str = httpEntity.getContentType().getValue();
        return httpEntity.getContentType().getValue();
    }


//    @Override
//    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//        String parsed;
//        try {
//            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        }
//        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
//    }
//
//
//    @Override
//    protected void deliverResponse(String response) {
//        mListener.onResponse(response);
//    }
}
