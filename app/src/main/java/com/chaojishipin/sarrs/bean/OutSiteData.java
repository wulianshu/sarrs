package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 外网视频请求数据
 * @author xll
 *
 */
public class OutSiteData implements LetvBaseBean , Comparable<OutSiteData>{

    private String request_format;

    private List<String> api_list;

    private List<String>  allowed_formats;

    private String source;

    private List<String> stream_list;

    private String header;

    private String url;

    private String os_type;

    private String status;

    private String message;
    private String eid;
    private String rule;


    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getRequest_format() {
        return request_format;
    }

    public void setRequest_format(String request_format) {
        this.request_format = request_format;
    }

    public List<String> getApi_list() {
        return api_list;
    }

    public void setApi_list(List<String> api_list) {
        this.api_list = api_list;
    }

    public List<String> getAllowed_formats() {
        return allowed_formats;
    }

    public void setAllowed_formats(List<String> allowed_formats) {
        this.allowed_formats = allowed_formats;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getStream_list() {
        return stream_list;
    }

    public void setStream_list(List<String> stream_list) {
        this.stream_list = stream_list;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
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


    public String getRule() {
        return rule;
    }
   /**
    * rule 包含 ts te
    * */
    public void setRule(String rule) {
        this.rule = rule;
    }

    private String ts;

    public String getTe() {
        return te;
    }

    public void setTe(String te) {
        this.te = te;
    }

    public boolean isHasRule() {
        return hasRule;
    }

    public void setHasRule(boolean hasRule) {
        this.hasRule = hasRule;
    }

    private boolean hasRule;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    private String te;


    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
  /**
   *  根据优先级从小到大排序
   * */

    @Override
    public int compareTo(OutSiteData another) {
        return this.getPriority()-another.getPriority();
    }
}
