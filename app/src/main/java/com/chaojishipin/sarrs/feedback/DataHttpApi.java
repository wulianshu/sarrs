package com.chaojishipin.sarrs.feedback;

import android.os.Bundle;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.impl.LetvHttpBaseParameter;
import com.letv.http.impl.LetvHttpParameter;
import com.letv.http.impl.LetvHttpTool;
import com.letv.http.parse.LetvBaseParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by wangyemin on 2015/10/10.
 */
public class DataHttpApi {

    public static final String TAG = "DataFeedBack";

//    private static String resolveGETParams(String url, Bundle params) {
//        StringBuilder sb = new StringBuilder(url);
//        sb.append("?");
//        if (params != null) {
//            boolean isFirst = true;
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                if (!isFirst) {
//                    sb.append("&");
//                }
//                isFirst = false;
//                try {
//                    String value = entry.getValue();
//                    if (value == null) {
//                        value = "";
//                    }
//                    sb.append(entry.getKey()).append("=")
//                            .append(URLEncoder.encode(value, "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//            url = sb.toString();
//        }
//        return url;
//    }


    /**
     * 负反馈
     *
     * @param parser
     * @param id    视频id或者专辑id
     * @param source
     * @param cid
     * @param type
     * @param token
     * @param netType
     * @param <T>
     * @param <D>
     * @return
     */
    public  static <T extends LetvBaseBean, D> LetvDataHull<T> requestDislikeReport(LetvBaseParser<T, D> parser,
                                                                                   String id,
                                                                                   String source,
                                                                                   String cid,
                                                                                   String type,
                                                                                   String token,
                                                                                   String netType,
                                                                                   String bucket,
                                                                                   String seid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_4);
        sb.append("/sarrs/dislike");
        String url = sb.toString();
        Bundle params = new Bundle();
        params.putString("id", id);
        params.putString("source", source);
        params.putString("cid", cid);
        params.putString("type", type);
        params.putString("token", token);
        params.putString("nt", netType);
        params.putString("bucket", bucket);
        params.putString("seid", seid);

        addVer(params);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
        Log.d(TAG, "!!!!!!!!!负反馈上报 url----" + url);
        Log.d(TAG, "!!!!!!!!!负反馈上报 params----" + params.toString());
        return request(httpParameter);
    }

    /**
     * 用户兴趣选择
     *
     * @param parser
     * @param id
     * @param cid
     * @param type
     * @param token
     * @param netType
     * @param <T>
     * @param <D>
     * @return
     */
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestInterestReport(LetvBaseParser<T, D> parser,
                                                                                    String id,
                                                                                    String cid,
                                                                                    String type,
                                                                                    String token,
                                                                                    String netType,
                                                                                    String bucket,
                                                                                    String seid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_4);
        sb.append("/sarrs/setinterestsurvey");
        String url = sb.toString();
        Bundle params = new Bundle();
        params.putString("id", id);
        params.putString("cid", cid);
        params.putString("type", type);
        params.putString("token", token);
        params.putString("nt", netType);
        params.putString("bucket", bucket);
        params.putString("seid", seid);
        addVer(params);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
        Log.d(TAG, "!!!!!!!!!兴趣上报 url----" + url);
        Log.d(TAG, "!!!/!!!!!!兴趣上报 params----" + params.toString());
        return request(httpParameter);
    }

    /**
     * 播放记录
     *
     * @param parser
     * @param id    视频id
     * @param aid   专辑id（没有时传空）
     * @param source
     * @param cid
     * @param playTime
     * @param token
     * @param netType
     * @param <T>
     * @param <D>
     * @return
     */
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestPlayRecordReport(LetvBaseParser<T, D> parser,
                                                                                      String id,
                                                                                      String aid,
                                                                                      String source,
                                                                                      String cid,
                                                                                      int playTime,
                                                                                      String token,
                                                                                      String netType,
                                                                                      String bucket,
                                                                                      String seid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_4);
        sb.append("/sarrs/addplayrecord");
        String url = sb.toString();
        Bundle params = new Bundle();
        params.putString("id", id);
        params.putString("aid", aid);
        params.putString("source", source);
        params.putString("cid", cid);
        params.putString("playtime", String.valueOf(playTime));
        params.putString("token", token);
        params.putString("nt", netType);
        params.putString("bucket", bucket);
        params.putString("seid", seid);
        addVer(params);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
        Log.d(TAG, "!!!!!!!!!播放记录上报 url----" + url);
        Log.d(TAG, "!!!!!!!!!播放记录上报 params----" + params.toString());
        return request(httpParameter);
    }

    /**
     * 收藏, 该接口暂不使用，使用收藏接口替代了
     *
     * @param parser
     * @param id
     * @param source
     * @param cid
     * @param type
     * @param token
     * @param netType
     * @param <T>
     * @param <D>
     * @return
     */
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAddCollectionReport(LetvBaseParser<T, D> parser,
                                                                                         String id,
                                                                                         String source,
                                                                                         String cid,
                                                                                         String type,
                                                                                         String token,
                                                                                         String netType) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_4);
        sb.append("/sarrs/addcollection");
        String url = sb.toString();
        Bundle params = new Bundle();
        params.putString("id", id);
        params.putString("source", source);
        params.putString("cid", cid);
        params.putString("type", type);
        params.putString("token", token);
        params.putString("nt", netType);
        addVer(params);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
        Log.d(TAG, "!!!!!!!!!收藏上报 url----" + url);
        Log.d(TAG, "!!!!!!!!!收藏上报 params----" + params.toString());
        return request(httpParameter);
    }

    /**
     * 分享
     *
     * @param parser
     * @param id        视频id或者专辑id
     * @param source    分享专辑、单视频时传，分享专题、排行榜不传
     * @param cid
     * @param type
     * @param token
     * @param netType
     * @param <T>
     * @param <D>
     * @return
     */
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAddShare(LetvBaseParser<T, D> parser,
                                                                              String id,
                                                                              String source,
                                                                              String cid,
                                                                              String type,
                                                                              String token,
                                                                              String netType,
                                                                              String bucket,
                                                                              String seid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_4);
        sb.append("/sarrs/addshare");
        String url = sb.toString();
        Bundle params = new Bundle();
        params.putString("id", id);
        params.putString("source", source);
        params.putString("cid", cid);
        params.putString("type", type);
        params.putString("token", token);
        params.putString("nt", netType);
        params.putString("bucket", bucket);
        params.putString("seid", seid);
        addVer(params);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
        Log.d(TAG, "!!!!!!!!!分享上报 url----" + url);
        Log.d(TAG, "!!!!!!!!!分享上报 params----" + params.toString());
        return request(httpParameter);
    }

    private static <T extends LetvBaseBean, D> LetvDataHull<T> request(
            LetvHttpBaseParameter<T, D, ?> httpParameter) {
        LetvHttpTool<T> handler = new LetvHttpTool<T>();
        return handler.requsetData(httpParameter);
    }

    static void addVer(Bundle params) {
        params.putString("p", "0");
        params.putString("pl1", "0");
        params.putString("pl2", "00");
        params.putString("appid", "0");
        params.putString("auid", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
        params.putString("appv", Utils.getClientVersionName());
        params.putString("appfrom", "0");
        params.putString("pl", "1000011");
    }
}
