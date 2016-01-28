package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 直播流地址实体类
 * Created by wangyemin on 2016/1/24.
 */
public class LiveStreamEntity implements LetvBaseBean {
    private String rateType;
    private String streamId;
    private String rate;
    private String streamUrl;
    private String streamName;

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    @Override
    public String toString() {
        return "LiveStreamEntity{" +
                "rateType='" + rateType + '\'' +
                ", streamId='" + streamId + '\'' +
                ", rate='" + rate + '\'' +
                ", streamUrl='" + streamUrl + '\'' +
                ", streamName='" + streamName + '\'' +
                '}';
    }
}
