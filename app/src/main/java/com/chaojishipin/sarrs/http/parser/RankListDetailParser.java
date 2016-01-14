package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.RankListDetail;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class RankListDetailParser extends ResponseBaseParser<RankList> {
    private final String TAG = "RankListDetailParser";
    @Override
    public RankList parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            RankList ranklist = new RankList();
            SarrsArrayList list = new SarrsArrayList();
            JSONArray topicArray = data.optJSONArray("items");
            ranklist.setImage(data.optString("image"));
            ranklist.setTitle(data.optString("title"));
            Log.e(TAG,topicArray.toString());
            int topicSize = topicArray.length();
            for (int i = 0; i < topicSize; i++) {
                RankListDetail rankListDetail = new RankListDetail();
                JSONObject topicObj = topicArray.optJSONObject(i);
                rankListDetail.setTitle(topicObj.optString("title"));
                rankListDetail.setPlay_count(topicObj.optString("play_count"));
                rankListDetail.setEpiso_num(topicObj.optString("episo_num"));
                rankListDetail.setImage(topicObj.optString("image"));
                rankListDetail.setLabel(topicObj.optString("label"));
                rankListDetail.setGaid(topicObj.optString("gaid"));
                rankListDetail.setDescription(topicObj.optString("description"));
                rankListDetail.setCategory_id(topicObj.optString("category_id"));
                rankListDetail.setEpiso_latest(topicObj.optString("episo_latest"));
                rankListDetail.setSource(topicObj.optString("source"));
                JSONArray videos = topicObj.optJSONArray("videos");
                if (videos != null) {
                    int videoSize = videos.length();
                    ArrayList<VideoItem> videoList = new ArrayList<VideoItem>(videoSize);
                    for (int j = 0; j < videoSize; j++) {
                        VideoItem videoBean = new VideoItem();
                        JSONObject json = videos.optJSONObject(j);
                        System.out.print("json:" + json);
                        videoBean.setTitle(json.optString("title"));
                        videoBean.setGvid(json.optString("gvid"));
                        videoBean.setSource(rankListDetail.getSource());
                        videoList.add(videoBean);
                    }
                    rankListDetail.setVideos(videoList);
                } else {
                    rankListDetail.setVideos(null);
                }

                list.add(rankListDetail);
            }
            ranklist.setItems(list);
            return ranklist;
        }
        return null;
    }
}