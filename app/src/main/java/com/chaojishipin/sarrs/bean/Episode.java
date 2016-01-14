package com.chaojishipin.sarrs.bean;

import com.chaojishipin.sarrs.download.bean.UrlAnalysisParamter;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 剧集信息封装类
 */
public class Episode implements LetvBaseBean {
    private static final long serialVersionUID = 6046632453129007556L;
    private String intro;
    private String number;
    private String name;
    private String subName;

    private String porder;  //集数
    private String releaseDate;
    private String vid;
    private int season = 0;
    private String play_url;
    private int dataType; //如：1-专辑，2-视频，等
    private List<String> mp4Segments = new ArrayList<String>();
    private String mp4;
    private String m3u8;
    private String mid;
    private String pls;
    private String serialid;// 视频唯一标识--serialid=aid+porder
    private String request_site;// 请求的源地址
//    private String cloudId;
    private String globaVid;

    private String src;

    private String downLoadType;// 下载类型mp4/m3u8
    private String isdownload;// 判断是否能下载
    private String image;
    private String aid; //专辑id
    private String cid; //channel_id

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }


    public String getDownLoadType() {
        return downLoadType;
    }

    public void setDownLoadType(String downLoadType) {
        this.downLoadType = downLoadType;
    }

//    public String getCloudId() {
//        return cloudId;
//    }

//    public void setCloudId(String cloudId) {
//        this.cloudId = cloudId;
//    }

    private ArrayList<UrlAnalysisParamter> analysisParamters;

    // public ArrayList<UrlAnalysisParamter> getAnalysisParamters() {
    // return analysisParamters;
    // }

    // public void setAnalysisParamters(
    // ArrayList<UrlAnalysisParamter> analysisParamters) {
    // this.analysisParamters = analysisParamters;
    // }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getRequest_site() {
        return request_site;
    }

    public void setRequest_site(String request_site) {
        this.request_site = request_site;
    }

    public String getSerialid() {
        return serialid;
    }

    public void setSerialid(String serialid) {
        this.serialid = serialid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }



    public String getPorder() {
        return porder;
    }

    public void setPorder(String porder) {
        this.porder = porder;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }


    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public List<String> getMp4Segments() {
        return mp4Segments;
    }

    public void setMp4Segments(List<String> mp4Segments) {
        this.mp4Segments = mp4Segments;
    }

    public String getM3u8() {
        return m3u8;
    }

    public void setM3u8(String m3u8) {
        this.m3u8 = m3u8;
    }

    public String getMp4() {
        return mp4;
    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

    public String getPls() {
        return pls;
    }

    public void setPls(String pls) {
        this.pls = pls;
    }

    public String getGlobaVid() {
        return globaVid;
    }

    public void setGlobaVid(String globaVid) {
        this.globaVid = globaVid;
    }

    public String getIsdownload() {
        return isdownload;
    }

    public void setIsdownload(String isdownload) {
        this.isdownload = isdownload;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

}
