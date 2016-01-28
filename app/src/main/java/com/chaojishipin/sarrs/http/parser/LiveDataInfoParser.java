package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.LiveDataEntity;
import com.chaojishipin.sarrs.bean.LiveDataInfo;
import com.chaojishipin.sarrs.bean.LiveProgramEntity;
import com.chaojishipin.sarrs.bean.LiveStreamEntity;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播频道数据解析
 * Created by wangyemin on 2016/1/24.
 */
public class LiveDataInfoParser extends ResponseBaseParser<LiveDataInfo> {
    private static final String ROWS = "rows";
    private static final String ICON = "icon";
    private static final String PROGRAMS = "programs";
    private static final String TITLE = "title";
    private static final String BEGINTIME = "beginTime";
    private static final String CHANNELNAME = "channelName";
    private static final String REC = "rec";
    private static final String CHANNELID = "channelId";
    private static final String POSTER = "poster";
    private static final String CHANNELENAME = "channelEname";
    private static final String STREAMS = "streams";
    private static final String RATETYPE = "rateType";
    private static final String STREAMID = "streamId";
    private static final String RATE = "rate";
    private static final String STREAMURL = "streamUrl";
    private static final String STREAMNAME = "streamName";
    private static final String CID = "cid";

    @Override
    public LiveDataInfo parse(JSONObject data) throws Exception {
        LiveDataInfo liveDataInfo = new LiveDataInfo();
        if (null != data && ConstantUtils.HttpRequestStatus.STATUSCODE.equalsIgnoreCase(data.optString(ConstantUtils.HttpRequestStatus.STATUS)) && data.has(ROWS)) {
            JSONArray liveDataArr = data.optJSONArray(ROWS);
            if (null != liveDataArr && liveDataArr.length() > 0) {
                List<LiveDataEntity> liveInfoList = new ArrayList<LiveDataEntity>();
                for (int i = 0; i < liveDataArr.length(); i++) {
                    LiveDataEntity liveDataEntity = new LiveDataEntity();
                    JSONObject liveDataObj = (JSONObject) liveDataArr.opt(i);
                    if (null != liveDataObj) {
                        String cid = liveDataObj.optString(CID);
                        // 电视台直播数据解析
                        if (!StringUtil.isEmpty(cid) && ConstantUtils.LIVE_TELEVISION.equalsIgnoreCase(cid)) {
                            liveDataEntity.setCid(liveDataObj.optString(CID));
                            liveDataEntity.setIcon(liveDataObj.optString(ICON));
                            // 设置programs
                            JSONArray programsArr = liveDataObj.optJSONArray(PROGRAMS);
                            if (null != programsArr && programsArr.length() > 0) {
                                List<LiveProgramEntity> programs = new ArrayList<LiveProgramEntity>();
                                for (int j = 0; j < programsArr.length(); j++) {
                                    LiveProgramEntity liveProgramEntity = new LiveProgramEntity();
                                    JSONObject programObj = (JSONObject) programsArr.opt(j);
                                    if (null != programObj) {
                                        liveProgramEntity.setTitle(programObj.optString(TITLE));
                                        liveProgramEntity.setBeginTime(programObj.optString(BEGINTIME));
                                    }
                                    programs.add(liveProgramEntity);
                                }
                                liveDataEntity.setPrograms(programs);
                            }
                            liveDataEntity.setChannelName(liveDataObj.optString(CHANNELNAME));
                            liveDataEntity.setRec(liveDataObj.optString(REC));
                            liveDataEntity.setChannelId(liveDataObj.optString(CHANNELID));
                            liveDataEntity.setPoster(liveDataObj.optString(POSTER));
                            liveDataEntity.setChannelEname(liveDataObj.optString(CHANNELENAME));
                            // 设置streams
                            JSONArray streamsArr = liveDataObj.optJSONArray(STREAMS);
                            if (null != streamsArr && streamsArr.length() > 0) {
                                List<LiveStreamEntity> streams = new ArrayList<LiveStreamEntity>();
                                for (int j = 0; j < streamsArr.length(); j++) {
                                    LiveStreamEntity liveStreamEntity = new LiveStreamEntity();
                                    JSONObject streamObj = (JSONObject) streamsArr.opt(j);
                                    if (null != streamObj) {
                                        liveStreamEntity.setRateType(streamObj.optString(RATETYPE));
                                        liveStreamEntity.setStreamId(streamObj.optString(STREAMID));
                                        liveStreamEntity.setRate(streamObj.optString(RATE));
                                        liveStreamEntity.setStreamUrl(streamObj.optString(STREAMURL));
                                        liveStreamEntity.setStreamName(streamObj.optString(STREAMNAME));
                                    }
                                    streams.add(liveStreamEntity);
                                }
                                liveDataEntity.setStreams(streams);
                            }
                        } else if (!StringUtil.isEmpty(cid) && ConstantUtils.LIVE_SPORT.equalsIgnoreCase(cid)) {
                            liveDataEntity.setCid(liveDataObj.optString(CID));
                        }
                    }
                    liveInfoList.add(liveDataEntity);
                }
                liveDataInfo.setRows(liveInfoList);
            }
        }
        return liveDataInfo;
    }
}
