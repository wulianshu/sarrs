package com.chaojishipin.sarrs.utils;

import android.content.Context;

/**
 * 常量Utils
 * Created by zhangshuo on 2015/6/1.
 */
public class ConstantUtils {
    public static String SHARE_APP_TAG = "chaojishipin_settings";
    public static String COMMENT_DEVICE = "chaojishipin";
    //CATEGORY_ID
    //电视剧
    public static String TV_SERISE_CATEGORYID = "1";
    //动漫
    public static String CARTOON_CATEGORYID = "3";
    //电影
    public static String MOVIES_CATEGORYID = "2";
    //记录片
    public static String DOCUMENTARY_CATEGORYID = "16";
    //综艺节目
    public static String VARIETY_CATEGORYID = "4";

    // 电视剧
    public static int MAIN_DATA_TYPE_1 = 1;
    // 综艺
    public static int MAIN_DATA_TYPE_2 = 2;
    // 动漫
    public static int MAIN_DATA_TYPE_3 = 3;
    //专题Contant_Type
    public static String TOPIC_CONTENT_TYPE = "8";

    //排行榜Contant_Type
    public static String RANKLIST_CONTENT_TYPE = "9";

    //直播Contant_Type
    public static String LIVE_CONTENT_TYPE = "6";

    public final static String TITLE_MOVIE = "电影";

    public final static String TITLE_SUGGEST = "精彩推荐";

    public final static String TITLE_ZHUANTI = "专题";

    public final static String TITLE_LIVE = "直播";

    public final static int NET_TYPE_ERROR = -1;

    public final static String NET_TYPE_NAME = "unknow";

    public final static String REQUEST_SUCCESS = "200";

    public final static String REQUEST_FAILURE = "202";

    public final static String SLIDINGMENU_CHANNEL = "channels";

    public final static String SLIDINGMENU_LINE = "10";

    public final static String SLIDINGMENU_SUGGEST = "suggests";

    public final static String FILECACHE_SLIDINGMENU_DATA = "channellist.txt";

    public final static String INTEREST_COMMENT_AREA = "rec_0701";

    public final static String MAINACTIVITY_REFRESH_AREA = "rec_0703";

    public final static String MAINACTIVITY_LOAD_AREA = "rec_0703";
    //新流服务判断是播放请求还是下载请求
    public final static String GETURL4PLAY = "GETURL4PLAY";//播放
    public final static String GETURL4DOWNLOAD = "GETURL4DOWNLOAD";
    public final static String M3U8FILETAG = "#EXTM3U";

    public static String DEVICE_ID;

    public static void init(Context context) {
        DEVICE_ID = Utils.getDeviceId(context);
    }

    public final static int CANCEL_REFRESH_DATA = 0;

    public final static int CANCEL_LOADING_DATA = CANCEL_REFRESH_DATA + 1;

    public static final int SEARCH_RECORD_MAXIMUM_SIZE = 10;

