package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

/**
 * 搜索suggest接口返回数据
 * @author daipei
 * http://wiki.letv.cn/pages/viewpage.action?pageId=45558624
 */
public class SearchSuggestInfos implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    private String status;

    private SarrsArrayList<SearchSuggestDataList> items;//信息list

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SarrsArrayList<SearchSuggestDataList> getItems() {
        return items;
    }

    public void setItems(SarrsArrayList<SearchSuggestDataList> items) {
        this.items = items;
    }

}
