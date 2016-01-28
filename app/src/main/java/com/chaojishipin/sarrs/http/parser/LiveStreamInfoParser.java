package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.LiveStreamEntity;
import com.chaojishipin.sarrs.bean.LiveStreamInfo;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONObject;

/**
 * Created by wangyemin on 2016/1/26.
 */
public class LiveStreamInfoParser extends ResponseBaseParser<LiveStreamInfo> {
    private static final String ROWS = "rows";
    private static final String RATETYPE = "rateType";
    private static final String STREAMID = "streamId";
    private static final String RATE = "rate";
    private static final String STREAMURL = "streamUrl";
    private static final String STREAMNAME = "streamName";

    @Override
    public LiveStreamInfo parse(JSONObject data) throws Exception {
        LiveStreamInfo liveStreamInfo = new LiveStreamInfo();
        if (null != data && ConstantUtils.HttpRequestStatus.STATUSCODE.equalsIgnoreCase(data.optString(ConstantUtils.HttpRequestStatus.STATUS)) && data.has(ROWS)) {
            liveStreamInfo.setRows(JsonUtil.parseArray(data.optString(ROWS), LiveStreamEntity.class));
        }
        return liveStreamInfo;
    }
}
