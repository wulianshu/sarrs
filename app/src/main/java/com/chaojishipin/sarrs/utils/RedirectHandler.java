package com.chaojishipin.sarrs.utils;


import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

/**
 *
 * Created by xll on 2015/9/20.
 */
public class RedirectHandler extends DefaultRedirectHandler {

    @Override
    public boolean isRedirectRequested(HttpResponse response,
                                       HttpContext context) {
// TODO Auto-generated method stub
        return false;
    }

}
