package com.chaojishipin.sarrs.thread;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.utils.FileUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

;


/**
 *  send Js工作线程
 * Created by xll on 2015/12/05
 */
public class JSThread implements Runnable{

    private String apiContent;
    private String streamUrl;
    private String requestUrl;
    private boolean isHasRule;
    private String mTs;
    private String mTe;
    private Handler mHandler;

    public JSThread( Handler handler,String requestUrl,String apicontent,String stream,boolean isHasRule,String ts,String te){
        this.apiContent=apicontent;
        this.requestUrl=requestUrl;
        this.streamUrl=stream;
        this.mTs=ts;
        this.mTe=te;
        this.isHasRule=isHasRule;
        this.mHandler=handler;
    }


    void buildJsparam(){
        com.alibaba.fastjson.JSONObject obj = new  com.alibaba.fastjson.JSONObject();
        LogUtil.e("xll", "NEW send js requestUrl:" + requestUrl + " streamUrl " + streamUrl);
        obj.put("requestUrl", Base64.encodeToString(requestUrl.getBytes(), Base64.DEFAULT));
        obj.put("uStream", Base64.encodeToString(streamUrl.getBytes(), Base64.DEFAULT));
        obj.put("apiContent", Base64.encodeToString(apiContent.getBytes(), Base64.DEFAULT));
        //TODO rule 值获取
        if(isHasRule){
            LogUtil.e("xll", "NEW js has rule");
            com.alibaba.fastjson.JSONObject ruleObj = new  com.alibaba.fastjson.JSONObject();

            if(!TextUtils.isEmpty(mTs)){
                ruleObj.put("ts",mTs);
            }
            if(!TextUtils.isEmpty(mTe)){
                ruleObj.put("te",mTe);
            }
            obj.put("rule", ruleObj);
            LogUtil.e("xll", "NEW js rule " + ruleObj.toString());
            LogUtil.e("xll", "NEW js ts te (" +mTs+")  ("+mTe+")");
        }else{
            LogUtil.e("xll", "NEW js no rule");
        }

        String  mSnifferParamter = obj.toString().replace("\\n","");
        mHandler.sendEmptyMessage(Utils.GET_JS_RESULT);
        String fileName="request.html";
        FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce(), fileName, mSnifferParamter);
        LogUtil.e("xll","NEW js "+mSnifferParamter);

    }


    @Override
    public void run() {
        LogUtil.e("xll", "thread : " + Thread.currentThread().getName() + " start");
        // exe task
        buildJsparam();
        LogUtil.e("xll","thread : "+Thread.currentThread().getName()+" end ");
    }


    @Override
    public String toString(){
        return isHasRule+"\n"+streamUrl+"\n"+mTs+"\n"+mTe+"\n";
    }
}