    public static final byte[] AES_KEY = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54};

    public static int TV_SERISE_ONEPAGE_COUNT = 60;

    public static int VARIETY_ONEPAGE_COUNT = 10;
    /***********************
     * Hanlder相关静态值
     ********************/
    public static final int HANDLER_MESSAGEDELAYED_1000 = 1000;//延时1秒

    /*************************************
     * http相关http静态值
     *****************************/
    public static final String HTTP_REQUEST_TAG = "SarrsRequest";//请求log的Tag

    public static final int HTTP_REQUEST_DEFAULT_TIMEOUT_MS = 10000;//超时时间

    public interface HOST {
        public static final String TEST = "http://111.206.210.12:8080";
        public static final String PRODUCT = "http://115.182.93.104:8080";
        public static final String DOMON_1 = "http://search.chaojishipin.com";
        public static final String DOMON_2 = "http://back.chaojishipin.com";
        //public static final String DOMON_2 = "http://10.154.252.65:8080";

        public static final String DOMON_3 = "http://rec.chaojishipin.com";
        public static final String DOMON_4 = "http://feedback.chaojishipin.com";

        //        public static final String DOMON_3 = "http://10.154.252.65:8080";
//        public static final String DOMON_4 = "http://10.154.252.65:8080";
        //public static final String DOMON_5 = "http://10.154.252.65:8080";
        public static final String DOMON_5 = "http://user.chaojishipin.com";
        public static final String DOMON_6 = "http://play.chaojishipin.com";
        public static final String DOMON_7 = "http://share.chaojishipin.com";
        public static final String DOMAIN_8 = "http://play.chaojishipin.com";
        public static final String DOMON_9 = "http://d.chaojishipin.com/stat.html";
    }

    public interface MediaMode {
        public String ONLINE = "online";
        public String LOCAL = "local";
    }

    public interface LeTvBitStreamParam {
        public String KEY_GVID = "gvid";
        public String KEY_VTYPE = "vtype";
        public String KEY_PLAY_ID = "playid";
        public String KEY_DOWNLOAD = "2";
        public String KEY_TSS = "tss";
        public String KEY_KEY = "key";
        public String KEY_GETPLAYURL_KEY = "SARRS20!%";
    }

    /**
     * 播放器进图条发生变化
     */
    public static final int PROGRESS_CHANGE = 0;
    /**
     * 控制栏消失
     */
    public static final int DISSMISS_MEDIACONTROLLER = PROGRESS_CHANGE + 1;

    public static final int JUDGE_BUFFER = DISSMISS_MEDIACONTROLLER + 1;

    /**
     * 控制栏消失的时间
     */
    public static final int MEDIA_CONTROLLER_DISMISS_TIME = 5000;
    public static final int MEDIA_CONTROLLER_DISMISS__NO_TIME = 0000;
    /**
     * 判断视频缓冲的延迟时间
     */
    public static final long JUDGE_BUFFER_DELAY_TIME = 3000;

    /*****************************
     * 所有http请求Tag
     *****************************************/
    public final static String REQUEST_SLDINGMENU_LETF_TAG = "request_slidingmenu_left";

    public final static String REQUEST_MAINACTIVITY_DATA = "request_mainactivity_data";
    public final static String REQUEST_TOPIC = "request_topic";
    public final static String REQUEST_RANKLIST = "request_ranklist";
    public final static String REQUEST_TOPIC_DETAIL = "request_topic_detail";
    public final static String REQUEST_RANKLIST_DETAIL = "request_ranklist_detail";
    public final static String REQUEST_HISTORYRECORD_DETAIL = "request_ranklist_detail";
    public final static String JSCUT_SUCCESS_UPLOAD = "jscut_success_upload";
    public final static String PLAY_FEED_BACK = "play_feed_back";
    public final static String REQUEST_VIDEODETAIL_HALF_PLAY_TAG = "request_videodetail_half_play";
    public final static String REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG = "request_videodetail_video_index";
    public final static String REQUEST_SEARCHRESULT_TAG = "request_searchresult";
    public final static String REQUEST_SEARCHSUGGEST_TAG = "request_searchsuggest";
    public final static String REQUEST_SEARCH_TOPLIST_TAG = "request_searchtoplistt";
    public final static String REQUEST_GETPLAYURL_TAG = "request_playurl";
    public final static String REQUEST_GETVERIFYCODE_TAG = "request_verifycode";
    public final static String REQUEST_LOGIN_TAG = "request_login";
    public final static String REQUEST_WXLOGIN_TAG = "request_wxlogin";
    public final static String REQUEST_SINALOGIN_TAG = "request_sinalogin";
    public final static String REQUEST_QQLOGIN_TAG = "request_qqlogin";
    public final static String REQUEST_MODIFYUSERINFO_TAG = "request_modifyuserinfo";
    public final static String REQUEST_LOGOUT_TAG = "request_loginout";
    public final static String REQUEST_SIGNLEVIDEO_TAG = "request_signlevideo";
    public final static String REQUEST_DOWNLOADURL_TAG = "request_download";
    public final static String REQUEST_UPDATEURL = "request_udpateurl";
    public final static String REQUEST_CLOUDDISKVIDEO_TAG = "request_clouddisk";
    public final static String REQUEST_UPGRADE = "request_upgrade";
    public final static String REQUEST_JSCODE = "request_jscode";
    public final static String REQUEST_USERCOMMENTINFO_TAG = "request_usercommentinfo";
    public final static String REQUEST_ADDCOMMENTINFO_TAG = "request_addcomment";
    public final static String REQUEST_INTERESTRECOMMEND_TAG = "request_interestcommend";

    public final static String UPLOAD_HISTORY_RECORD = "upload_history_record";
    public final static String UPLOAD_HISTORY_RECORD_ONE_RECORD = "upload_history_record_one_record";
    public final static String REQUEST_FAVORITE_BATCH = "request_favorite_batch";
    public final static String REQUEST_GET_FAVORITE_RESULT = "request_favorite_result";
    public final static String REQUEST_ADD_FAVORITE = "request_add_favorite";
    public final static String REQUEST_CANCEL_FAVORITE = "request_cancel_favorite";
    public final static String REQUEST_ISEXISTS_FAVORITE = "request_isexists_favorite";
    public final static String REQUEST_UPLOAD_STAT = "request_upload_stat";
    // live相关
    public final static String REQUEST_LIVE_DATA_TAG = "request_live_data";
    public final static String REQUEST_LIVE_STREAM_DATA_TAG = "request_live_stream_data";
    /*****************************
     * 播放器来源页面枚举值
     ****************************************/
    public static final String PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK_NOTIFY = "tagClickNotify";
    public static final String PLAYER_FROM_SPECAIL = "specail";
    public static final String PLAYER_FROM_DOWNLOAD = "download";
    public static final String PLAYER_FROM_RANKLIST = "ranklist";
    public static final String PLAYER_FROM_MAIN = "mainpage";
    public static final String PLAYER_FROM_SEARCH = "searchpage";
    public static final String PLAYER_FROM_DETAIL = "detailpage";//详情页传递数据
    public static final String PLAYER_FROM_DETAIL_ITEM = "detailitem";//详情页点击item
    public static final String PLAYER_FROM_HISTORY = "history";
    /*****************************
     * 播放器发送消息类型
     *********************************/
    // 点击播放器剧集分页TAG消息
    public static final String PLAYER_FROM_FULLSCREEN_PLAY_NEXT_CLICK = "playnextclick";
    public static final String PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK = "fullTagClick";
    public static final String PLAYER_FROM_CURRENT_PAGE_LASTEPISO = "nextpage";
    public static final String PLAYER_FROM_BOTTOM_EPISO_TAG_CLICK = "bottomTagClick";

    // 播放器进度条拖动、点击下一集、-》更新剧集
    public static final String PLAYER_FROM_SEEKBAR_SCROLL_NEXT_EPISO = "scrollNext";
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxee6e741e4aa127e4";
    public static final String APP_SERCRET = "63d911fb0425499c5517454398da6abb";

    // 修改资料界面启动模式
    public static final int Modify_Nick = 0;
    public static final int Modify_Image = 2;
    public static final int Modify_Gender = 1;
    // 加载本地图片
    public static final int LOAD_END = 1;

    public static final String RESULT_OK = "200";

    // 下载剧集面板来源
    public static final int From_HalfPlayer_Down = 0;
    public static final int From_More_Down = 1;

    /*
     *Umeng数据统计上报
     * */
    public static final String FEED_UP_LOAD = "feed_up_load";
    public static final String FEED_DOWN_LOAD = "feed_down_load";
    public static final String FEED_SHARE_BTN = "feed_share_btn";
    public static final String FEED_FAV_BTN = "feed_fav_btn";
    public static final String FEED_DEL_BTN = "feed_del_btn";
    public static final String HALFSCREEN_SWITCH = "halfscreen_switch";
    public static final String HALFSCREEN_SHARE_BTN = "halfscreen_share_btn";
    public static final String HALFSCREEN_DOWNLOAD_BTN = "halfscreen_download_btn";
    public static final String HALFSCREEN_COLLECTION_BTN = "halfscreen_collection_btn";
    public static final String HALFSCREEN_COMMENT = "halfscreen_comment";
    public static final String FULLSCREEN_NEXT = "fullscreen_next";
    public static final String FULLSCREEN_BACK = "fullscreen_back";
    public static final String FULLSCREEN_SWITCH = "fullscreen_switch";
    public static final String FULLSCREEN_DOWNLOAD = "fullscreen_download";
    public static final String FULLSCREEN_COLLECTION = "fullscreen_collection";
    public static final String FULLSCREEN_SHARE = "fullscreen_share";
    public static final String FULLSCREEN_EPISODE = "fullscreen_episode";
    public static final String SEARCH_BTN = "search_btn";
    public static final String SEARCH_KEYBOARD = "search_keyboard";
    public static final String SEARCH_VOICE = "search_voice";
    //渠道名称启动的时候赋值
    public static String CHANNEL_NAME = "0";

    //直播频道id
    public static final String LIVE_SPORT = "1";
    public static final String LIVE_MUSIC = "2";
    public static final String LIVE_ENTERTAINMENT = "3";
    public static final String LIVE_TELEVISION = "4";
    public static final String LIVE_OTHER = "5";
    //直播小红点key值
    public static final String LIVE_PUSH_KEY = "live_push_key";

    //Umeng页面访问路劲上报 页面名称定义

    public final static String  AND_RECMAND = "And_recmand";
    public final static String  AND_SEARCH_SIRI = "And_search_siri";
    public final static String  AND_SEARCH_RESULT = "And_search_result";
    public final static String  AND_SEARCH_INPUTWORD = "And_search_inputword";
    public final static String  AND_SIDE_VIEW = "And_side_view";
    public final static String  AND_PLAY_HISTORY = "And_play_history";
    public final static String  AND_TOPIC = "And_topic";
    public final static String  AND_TOPIC_DETAIL = "And_topic_detail";
    public final static String  AND_RANK = "And_rank";
    public final static String  AND_RANK_DETAIL = "And_rank_detail";
    public final static String  AND_TELEPLAY = "And_teleplay";
    public final static String  AND_MOVIE = "And_movie";
    public final static String  AND_CARTOON = "And_cartoon";
    public final static String  AND_VARIETY = "And_variety";
    public final static String  AND_RECORD = "And_record";
    public final static String  AND_LOGIN = "And_login";
    public final static String  AND_FAVOR = "And_favor";
    public final static String  AND_MESSAGE = "And_message";
    public final static String  AND_DOWNLOAD = "And_download";
    public final static String  AND_DOWNLOADING = "And_downloading";
    public final static String  AND_HALF_PLAY = "And_half_play";
    public final static String  AND_FULL_PLAY = "And_full_play";
    public final static String  AND_SHARE_VIEW = "AND_SHARE_VIEW";
    public final static String  AND_PLAY_H5 = "And_play_h5";
    public final static String  AND_EDIT_USERINFO = "And_edit_userinfo";
    public final static String  AND_SETTING = "And_setting";
    public final static String  AND_ABOUT_US = "And_about_us";
    public final static String  AND_EDIT_HEAD_PORTRAIT = "And_edit_head_portrait";
    public final static String  AND_EDIT_NICKNAME = "And_edit_nickname";
    public final static String  AND_EDIT_SEX = "And_edit_sex";
    public final static String  AND_LIVE = "And_live";
    public final static String  AND_REGISTER = "And_register";
    public final static String  ANDROID_EDIT_USERINFO = "Android_edit_userinfo";
    public final static String  SEARCHACTIVITY = "SearchActivity";

    public interface HttpRequestStatus {
        String STATUSCODE = "200";
        String STATUS = "status";
    }

    public interface ThirdpartySecret {
        public static final String START = "1@3$545";
        public static final String END = "1@3$545";


    }

    public interface FavoriteConstant {

        public static final String TYPE_SPECIAL = "4";
        public static final String TYPE_SINGLE = "2";
        public static final String TYPE_ALBUM = "1";


    }

    public interface SarrsMenuInitMode {

        public static final int MODE_DELETE = 0;
        public static final int MODE_DELETE_SAVE_SHARE = 1;

    }

    /**
     * 收藏入口跳到登陆界面
     */
    public interface SaveJumpTologin {

        public static final int VIDEOTAIL_SAVE_LOGIN = 0;
        public static final int SPECIAL_LOGIN = 1;
        public static final int MAINSAVE_LOGIN = 2;
        public static final int MEDIA_LOGIN = 3;

    }


    public interface ScreenConstants {

        public static final String SCREEN_WIDTH = "width";
        public static final String SCREEN_HEIGHT = "height";
    }

    // reqTag
    public static final String reqTag = "reqTag";

    public interface JumpToVideoDetailMode {
        public static final String FROM_DOWNLOAD = "download";
        public static final String FROM_OTHER = "other";

        // 从share等第三方跳到半屏页
        public static final String FROM_SCHEMA_ALBUM = "sarrs1";
        public static final String FROM_SCHEMA_SINGLE = "sarrs2";
    }

    public interface DataType {
        public static final String ALBUM = "1";
        public static final String SINGLE = "2";

    }

    public static final String SliddingMenuInit = "init";

    /**
     * 防盗链返回 播放格式 os-type 对应  ANDROID ： MP4    IOS ：M3U8
     */
    public interface OutSiteDateType {
        public static final String M3U8 = "IOS";
        public static final String MP4 = "ANDROID";

    }

    public interface ClarityType {

        public static final String REAL = "REAL";
        public static final String SUPER = "SUPER";
        public static final String SUPER2 = "SUPER2";
        public static final String HIGH = "HIGH";
        public static final String NORMAL = "NORMAL";

    }

    /**
     * 播放清晰度优先级
     */

    public interface Priority {
        public static final int REAL_M3U8 = 1;
        public static final int REAL_MP4 = 2;
        public static final int SUPER_M3U8 = 3;
        public static final int SUPER_MP4 = 4;
        public static final int SUPER2_M3U8 = 5;
        public static final int SUPER2_MP4 = 6;
        public static final int HIGH_M3U8 = 7;
        public static final int HIGH_MP4 = 8;
        public static final int NORMAL_M3U8 = 9;
        public static final int NORMAL_MP4 = 10;
    }


}
