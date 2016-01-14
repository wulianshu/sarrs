package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.bean.TopicDetail;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/8/24.
 */
public class TopicDetailParser extends ResponseBaseParser<Topic>{
    private String TAG = "TopicDetailParser";
    @Override
    public Topic parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            Topic atopic = new Topic();
            SarrsArrayList topicList = new SarrsArrayList();
            JSONArray topicArray = data.optJSONArray("items");
            atopic.setImage(data.optString("image"));
            atopic.setTid(data.optString("tid"));
            atopic.setTitle(data.optString("title"));
            atopic.setTotal(data.optInt("total"));
            atopic.setDescription(data.optString("description"));
            Log.e(TAG,topicArray.toString());
            int topicSize = topicArray.length();
            for (int i = 0; i < topicSize; i++) {
                TopicDetail topic = new TopicDetail();
                JSONObject topicObj = topicArray.optJSONObject(i);
                topic.setTitle(topicObj.optString("title"));
                topic.setPlay_count(topicObj.optString("play_count"));
                topic.setEpiso_num(topicObj.optString("episo_num"));
                topic.setGaid(topicObj.optString("gaid"));
                topic.setImage(topicObj.optString("image"));
                topic.setDescription(topicObj.optString("description"));
                topic.setCategory_id(topicObj.optInt("category_id"));
                topic.setLabel(topicObj.optString("label"));
                topic.setEpiso_latest(topicObj.optString("episo_latest"));
                topic.setSource(topicObj.optString("source"));

                JSONArray videos = topicObj.optJSONArray("videos");
                if(videos != null) {
                    int videoSize = videos.length();
                    ArrayList<VideoItem> videoList = new ArrayList<VideoItem>(videoSize);
                    for (int j = 0; j < videoSize; j++) {
                        VideoItem videoBean = new VideoItem();
                        JSONObject json = videos.optJSONObject(j);
                        System.out.print("json:" + json);
                        videoBean.setTitle(json.optString("title"));
                        videoBean.setGvid(json.optString("gvid"));
                        videoBean.setSource(topic.getSource());
                        videoList.add(videoBean);
                    }
                    topic.setVideos(videoList);
                }else{
                    topic.setVideos(null);
                }
                topicList.add(topic);
            }
            atopic.setItems(topicList);
            return atopic;
        }
        return null;
    }
}
