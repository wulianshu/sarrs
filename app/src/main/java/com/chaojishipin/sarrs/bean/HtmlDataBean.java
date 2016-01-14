package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

public class HtmlDataBean implements LetvBaseBean{
    
    /**
     * zhangshuo
     * 2014年9月19日 下午2:33:34
     */
    private static final long serialVersionUID = 1L;
    
    private String htmlData;

    public String getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(String htmlData) {
        this.htmlData = htmlData;


    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }





    private String code;

}
