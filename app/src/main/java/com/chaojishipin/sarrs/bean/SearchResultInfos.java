package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

/**
 * 搜索结果接口返回数据
 * @author daipei
 * http://wiki.letv.cn/pages/viewpage.action?pageId=45291709
 */
public class SearchResultInfos implements LetvBaseBean {

    private static final long serialVersionUID = 1L;

    private String total;//搜索结果数

    public String getCorrect_flag() {
        return correct_flag;
    }

    public void setCorrect_flag(String correct_flag) {
        this.correct_flag = correct_flag;
    }

    private String correct_flag;
    private String correct_word;//纠正词，如用户输入搜索词有错误，搜索服务端分析用户可能希望搜索的词，进行纠错
    private String search_word;//用户输入的搜索词
    private String illegal_flag;//是否禁词 false=普通词 true=禁词  用于搜索无结果时，判断是普通无结果还是禁词无结果，展示不同文案
    private SarrsArrayList<SearchResultDataList> items;//数据信息

    private String bucket;
    private String reid;

    public SearchResultInfos()
    {
        super();
    }

    public SearchResultInfos(SearchResultInfos copyOne)
    {
        super();
        this.total = copyOne.getTotal();
        this.correct_word = copyOne.getCorrect_word();
        this.search_word = copyOne.getSearch_word();
        this.illegal_flag = copyOne.getIllegal_flag();
        this.bucket=copyOne.getBucket();
        this.reid=copyOne.getReid();
        items = new SarrsArrayList<SearchResultDataList>();
        items.addAll(copyOne.getItems());
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCorrect_word() {
        return correct_word;
    }

    public void setCorrect_word(String correct_word) {
        this.correct_word = correct_word;
    }

    public String getSearch_word() {
        return search_word;
    }

    public void setSearch_word(String search_word) {
        this.search_word = search_word;
    }

    public String getIllegal_flag() {
        return illegal_flag;
    }

    public void setIllegal_flag(String is_illegal) {
        this.illegal_flag = is_illegal;
    }

    public void setItems(SarrsArrayList<SearchResultDataList> items) {
        this.items = items;
    }

    public SarrsArrayList<SearchResultDataList> getItems() {
        return items;
    }


    public String getReid() {
        return reid;
    }

    public void setReid(String reid) {
        this.reid = reid;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public String toString() {
        return "SearchResultInfos{" +
                "total='" + total + '\'' +
                ", correct_flag='" + correct_flag + '\'' +
                ", correct_word='" + correct_word + '\'' +
                ", search_word='" + search_word + '\'' +
                ", illegal_flag='" + illegal_flag + '\'' +
                ", items=" + items +
                ", bucket='" + bucket + '\'' +
                ", reid='" + reid + '\'' +
                '}';
    }
}
