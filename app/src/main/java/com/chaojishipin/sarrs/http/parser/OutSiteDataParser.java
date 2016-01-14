package com.chaojishipin.sarrs.http.parser;


import android.text.TextUtils;
import android.util.Base64;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.OutSiteData;
import com.chaojishipin.sarrs.bean.OutSiteDataInfo;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  外网视频请求解析类
 *  @author xll
 */
public class OutSiteDataParser extends ResponseBaseParser<OutSiteDataInfo> {


    @Override
    protected JSONObject getData(String data) throws JSONException {

        byte[] decodeData = Base64.decode(data, Base64.DEFAULT);
        String result = Utils.AES256_decode(decodeData, ConstantUtils.AES_KEY);
        if (!TextUtils.isEmpty(result)) {
            LogUtil.e("xll", "outSite ：result " + result);
            return new JSONObject(result);
        }
        return null;

    }

    @Override
    public OutSiteDataInfo parse(JSONObject main) throws Exception {
        OutSiteData outSiteData=null;
        OutSiteDataInfo info=null;
        if(!TextUtils.isEmpty(main.optString("status"))
                &&main.optString("status").equalsIgnoreCase(ConstantUtils.RESULT_OK)){
            String fileName="response.html";
            FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce().getApplicationContext(),fileName,main.toString());
            JSONArray data=main.optJSONArray("data");
            info=new OutSiteDataInfo();
            if(main.has("status")){
                info.setStatus(main.optString("status"));
            }
            List<OutSiteData> outList=new ArrayList<>();
            List<String> reqs=new ArrayList<>();
            Map<String ,OutSiteData> outSiteDataMap=new HashMap<>();
            if(data!=null&&data.length()>0) {

                for(int i=0;i<data.length();i++){
                    outSiteData=new OutSiteData();

                    JSONObject child=(JSONObject)data.get(i);
                    if(child.has("eid")){
                        outSiteData.setEid(child.optString("eid"));
                    }
                    if(child.has("rule")){

                        outSiteData.setHasRule(true);
                        JSONObject ruleObj=child.optJSONObject("rule");
                        if(ruleObj.has("ts")){
                            outSiteData.setTs(ruleObj.getString("ts"));
                        }
                        if(ruleObj.has("te")){
                            outSiteData.setTe(ruleObj.getString("te"));
                        }
                        // outSiteData.setRule(child.optString("rule"));
                    }else{
                        outSiteData.setHasRule(false);
                    }

                    if(child.has("source")){
                            outSiteData.setSource(child.optString("source"));
                    }
                    if(child.has("url")){
                            outSiteData.setUrl(child.optString("url"));
                    }
                    if(child.has("os_type")){
                            outSiteData.setOs_type(child.optString("os_type"));
                    }
                    if(child.has("header")){
                        JSONObject headerObj=child.getJSONObject("header");
                            outSiteData.setHeader(headerObj.optString("User-Agent"));
                    }
                    if(child.has("api_list")){
                        JSONArray api_list=child.optJSONArray("api_list");
                        outSiteData.setApi_list(readJsonArrayData(api_list));
                    }
                   if(child.has("allowed_formats")){
                       JSONArray allowed_formats=child.optJSONArray("allowed_formats");
                       outSiteData.setAllowed_formats(readJsonArrayData(allowed_formats));
                   }
                  if(child.has("stream_list")){
                      JSONArray stream_list=child.optJSONArray("stream_list");
                      List<String> streams=readJsonArrayData(stream_list);
                      if(streams!=null&&streams.size()>0){
                          info.setIsHasStreamList(true);
                      }
                      outSiteData.setStream_list(streams);
                  }
                  if(child.has("request_format")){
                        outSiteData.setRequest_format(child.optString("request_format"));
                  }
                  if(child.has("os_type")&&child.has("request_format")){
                      outSiteData.setPriority(Utils.getPriority(child.optString("os_type"),child.optString("request_format")));
                      // 构造 mp4 reqmap
                      if(!outSiteDataMap.containsKey(child.optString("request_format"))&&child.has("stream_list")&&child.optString("os_type").equalsIgnoreCase(ConstantUtils.OutSiteDateType.MP4)) {
                          outSiteDataMap.put(child.optString("request_format"), outSiteData);
                      }
                  }


                    outList.add(outSiteData);

                }
                Collections.sort(outList);
                info.setOutSiteDatas(outList);
                info.setOutSiteDataMap(outSiteDataMap);
                if(info.getOutSiteDatas()!=null&&info.getOutSiteDatas().size()>0){
                    for(int i=0;i<info.getOutSiteDatas().size();i++){
                        LogUtil.e("xll","NEW priority : "+info.getOutSiteDatas().get(i).getPriority());
                    }
                }


            }
        }else{
            // no stream 501
            info=new OutSiteDataInfo();
            ArrayList<OutSiteData>outs=new ArrayList<>();
            OutSiteData out=new OutSiteData();
            out.setPriority(1);
            if(main.has("status")){
                info.setStatus(main.optString("status"));
            }
            if(main.has("url")){
                out.setUrl(main.optString("url"));
            }
            outs.add(out);
            info.setOutSiteDatas(outs);




        }
        return info;
    }


    /**
     *  读取api_list allowed_formats stream_list
     * */
    List<String> readJsonArrayData(JSONArray array){

        List<String> strList=new ArrayList<>();
        for(int i=0;i<array.length();i++){
               if(!TextUtils.isEmpty(array.optString(i))){
                   strList.add(array.optString(i));
               }
        }
        return strList;

    }



}


