package com.chaojishipin.sarrs.http.parser;

import android.text.TextUtils;

import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainActivityData;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhangshuo on 2015/6/17.
 */
public class MainActivityDataParser extends ResponseBaseParser<MainActivityData> {

    @Override
    public MainActivityData parse(JSONObject data) throws Exception {
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            MainActivityData mainActivityData = new MainActivityData();
            mainActivityData.setReid(data.optString("reid"));
            mainActivityData.setBucket(data.optString("bucket"));
            mainActivityData.setCategory(data.optString("category_id"));


            JSONArray albumsArr = data.optJSONArray("items");
            int albumsize = albumsArr.length();
            ArrayList<MainActivityAlbum> albums = new ArrayList<>(albumsize);
            for (int i = 0; i < albumsize; i++) {
                JSONObject albumItem = albumsArr.optJSONObject(i);
                MainActivityAlbum album = new MainActivityAlbum();
                album.setBucket(data.optString("bucket"));
                album.setReId(data.optString("reid"));
                album.setTitle(albumItem.optString("title"));
                album.setPlay_count(albumItem.optString("play_count"));
                album.setImgage(albumItem.optString("image"));
                album.setLable(albumItem.optString("label"));
                album.setContentType(albumItem.optString("content_type"));
                album.setId(albumItem.optString("id"));
                album.setDescription(albumItem.optString("description"));
                // cid与items同级
                album.setSource(albumItem.optString("source"));
                album.setCategory_id(albumItem.optString("category_id"));

                JSONArray videos = albumItem.optJSONArray("videos");
                int videoSize = videos.length();
                ArrayList<VideoItem> videoList = new ArrayList<VideoItem>(videoSize);
                for (int j = 0; j < videoSize; j++) {
                    JSONObject videoItem = videos.optJSONObject(j);
                    VideoItem videoBean = new VideoItem();
                    if (TextUtils.isEmpty(videoItem.optString("title"))) {
                        videoBean.setTitle(albumItem.optString("title"));
                    } else
                        videoBean.setTitle(videoItem.optString("title"));
                    videoBean.setGvid(videoItem.optString("gvid"));
                    videoBean.setSource(albumItem.optString("source"));
                    videoList.add(videoBean);
                }
                album.setVideos(videoList);
                albums.add(album);
            }
            mainActivityData.setAlbumList(albums);
            return mainActivityData;
        }
        return null;
    }
}
