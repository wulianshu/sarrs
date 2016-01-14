package com.chaojishipin.sarrs.http.volley;


import com.chaojishipin.sarrs.http.parser.ResponseBaseParser;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SarrsManager;
import com.letv.http.bean.LetvBaseBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Sarrs基本Http api
 */
public class SarrsBaseHttpApi {
    protected static <T extends LetvBaseBean> SarrsRequest<T> createRequest(int method, String url
            , ResponseBaseParser<T> parser, Map<String, String> params,Map<String,String> healders) {
        if (method == SarrsRequest.Method.GET) {
            url = resolveGETParams(url, params);
            return new SarrsRequest<>(method, url, parser, params,null);
        }else{
            return new SarrsRequest<>(method, url, parser, params,healders);
        }
    }

    protected static HashMap<String, String> getBaseParams() {
        HashMap<String, String> ret = new HashMap<>();
//        ret.put("version", String.valueOf(Constants.VERSION_CODE));
//        ret.put("channel", Constants.CHANNEL);
//        ret.put("platform", "1");
//        ret.put("lc", "asdwdsfadgqew");
//        ret.put("version", SarrsConstants.VERSION_NAME);
        return ret;
    }

    protected static String getHost() {
        if (SarrsManager.isHttpTest()) {
            return ConstantUtils.HOST.TEST;

        } else {
            return ConstantUtils.HOST.PRODUCT;
        }
    }

    private static String resolveGETParams(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        if (params != null) {
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!isFirst) {
                    sb.append("&");
                }
                isFirst = false;
                try {
                    String value = entry.getValue();
                    if(value == null){
                        value = "";
                    }
                    sb.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            url = sb.toString();
        }
        return url;
    }

}
