package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
*   Created by xll 2015 06 07
*   半屏播放解析类
* */
public class VideoDetailParser extends ResponseBaseParser<VideoDetailItem> {
    @Override
    public VideoDetailItem parse(JSONObject data) throws Exception {
        VideoDetailItem mVideoItem = new VideoDetailItem();
        if (data.has("status") && data.getString("status").equalsIgnoreCase(ConstantUtils.REQUEST_SUCCESS)) {
            if (data.has("item")) {
                JSONObject itemJson = data.optJSONObject("item");
                if (itemJson.has("page_index")) {
                    mVideoItem.setPage_index(itemJson.getInt("page_index"));
                }
                if (itemJson.has("publish_date")) {
                    mVideoItem.setPublish_date(itemJson.getString("publish_date"));
                }
                if (itemJson.has("play_count")) {
                    mVideoItem.setPlay_count(itemJson.getString("play_count"));
                }
                if (itemJson.has("episo_num")) {
                    mVideoItem.setEpiso_num(itemJson.getString("episo_num"));
                }
                if (itemJson.has("publisher")) {
                    mVideoItem.setPublisher(itemJson.getString("publisher"));
                }
                if (itemJson.has("image")) {
                    mVideoItem.setImg(itemJson.getString("image"));
                }
                if (itemJson.has("score")) {
                    mVideoItem.setScore(itemJson.getString("score"));
                }

                if (itemJson.has("sub_category_name")) {
                    mVideoItem.setSub_category_name(itemJson.getString("sub_category_name"));
                }
                if (itemJson.has("category_id")) {
                    mVideoItem.setCategory_id(itemJson.getString("category_id"));
                }
                if (itemJson.has("play_status")) {
                    mVideoItem.setPlay_status(itemJson.getString("play_status"));
                }
                if (itemJson.has("id")) {
                    mVideoItem.setId(itemJson.getString("id"));
                }
                if (itemJson.has("title")) {
                    mVideoItem.setTitle(itemJson.getString("title"));
                }
                if (itemJson.has("description")) {
                    mVideoItem.setDescription(itemJson.getString("description"));
                }
                if (itemJson.has("category_id")) {
                    mVideoItem.setCategory_id(itemJson.getString("category_id"));
                }
                if (itemJson.has("area_name")) {
                    mVideoItem.setArea_name(itemJson.getString("area_name"));
                }
                if (itemJson.has("episo_latest")) {
                    mVideoItem.setEpiso_latest(itemJson.getString("episo_latest"));
                }
                if (itemJson.has("source")) {
                    mVideoItem.setSource(itemJson.getString("source"));
                }
                if (itemJson.has("image")) {
                    mVideoItem.setDetailImage(itemJson.getString("image"));
                }
                if (itemJson.has("is_end")) {
                    mVideoItem.setIs_end(itemJson.getString("is_end"));
                }
                if (itemJson.has("videos")) {
                    JSONArray videosJson = itemJson.optJSONArray("videos");
                    int videoSize = videosJson.length();
                    ArrayList<VideoItem> videoList = new ArrayList<VideoItem>(videoSize);
                    for (int j = 0; j < videoSize; j++) {
                        JSONObject videoItem = videosJson.optJSONObject(j);
                        VideoItem videoBean = new VideoItem();
                        videoBean.setTitle(videoItem.optString("title"));
                        videoBean.setGvid(videoItem.optString("gvid"));
                        videoBean.setOrder(videoItem.optString("order"));
                        videoBean.setId(mVideoItem.getId());
                        videoBean.setCategory_id(mVideoItem.getCategory_id());
                        videoBean.setIs_end(mVideoItem.getIs_end());
                        videoBean.setEpiso_num(mVideoItem.getEpiso_num());
                        videoBean.setEpiso_latest(mVideoItem.getEpiso_latest());
                        videoBean.setSource(mVideoItem.getSource());
//                      videoBean.setCategory_id();
                        /**
                         * videoItem视频不存在则使用专辑的图片
                         */
                        if (videoItem.optString("image") != null && videoItem.optString("image").length() > 0) {
                            videoBean.setImage(videoItem.optString("image"));
                        } else if (mVideoItem.getDetailImage() != null && mVideoItem.getDetailImage().length() > 0) {
                            videoBean.setImage(mVideoItem.getDetailImage());
                        } else {
                            videoBean.setImage("");
                        }

                        videoBean.setCategory_id(mVideoItem.getCategory_id());
                        videoBean.setSource(itemJson.getString("source"));
                        videoList.add(videoBean);
                    }
                    mVideoItem.setVideoItems(videoList);

                }


                if (itemJson.has("metadata")) {
                    JSONArray mataJson = itemJson.optJSONArray("metadata");
                    List<String> metaList = new ArrayList<>();
                    for (int i = 0; i < mataJson.length(); i++) {
                        metaList.add(i, (String) mataJson.get(i));
                    }
                    mVideoItem.setMataList(metaList);
//                    Log.i("details","--->"+mVideoItem.getMataList().toString());
                }
                if (itemJson.has("page_titles")) {
                    List<String> titleList = new ArrayList<String>();
                    JSONArray page_titlesJson = itemJson.optJSONArray("page_titles");
                    for (int i = 0; i < page_titlesJson.length(); i++) {
                        titleList.add((String) page_titlesJson.get(i));
                    }
                    mVideoItem.setPage_titles(titleList);
                }

            }

        }
        return mVideoItem;
    }
}
