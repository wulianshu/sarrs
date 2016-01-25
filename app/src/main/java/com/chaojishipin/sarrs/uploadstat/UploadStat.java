package com.chaojishipin.sarrs.uploadstat;

import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;

/**
 * Created by wulianshu on 2015/12/22.
 */
public class UploadStat {
    /**
     * 大数据上报
     *
     * @paramcid
     */
    public static void uploadstat(Object object,String acode,String pageid,String ref,String rank,String rid_topcid,String sa,String pn,String input,String gvid) {
        //请求频道页数据
        String token ="-";
        if( UserLoginState.getInstance().isLogin()) {
            token = UserLoginState.getInstance().getUserInfo().getToken();
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPLOAD_STAT+rank);
        HttpApi.
                click_stat(token, object, acode, pageid, ref, rank, rid_topcid,sa,pn,input, gvid);
    }
    public static void uploadplaystat(Object object,String ac,String ut,String retry,String play_type,String code_rate,String ref,String timing,String vlen,String seid,String peid) {
        //请求频道页数据
        String token ="-";
        if( UserLoginState.getInstance().isLogin()) {
            token = UserLoginState.getInstance().getUserInfo().getToken();
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPLOAD_STAT+ac);
        HttpApi.
                play_stat(object, token, ac, ut, retry, play_type, code_rate, ref, timing, vlen, seid, peid);
    }

    /**
     * 截流成功的上报
     *
     * @param
     *
     */
    public static void streamupload(String playurl,String stream,String format) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.JSCUT_SUCCESS_UPLOAD);
        HttpApi.
                streamUpload( playurl, stream, format)
                .start(null, ConstantUtils.JSCUT_SUCCESS_UPLOAD);
    }

    /**
     *
     * @param playurl 	播放地址
     * @param state 状态   1-客户端截流成功 2-客户端截流失败，服务端流播放成功 3-播放失败（截流失败并且服务端流播放失败）4-用户手动退出 5-下载成功 6-下载失败
     * @param type
     * @param source
     * @param aid
     * @param waiting
     */
    public static void playfeedback(String playurl,int state,int type,String source,String aid,int waiting) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.PLAY_FEED_BACK);
        HttpApi.
                playfeedBack( playurl, state, type, source, aid, waiting)
                .start(null, ConstantUtils.PLAY_FEED_BACK);
    }
}
