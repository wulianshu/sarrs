package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SearchResultDataList implements LetvBaseBean {
    private String id;
    private String title;
    private String play_count;
    private String source;
    private String category_name;
    private ArrayList<VideoItem> videos;
    private String category_id;
    private String image;
    private String content_type;

    private int view_type;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setPlay_count(String play_count)
    {
        this.play_count = play_count;
    }

    public String getPlay_count()
    {
        return this.play_count;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getSource()
    {
        return this.source;
    }

    public void setCategory_name(String category_name)
    {
        this.category_name = category_name;
    }

    public String getCategory_name()
    {
        return this.category_name;
    }

    public ArrayList<VideoItem> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoItem> videos) {
        this.videos = videos;
    }

    public void setCategory_id(String category_id)
    {
        this.category_id = category_id;
    }

    public String getCategory_id()
    {
        return this.category_id;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImage()
    {
        return this.image;
    }

    public void setContent_type(String content_type)
    {
        this.content_type = content_type;
    }

    public String getContent_type()
    {
        return this.content_type;
    }
    public void setView_type(int view_type)
    {
        this.view_type = view_type;
    }

    public int getView_type()
    {
        return this.view_type;
    }

}
