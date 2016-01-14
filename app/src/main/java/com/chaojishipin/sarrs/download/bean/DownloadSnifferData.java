package com.chaojishipin.sarrs.download.bean;

import java.util.ArrayList;

/**
 * Created by vicky on 15/9/1.
 */
public class DownloadSnifferData {
    String site;
    String m3u8_parse_time;
    String m3u8_api;
    String mp4_parse_time;
    Mp4Param mp4_param;
    String url;
    UrlApi mp4_api;
    String id;
    UrlApi m3u8_play_url;
    UrlApi mp4_play_url;
    String source;
    String is_downlaod;
    String m3u8_param;
    ArrayList rule;
    String p_id;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getM3u8_parse_time() {
        return m3u8_parse_time;
    }

    public void setM3u8_parse_time(String m3u8_parse_time) {
        this.m3u8_parse_time = m3u8_parse_time;
    }

    public String getM3u8_api() {
        return m3u8_api;
    }

    public void setM3u8_api(String m3u8_api) {
        this.m3u8_api = m3u8_api;
    }

    public String getMp4_parse_time() {
        return mp4_parse_time;
    }

    public void setMp4_parse_time(String mp4_parse_time) {
        this.mp4_parse_time = mp4_parse_time;
    }

    public Mp4Param getMp4_param() {
        return mp4_param;
    }

    public void setMp4_param(Mp4Param mp4_param) {
        this.mp4_param = mp4_param;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UrlApi getMp4_api() {
        return mp4_api;
    }

    public void setMp4_api(UrlApi mp4_api) {
        this.mp4_api = mp4_api;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UrlApi getM3u8_play_url() {
        return m3u8_play_url;
    }

    public void setM3u8_play_url(UrlApi m3u8_play_url) {
        this.m3u8_play_url = m3u8_play_url;
    }

    public UrlApi getMp4_play_url() {
        return mp4_play_url;
    }

    public void setMp4_play_url(UrlApi mp4_play_url) {
        this.mp4_play_url = mp4_play_url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIs_downlaod() {
        return is_downlaod;
    }

    public void setIs_downlaod(String is_downlaod) {
        this.is_downlaod = is_downlaod;
    }

    public String getM3u8_param() {
        return m3u8_param;
    }

    public void setM3u8_param(String m3u8_param) {
        this.m3u8_param = m3u8_param;
    }

    public ArrayList getRule() {
        return rule;
    }

    public void setRule(ArrayList rule) {
        this.rule = rule;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public class Mp4Param
    {
        private HighUrlParam HighUrl;

        public HighUrlParam getHighUrl() {
            return HighUrl;
        }

        public void setHighUrl(HighUrlParam highUrl) {
            HighUrl = highUrl;
        }
    }

    public class HighUrlParam
    {
        private String LAYER;
        private String ts;
        private String V_stream;
        private String te;
        private String referer;

        public String getLAYER() {
            return LAYER;
        }

        public void setLAYER(String LAYER) {
            this.LAYER = LAYER;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }

        public String getV_stream() {
            return V_stream;
        }

        public void setV_stream(String v_stream) {
            V_stream = v_stream;
        }

        public String getTe() {
            return te;
        }

        public void setTe(String te) {
            this.te = te;
        }

        public String getReferer() {
            return referer;
        }

        public void setReferer(String referer) {
            this.referer = referer;
        }
    }

    public class UrlApi
    {
        private String HighUrl;

        public String getHighUrl() {
            return HighUrl;
        }

        public void setHighUrl(String highUrl) {
            HighUrl = highUrl;
        }
    }
}
