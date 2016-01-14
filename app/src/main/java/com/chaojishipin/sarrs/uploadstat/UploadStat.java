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
    public static void uploadstat(Object object,String acode,String pageid,String ref,String rank,String rid_topcid,String sa,String pn,String input) {
        //请求频道页数据
        String token ="-";
        if( UserLoginState.getInstance().isLogin()) {
            token = UserLoginState.getInstance().getUserInfo().getToken();
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPLOAD_STAT+rank);
        HttpApi.
                click_stat(token, object, acode, pageid, ref, rank, rid_topcid,sa,pn,input);
    }
    public static void uploadplaystat(Object object,String ac,String ut,String retry,String play_type,String code_rate,String ref,String timing,String vlen,String seid,String peid) {
        //请求频道页数据
        String token ="-";
        if( UserLoginState.getInstance().isLogin()) {
            token = UserLoginState.getInstance().getUserInfo().getToken();
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPLOAD_STAT+ac);
        HttpApi.
                play_stat( object,token , ac,  ut,  retry,  play_type,  code_rate,  ref,timing,vlen,seid,peid);
    }
//    /**
//     * 大数据上报
//     *
//     * @paramcid
//     */
//    public static void query(Object object,String acode,String pageid,String ref,String rank,String rid_topcid) {
//        //请求频道页数据
//        String token ="-";
//        if( UserLoginState.getInstance().isLogin()) {
//            token = UserLoginState.getInstance().getUserInfo().getToken();
//        }
//        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPLOAD_STAT);
//        HttpApi.
//                click_stat(token, object, acode, pageid, ref, rank, rid_topcid);
//        //.start(null, ConstantUtils.REQUEST_UPLOAD_STAT);
//    }
}
