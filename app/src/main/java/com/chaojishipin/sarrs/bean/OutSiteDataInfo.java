package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;
import java.util.Map;

/**
 * 外网视频请求数据
 * @author xll
 *
 */
public class OutSiteDataInfo implements LetvBaseBean {


    private String status;
    private String message;

    private List<OutSiteData> outSiteDatas;

    public boolean isHasStreamList() {
        return isHasStreamList;
    }

    public void setIsHasStreamList(boolean isHasStreamList) {
        this.isHasStreamList = isHasStreamList;
    }


    private boolean isHasStreamList = false;
    /**
     *  处理外站源播放不了（QQ、芒果 存放mp4格式的map）
     * */
    public List<OutSiteData> getOutSiteDatas() {
        return outSiteDatas;
    }
    private Map<String ,OutSiteData> outSiteDataMap;

    public Map<String, OutSiteData> getOutSiteDataMap() {
        return outSiteDataMap;
    }

    public void setOutSiteDataMap(Map<String, OutSiteData> outSiteDataMap) {
        this.outSiteDataMap = outSiteDataMap;
    }

    public List<String> getRequestFromats() {
        return requestFromats;
    }

    public void setRequestFromats(List<String> requestFromats) {
        this.requestFromats = requestFromats;
    }

    private List<String> requestFromats;
    public void setOutSiteDatas(List<OutSiteData> outSiteDatas) {
        this.outSiteDatas = outSiteDatas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
