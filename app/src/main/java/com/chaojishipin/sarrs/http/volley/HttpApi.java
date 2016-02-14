package com.chaojishipin.sarrs.http.volley;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.AddComment;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CancelFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.CloudDiskBean;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.FavoriteInfos;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.InterestRecommend;
import com.chaojishipin.sarrs.bean.LiveDataEntity;
import com.chaojishipin.sarrs.bean.LiveDataInfo;
import com.chaojishipin.sarrs.bean.LiveStreamInfo;
import com.chaojishipin.sarrs.bean.LogOutInfo;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainActivityData;
import com.chaojishipin.sarrs.bean.ModifyInfo;
import com.chaojishipin.sarrs.bean.OutSiteDataInfo;
import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.RankListDetail;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchResultDataList;
import com.chaojishipin.sarrs.bean.SearchResultInfos;
import com.chaojishipin.sarrs.bean.SearchSuggestInfos;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.bean.TopicDetail;
import com.chaojishipin.sarrs.bean.UpdateSnifferInfo;
import com.chaojishipin.sarrs.bean.UpdateUrlInfo;
import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.bean.SingleInfo;
import com.chaojishipin.sarrs.bean.UploadFile;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.UserCommentInfo;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.bean.VerifyCode;
import com.chaojishipin.sarrs.bean.VideoDetailIndex;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayDecodeMananger;
import com.chaojishipin.sarrs.http.parser.AddCommentParser;
import com.chaojishipin.sarrs.http.parser.AddFavoriteParser;
import com.chaojishipin.sarrs.http.parser.CancelFavoriteParser;
import com.chaojishipin.sarrs.http.parser.CheckFavoriteParser;
import com.chaojishipin.sarrs.http.parser.CloudDiskParser;
import com.chaojishipin.sarrs.http.parser.FavoriteInfoParser;
import com.chaojishipin.sarrs.http.parser.HistoryRecordParser;
import com.chaojishipin.sarrs.http.parser.HistoryRecordReponseDataParser;
import com.chaojishipin.sarrs.http.parser.HtmlParser;
import com.chaojishipin.sarrs.http.parser.InterestRecommendParser;
import com.chaojishipin.sarrs.http.parser.LiveDataInfoParser;
import com.chaojishipin.sarrs.http.parser.LiveInfoParser;
import com.chaojishipin.sarrs.http.parser.LiveStreamInfoParser;
import com.chaojishipin.sarrs.http.parser.LoginParser;
import com.chaojishipin.sarrs.http.parser.LogoutParser;
import com.chaojishipin.sarrs.http.parser.MainActivityDataParser;
import com.chaojishipin.sarrs.http.parser.ModifyUserParser;
import com.chaojishipin.sarrs.http.parser.OutSiteDataParser;
import com.chaojishipin.sarrs.http.parser.RankListDetailParser;
import com.chaojishipin.sarrs.http.parser.RankListParser;
import com.chaojishipin.sarrs.http.parser.SearchResultParser;
import com.chaojishipin.sarrs.http.parser.SearchSuggestParser;
import com.chaojishipin.sarrs.http.parser.SearchToplistParser;
import com.chaojishipin.sarrs.http.parser.SingleInfoParser;
import com.chaojishipin.sarrs.http.parser.SlidingMenuLeftParser;
import com.chaojishipin.sarrs.http.parser.TopicDetailParser;
import com.chaojishipin.sarrs.http.parser.TopicParser;
import com.chaojishipin.sarrs.http.parser.UpdateSnifferParser;
import com.chaojishipin.sarrs.http.parser.UpdateUrlParser;
import com.chaojishipin.sarrs.http.parser.UpgradeParser;
import com.chaojishipin.sarrs.http.parser.UpgradinfoParser;
import com.chaojishipin.sarrs.http.parser.UploadFileParser;
import com.chaojishipin.sarrs.http.parser.UserCommentParser;
import com.chaojishipin.sarrs.http.parser.VerifyCodeParser;
import com.chaojishipin.sarrs.http.parser.VideoDetailIndexParser;
import com.chaojishipin.sarrs.http.parser.VideoDetailParser;
import com.chaojishipin.sarrs.http.parser.VideoPlayerGetUrlParser;
import com.chaojishipin.sarrs.http.parser.WeiXinTokenParser;
import com.chaojishipin.sarrs.http.parser.WeiXinUserParser;
import com.chaojishipin.sarrs.http.volley.multipartrequest.MultipartRequest;
import com.chaojishipin.sarrs.http.volley.multipartrequest.MultipartRequestParams;
import com.chaojishipin.sarrs.listener.BaseSarrsResponseListener;
import com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener;
import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.ShareConstants;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.WeiXinToken;
import com.chaojishipin.sarrs.utils.ChannelUtil;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.MD5Utils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.http.bean.LetvBaseBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * api请求对象获取
 */
public class HttpApi extends SarrsBaseHttpApi {

    public static SarrsRequest getRankDetailRequest() {
        HashMap<String, String> params = getBaseParams();
        String url = getHost() + "apilivechannelaction_json.so?pageindex=1&platform=Le123Plat0021&pagesize=10&code=346e5b9d1bd97036&uuid=030e3a8f-c5ec-3dd0-b2ff-b446796bd0f8&channel=WS";
//        params.put("cg", item.getId());
        return createRequest(SarrsRequest.Method.GET, url, new LiveInfoParser(), null, null);
    }

    public static SarrsRequest<SarrsArrayList> getSlidingMenuLeftRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/channellist");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new SlidingMenuLeftParser(), params, null);
    }

    public static SarrsRequest<MainActivityData> getMainActivityDataRequest(Context context, String cid, String area) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_3);
        sb.append("/sarrs/rec");

        HashMap<String, String> params = getBaseParams();
        params.put("cid", cid);
        params.put("area", area);
        // 设备ID
        params.put("lc", Utils.getDeviceId(context));
        params.put("imei", Utils.getPhoneImei());
        // 业务线
        params.put("p", "0");
        //一级平台编号
        params.put("pl1", "0");
        //二级平台编号
        params.put("pl2", "00");
        addVer(params);
        System.out.println("urla:" + sb.toString());
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new MainActivityDataParser(), params, null);
    }

    ;


    protected static HashMap<String, String> getBaseParams() {
        HashMap<String, String> ret = new HashMap<>();
        return ret;
    }

    /*
      *   请求半屏播放页单个视频在专辑中的index
      * */
    public static SarrsRequest<VideoDetailIndex> getVideoDetailIndexRequest(String gvid, String gaid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("gaid", gvid);
        params.put("gvid", gaid);
        addVer(params);
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_1);
        sb.append("/sarrs/getindex");
        String url = sb.toString();
        return createRequest(SarrsRequest.Method.GET, url, new VideoDetailIndexParser(), params, null);
    }

    /**
     * 公共参数
     * 屏幕分辨率在ChaojishipinSplashActivity写文件
     */
    static void addVer(Map<String, String> params) {
        params.put("pl", "1000011");
        params.put("appv", Utils.getClientVersionName());
        params.put("appfrom", ConstantUtils.CHANNEL_NAME);
        params.put("pl1", "0");
        params.put("pl2", "00");
        params.put("appid", "0");
        params.put("clientos", Utils.getSystemVer());
        // 分辨率
        params.put("resolution", SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT, "") + "*" + SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, ""));
        params.put("width", SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, ""));
        // 设备ID TODO 两个字段一样需统一
        params.put("lc", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
        // 设备ID
        params.put("auid", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));

    }

    static void addVer2(Map<String, String> params) {
        params.put("pl", "1000011");
        params.put("appv", Utils.getClientVersionName());
        params.put("appfrom", "0");
        params.put("pl1", "0");
        params.put("pl2", "00");
        params.put("appid", "0");
        params.put("auid", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
    }


    /*
    *   请求半屏播放页
    * */
    public static SarrsRequest<VideoDetailItem> getVideoDetailRequest(String cid, String id, int pn, int index) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("cid", cid);
        params.put("id", id);
        params.put("pn", "" + pn);
        // params.put("index", "" + index);
        addVer(params);
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_1);
        sb.append("/sarrs/detail");
        String url = sb.toString();
        return createRequest(SarrsRequest.Method.GET, url, new VideoDetailParser(), params, null);
    }

    /**
     * 请求获取验证码
     */
    public static SarrsRequest<VerifyCode> getVerifyCodeRequest(String phone_number, String area) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone_number", phone_number);
        params.put("area", area);
        params.put("productid", "1000011");
        addVer(params);
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/smcode");
        String url = sb.toString();
        return createRequest(SarrsRequest.Method.GET, url, new VerifyCodeParser(), params, null);
    }

    /**
     * 登陆
     */
    public static SarrsRequest<BaseUserInfo> LoginRequest(String type, String phone_number, String area, String sm_code, String openid, String user_name, int sex, String img_url, String signature) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone_number", phone_number);
        params.put("area", area);
        params.put("type", type);
        params.put("sm_code", sm_code);
        params.put("openid", openid);
        params.put("user_name", user_name);
        params.put("sex", String.valueOf(sex));
        params.put("img_url", img_url);
        params.put("signature", signature);
        params.put("productid", "1000011");
        addVer(params);
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/login");
        String url = sb.toString();
        return createRequest(SarrsRequest.Method.GET, url, new LoginParser(), params, null);
    }


    /**
     * 退出
     */
    public static SarrsRequest<LogOutInfo> LogoutRequest(String uuid, String token) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uuid);
        params.put("token", token);
        addVer(params);
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/logout");
        String url = sb.toString();
        return createRequest(SarrsRequest.Method.GET, url, new LogoutParser(), params, null);
    }

    /**
     * 搜索页，搜索结果接口
     *
     * @return
     */
    public static SarrsRequest<SearchResultInfos> getSearchResultRequest(String keyWord, int pageIndex, int pageSize, SearchResultInfos infos) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_1);
        sb.append("/sarrs/search");

        HashMap<String, String> params = getBaseParams();
        params.put("wd", keyWord);
        params.put("pn", String.valueOf(pageIndex));
        params.put("ps", String.valueOf(pageSize));
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new SearchResultParser(infos), params, null);
    }

    /**
     * 搜索页，搜索suggest接口
     *
     * @return
     */
    public static SarrsRequest<SearchSuggestInfos> getSearchSuggestRequest(String keyWord) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_1);
        sb.append("/sarrs/suggest");
        HashMap<String, String> params = getBaseParams();
        params.put("q", keyWord);
        params.put("imei", Utils.getPhoneImei());
        if (UserLoginState.getInstance().getUserInfo() != null) {
            params.put("token", "" + UserLoginState.getInstance().getUserInfo().getToken());
        }
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new SearchSuggestParser(), params, null);
    }

    /**
     * 热门搜索接口
     *
     * @return
     */
    public static SarrsRequest getToplistRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_1);
        sb.append("/sarrs/toplist");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new SearchToplistParser(), params, null);
    }

    /**
     * 微信授权Token取用户信息接口
     */


    public static SarrsRequest<BaseUserInfo> getWeixinUserInfo(String access_token, String openid) {
        StringBuilder sb = new StringBuilder();
        String url = "https://api.weixin.qq.com/sns/userinfo";
        sb.append(url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", access_token);
        params.put("openid", openid);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new WeiXinUserParser(access_token), params, null);
    }

    /**
     * 微信授权获取Token接口
     */

    public static SarrsRequest<WeiXinToken> getWeixinAccessToken(String code) {
        StringBuilder sb = new StringBuilder();
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        sb.append(url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", ShareConstants.WEIXIN_APP_ID);
        params.put("secret", ShareConstants.WEIXIN_APP_SECRET);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new WeiXinTokenParser(), params, null);
    }


    /**
     * 修改用户资料接口:nickname不为空则修改nickname， gender不为-1则修改gender，img不为空则修改img，三者选其一
     */

    public static SarrsRequest<ModifyInfo> modifyUserRequest(String token, String nickName, int gender, String img) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/modifyuser");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        if (nickName != null && nickName.length() > 0) {
            params.put("nick_name", nickName);
        } else if (gender != -1) {
            params.put("gender", String.valueOf(gender));
        } else if (img != null && img.length() > 0) {
            params.put("img", img);
        }
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new ModifyUserParser(), params, null);
    }

    /**
     * 退出登陆接口
     */

    public static SarrsRequest<LogOutInfo> logOutrRequest(String token, String uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/logout");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("uuid", uuid);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new LogoutParser(), params, null);
    }

    /**
     * 版本升级接口
     *
     * @return SarrsRequest
     */
    public static SarrsRequest<UpgradeInfo> getUpgradeRequest(String appfrom) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/upgrade");
        HashMap<String, String> params = new HashMap<>();
        addVer(params);
        params.put("appfrom", appfrom);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new UpgradeParser(), params, null);
    }

    /**
     * 获取专题列表接口
     */
    public static SarrsRequest<SarrsArrayList> getTopicRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        HashMap<String, String> params = new HashMap<String, String>();
        sb.append("/sarrs/topiclist");
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new TopicParser(), params, null);
    }

    /**
     * 获取排行榜列表接口
     */
    public static SarrsRequest<SarrsArrayList> getRankListRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/ranklist");
        HashMap<String, String> params = new HashMap<String, String>();
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new RankListParser(), params, null);
    }

    /**
     * 获取专题详情列表接口
     */
    public static SarrsRequest<Topic> getTopicDetailRequest(Context context, String tid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/topic");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("tid", tid);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new TopicDetailParser(), params, null);
    }

    /**
     * 获取排行版详情列表接口
     */
    public static SarrsRequest<RankList> getRankListDetailRequest(String rid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/rank");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("rid", rid);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new RankListDetailParser(), params, null);
    }

    /**
     * 播放器，防盗链接口（letv源资源使用）
     *
     * @param format 新版留地址必须加清晰度，0-normal，1-high，2-super，3-super2，4-real，取多个清晰度用英文格式逗号分隔，如0,1,2
     * @return
     */
    public static SarrsRequest<VStreamInfoList> getPlayUrlRequest(String gvid, String vType, String playid, String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/geturl");

        HashMap<String, String> params = getBaseParams();
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_GVID, gvid);
        params.put("platid", "17");
        params.put("splatid", "1702");
        params.put("vtype", vType);
        params.put("playid", playid);
        params.put("format", format);
        addVer(params);

        String tssType;
        if (ConstantUtils.LeTvBitStreamParam.KEY_DOWNLOAD.equals(playid)) {
            tssType = "no";
        } else {
            // 如果当前不使用系统硬解，则采用m3u8播放
            tssType = PlayDecodeMananger.ismNeedSysDecoder() ? "no" : "ios";
        }
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_TSS, tssType);
        // 执行AES256加密取得lssv字段的加密值
        StringBuilder key_sb = new StringBuilder();
        key_sb.append(gvid);
        key_sb.append(ConstantUtils.LeTvBitStreamParam.KEY_GETPLAYURL_KEY);
        key_sb.append("17");
        LogUtil.d("dyf", "letv=====");
        LogUtil.d("dyf", "gvid=====" + gvid);
        LogUtil.d("dyf", "加密前key" + key_sb.toString());
        String key = MD5Utils.md5(key_sb.toString());
        LogUtil.d("dyf", "加密前后key" + key);
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_KEY, key);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new VideoPlayerGetUrlParser(), params, null);
    }

    /**
     * 播放器，防盗链接口（云盘源资源使用）
     *
     * @return
     */
    public static SarrsRequest<CloudDiskBean> getPlayUrlCloudRequest(String gvid, String vType, String playid, String type, String uniqueId, String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/geturl");

        HashMap<String, String> params = getBaseParams();
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_GVID, gvid);
        params.put("platid", "17");
        params.put("splatid", "1409"); //网盘源点播客户端不需要传该字段，代理层会返回splatid且值为1409
        params.put("vtype", vType);
        params.put("type", type);
        params.put("format", format);
        params.put("unique_id", uniqueId);
        addVer(params);
        // 执行AES256加密取得lssv字段的加密值
        StringBuilder key_sb = new StringBuilder();
        key_sb.append(gvid);
        key_sb.append(ConstantUtils.LeTvBitStreamParam.KEY_GETPLAYURL_KEY);
        key_sb.append("17");
        LogUtil.d("dyf", "云盘=====");
        LogUtil.d("dyf", "gvid=====" + gvid);
        LogUtil.d("dyf", "加密前key" + key_sb.toString());
        String key = MD5Utils.md5(key_sb.toString());
        LogUtil.d("dyf", "加密前后key" + key);
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_KEY, key);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new CloudDiskParser(), params, null);
    }


    /**
     * 播放器，防盗链接口（除云盘外其他外网资源资源使用）---旧版本
     *
     * @return
     */
    public static SarrsRequest<SingleInfo> requestSingleInfo(String gvid, String vType, String playId) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/geturl");

        HashMap<String, String> params = getBaseParams();
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_GVID, gvid);
        params.put("platid", "17");
        params.put("splatid", "1701");
        params.put("vtype", vType);
        params.put("playid", playId);
        addVer(params);
        StringBuilder key_sb = new StringBuilder();
        key_sb.append(gvid);
        key_sb.append(ConstantUtils.LeTvBitStreamParam.KEY_GETPLAYURL_KEY);
        key_sb.append("17");
        LogUtil.d("dyf", "除云盘外网=====");
        LogUtil.d("dyf", "gvid=====" + gvid);
        LogUtil.d("dyf", "加密前key" + key_sb.toString());
        String key = MD5Utils.md5(key_sb.toString());
        LogUtil.d("dyf", "加密前后key" + key);
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_KEY, key);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new SingleInfoParser(), params, null);
    }

    /**
     * 播放器，防盗链接口（除云盘外其他外网资源资源使用）---新版本
     *
     * @param vType  //码流类型，支持同时返回多个码流信息，用英文逗号分隔，SARRS传252022,252021,252009
     * @param playId //播放类型：0:点播 1:直播 2:下载
     * @return
     */
    public static SarrsRequest<OutSiteDataInfo> requestOutSiteData(String gvid, String vType, String playId, String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/geturl");

        HashMap<String, String> params = getBaseParams();
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_GVID, gvid);
        params.put("platid", "17");
        params.put("splatid", "1701");
        params.put("vtype", vType);
        params.put("playid", playId);
        params.put("format", format);
        addVer(params);
        StringBuilder key_sb = new StringBuilder();
        key_sb.append(gvid);
        key_sb.append(ConstantUtils.LeTvBitStreamParam.KEY_GETPLAYURL_KEY);
        key_sb.append("17");
        LogUtil.d("dyf", "除云盘外网=====");
        LogUtil.d("dyf", "gvid=====" + gvid);
        LogUtil.d("dyf", "加密前key" + key_sb.toString());
        String key = MD5Utils.md5(key_sb.toString());
        LogUtil.d("dyf", "加密前后key" + key);
        params.put(ConstantUtils.LeTvBitStreamParam.KEY_KEY, key);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new OutSiteDataParser(), params, null);
    }

    /**
     * 流地址播放失败后 更新流地址接口
     *
     * @param playUrl 流地址
     * @param format  清晰度，0-normal，1-high，2-super，3-super2，4-real
     * @param eid     新版外网流地址接口会返回该字段，上报时回传，用于统计播放失败率
     *                （iOS和Android流地址的两个eid中选择一个上报即可）
     */
    public static SarrsRequest<UpdateUrlInfo> updateUrl(String playUrl, String format, String eid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/streamupdate");
        HashMap<String, String> params = getBaseParams();
        params.put("playurl", playUrl);
        params.put("format", format);
        params.put("eid", eid);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new UpdateUrlParser(), params, null);
    }


    /**
     * 播放器，获取接口规则（除云盘外其他外网资源资源使用）
     *
     * @return
     */
    public static SarrsRequest<HtmlDataBean> requestJsCode(String jsVer) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMAIN_8);
        sb.append("/sarrs/extractjs");

        HashMap<String, String> params = getBaseParams();
        params.put("jsv", jsVer);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new HtmlParser(), params, null);
    }

    /**
     * 播放器获取js，接口规则更新（除云盘外其他外网资源资源使用）
     *
     * @return
     */
    public static SarrsRequest<UpdateSnifferInfo> udpateJsCode(String jsVer) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMAIN_8);
        sb.append("/sarrs/extractjs");
        HashMap<String, String> params = getBaseParams();
        params.put("jsv", jsVer);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new UpdateSnifferParser(), params, null);
    }

    /**
     * 获取评论接口
     *
     * @param vid
     * @param token
     * @param hot
     * @param cursor
     * @param forward
     * @param device
     * @return
     */
    public static SarrsRequest<UserCommentInfo> getUserCommentInfoRequest(String vid, String token, int hot, long cursor, int forward, String device) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/getcomment");
        HashMap<String, String> params = new HashMap<>();
        params.put("vid", vid);
        params.put("token", token);
        params.put("hot", String.valueOf(hot));
        params.put("cursor", String.valueOf(cursor));
        params.put("forward", String.valueOf(forward));
        params.put("device", device);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new UserCommentParser(), params, null);
    }

    /**
     * 添加评论接口
     *
     * @param token
     * @param type
     * @param text
     * @param soundUrl
     * @param soundSecond
     * @param toCommentId
     * @param playTime
     * @param device
     * @return
     */
    public static SarrsRequest<AddComment> addCommentRequest(String token, int type, String vid, String text, String soundUrl, int soundSecond, long toCommentId, int playTime, String device) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/addcomment");
        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("type", String.valueOf(type));
        params.put("vid", vid);
        params.put("text", text);
        params.put("soundUrl", soundUrl);
        params.put("soundSecond", String.valueOf(soundSecond));
        params.put("toCommentId", String.valueOf(toCommentId));
        params.put("playTime", String.valueOf(playTime));
        params.put("device", device);
        addVer(params);
        return createRequest(SarrsRequest.Method.POST, sb.toString(), new AddCommentParser(), params, null);
    }

    /**
     * 请求兴趣推荐接口
     *
     * @param token
     * @return
     */
    public static SarrsRequest<InterestRecommend> getInterestRecommendRequest(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_3);
        sb.append("/sarrs/interestsurvey");
        HashMap<String, String> params = new HashMap<>();
        params.put("area", ConstantUtils.INTEREST_COMMENT_AREA);
        params.put("token", token);
        params.put("num", "10");
        params.put("lc", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
        params.put("p", "0");
        params.put("pl1", "0");
        params.put("pl2", "00");
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new InterestRecommendParser(), params, null);
    }

//    /**
//     * 上传播放记录接口（除云盘外其他外网资源资源使用）
//     *
//     * @return
//     */
//    public static SarrsRequest<HtmlDataBean> uploadHistoryRecord(String token,String json) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(ConstantUtils.HOST.DOMON_5);
//        sb.append("/sarrs/batchplayrecord");
//        HashMap<String, String> params = getBaseParams();
//        params.put("token", token);
//        params.put("json", json);
//        addVer(params);
//        return createRequest(SarrsRequest.Method.POST, sb.toString(), new HtmlParser(), params,null);
//    }

    /**
     * 判断是否收藏
     *
     * @param id 单视频时为gvid，专辑为aid 专题为 tid
     */
    public static SarrsRequest<CheckFavorite> checkFavorite(String id, String token, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/checkcollection");
        HashMap<String, String> params = getBaseParams();
        params.put("token", token);
        params.put("id", id);
        params.put("type", type);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new CheckFavoriteParser(), params, null);
    }

    /**
     * 添加收藏
     *
     * @param nt 网络类型
     * @param id 单视频时为gvid，专辑为aid 专题为 tid
     */
    public static SarrsRequest<AddFavorite> addFavorite(String id,
                                                        String token,
                                                        String type,
                                                        String cid,
                                                        String nt,
                                                        String source,
                                                        String bucket,
                                                        String seid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/addcollection");
        HashMap<String, String> params = getBaseParams();
        params.put("token", token);
        params.put("id", id);
        params.put("type", type);
        params.put("cid", cid);
        params.put("nt", nt);
        params.put("source", source);
        params.put("bucket", bucket);
        params.put("seid", seid);
        params.put("p", "0");
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new AddFavoriteParser(), params, null);
    }

    /**
     * 取消收藏
     */
    public static SarrsRequest<CancelFavorite> cancelFavorite(String id, String token, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/removecollection");
        HashMap<String, String> params = getBaseParams();
        params.put("token", token);
        params.put("id", id);
        params.put("type", type);
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new CancelFavoriteParser(), params, null);
    }

    /**
     * 获取收藏列表
     */
    public static SarrsRequest<FavoriteInfos> getFavoriteList(String token, int start, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/collectionlist");
        HashMap<String, String> params = getBaseParams();
        params.put("token", token);
        params.put("start", start + "");
        params.put("limit", limit + "");
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new FavoriteInfoParser(), params, null);
    }


    /**
     * 批量同步收藏
     *
     * @action 0:添加  1删除
     */
    public static void batchFavoriteList(String token, String json, BaseSarrsResponseListener lis) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/batchcollection?token=" + token + "&appv=" + Utils.getClientVersionName() + "&pl=1000011&appfrom=0&pl1=0&pl2=00&appid=0&auid=" + Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
        LogUtil.e("wulianshu", "收藏批量上传 json： " + json);
        MyJsonRequest jsonRequest = new MyJsonRequest(Request.Method.POST, sb.toString(), json, lis, lis);
        HttpManager.getInstance().mQueue.add(jsonRequest);
        // addVer(params);  http://user.chaojishipin.com/sarrs/batchcollection?token=2J3g14y-Roc6e9TKomGQgu0
       /* HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");*/
        // return createRequest(SarrsRequest.Method.POST, sb.toString(), new BatchFavoriteParser(), params,headers);
    }


    /**
     * 上传播放记录接口（除云盘外其他外网资源资源使用）
     *
     * @return
     */
    public static void uploadHistoryRecord(String token, String json, UpoloadHistoryRecordListener listener) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/batchplayrecord");
        sb.append("?token=" + token + "&pl2=00&appv=" + Utils.getClientVersionName());

//        Map<String,String> map = new HashMap<String,String>();
//        try {
//            jsonObject.put("json",json);
//
//        }catch (Exception e){
//
//        }


//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sb.toString(), jsonObject,listener ,listener);
        MyJsonRequest jsonRequest = new MyJsonRequest(Request.Method.POST, sb.toString(), json, listener, listener);
        HttpManager.getInstance().mQueue.add(jsonRequest);
    }


    /**
     * 上传播放记录接口（除云盘外其他外网资源资源使用）
     *
     * @return
     */
    public static SarrsRequest<HistoryRecordResponseData> uploadHistoryRecordoneRecord(String token, UploadRecord historyRecord) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/syncplayrecord");
        HashMap<String, String> params = getBaseParams();

        params.put("source", historyRecord.getSource());
        params.put("id", historyRecord.getVid());
        params.put("token", token);
        params.put("playtime", historyRecord.getPlayTime() + "");
        params.put("aid", historyRecord.getPid());
        params.put("cid", historyRecord.getCid() + "");
        params.put("duration", historyRecord.getDurationTime() + "");

        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new HistoryRecordReponseDataParser(), params, null);
    }

    /**
     * 获取播放记录列表接口
     */
    public static SarrsRequest<SarrsArrayList> getHistoryRecordList(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_5);
        sb.append("/sarrs/playlist");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("start", "0");
        params.put("limit", "50");
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new HistoryRecordParser(), params, null);
    }

    /**
     * 上传头像
     */
    public static SarrsRequest<UploadFile> uploadFile(String filePath) {
        Uri uri = Uri.parse(filePath);
        String name = uri.getLastPathSegment();
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/fileupload");
        MultipartRequestParams params = new MultipartRequestParams();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(uri.getPath());
            params.put("upload", inputStream, name, "multipart/form-data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new MultipartRequest(Request.Method.POST, params, sb.toString(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
//                Log.d("multipart", error.getMessage());
            }
        }, new UploadFileParser());
    }

    public static SarrsRequest<SarrsArrayList> getUpgradinfo(String appfrom) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_2);
        sb.append("/sarrs/upgrade");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("appfrom", appfrom);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new UpgradinfoParser(), params, null);
    }

    /**
     * js截流成功上报
     * @param playurl
     * @param stream
     * @param format
     * @return
     */
    public static SarrsRequest<SarrsArrayList> streamUpload(String playurl,String stream,String format){
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/streamupload");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("playurl", playurl);
        params.put("stream", stream);
        params.put("format",format);
        return createRequest(SarrsRequest.Method.GET, sb.toString(),null, params, null);
    }

    /**
     * @return
     */
    public static SarrsRequest<SarrsArrayList> playfeedBack(String playurl,int state,int type,String source,String aid,int waiting){
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/playfeedback");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("playurl", playurl);
        params.put("state", state+"");
        params.put("download",type+"");
        params.put("aid",aid);
        params.put("source",source);
        params.put("waiting",waiting+"");
        return createRequest(SarrsRequest.Method.GET, sb.toString(),null, params, null);
    }

    /**
     *
     * @param token  用户的token
     * @param object 上传的实体
     * @param pageid 当前页面id
     * @param ref  来源页面id（之前页面的id）
     * @param rank 点击的位置
     */

    public static void click_stat(String token, Object object,String acode,String pageid,String ref,String rank,String rid_topcid,@Nullable String sa,String pn,String input,String gvid) {

        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_9);
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("ver", "1.0");
        //不同上报需要改
        params.put("p", "0");
        params.put("uid", token);
        params.put("acode", acode);//点击
        params.put("extend", "-");
        params.put("pageid", pageid);
        params.put("ref", ref);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String act_time = simpleDateFormat.format(new Date());
        LogUtil.e("wulianshu", act_time);
        params.put("act_time", act_time);
        params.put("aeid", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()) + System.currentTimeMillis());
        params.put("r", System.currentTimeMillis() + "");
        if (object instanceof MainActivityAlbum) {
            params.put("type", "action");
            MainActivityAlbum mainActivityAlbum = (MainActivityAlbum) object;
            params.put("cid", mainActivityAlbum.getCategory_id());
            params.put("aid", mainActivityAlbum.getId());
            params.put("vid",gvid);
            params.put("seid",mainActivityAlbum.getReId());
            if(mainActivityAlbum.getVideos() !=null && mainActivityAlbum.getVideos().get(0)!=null && !TextUtils.isEmpty(mainActivityAlbum.getVideos().get(0).getArea_name())){
                params.put("area",mainActivityAlbum.getVideos().get(0).getArea_name());
            }else{
                params.put("area","-");
            }

            params.put("bucket", mainActivityAlbum.getBucket());
            params.put("rank", rank);
            params.put("topic_id", "-");
            params.put("ranklist_id", "-");
            params.put("live_id", "-");
        } else if (object instanceof RankListDetail) {
            params.put("type", "action");
            RankListDetail rankListDetail = (RankListDetail) object;
            params.put("cid", rankListDetail.getCategory_id());
            params.put("aid", rankListDetail.getGaid());
            params.put("vid", rankListDetail.getVideos().get(0).getGvid());
            params.put("seid", "-");
            params.put("area", "-");
            params.put("bucket", "-");
            params.put("rank", rank);
            params.put("topic_id", "-");
            params.put("ranklist_id", rid_topcid);
            params.put("live_id", "-");
        } else if (object instanceof TopicDetail) {
            params.put("type", "action");
            TopicDetail topicDetail = (TopicDetail) object;
            params.put("cid", topicDetail.getCategory_id() + "");
            params.put("aid", topicDetail.getGaid());
            params.put("vid", topicDetail.getVideos().get(0).getGvid());
            params.put("seid", "-");
            params.put("area", "-");
            params.put("bucket", "-");
            params.put("rank", rank);
            params.put("topic_id", rid_topcid);
            params.put("ranklist_id", "-");
            params.put("live_id", "-");
        } else if (object instanceof HistoryRecord) {
            params.put("type", "action");
            HistoryRecord historyRecord = (HistoryRecord) object;
            params.put("cid", historyRecord.getCategory_id());
            params.put("aid", historyRecord.getId());
            params.put("vid", historyRecord.getGvid());
            params.put("seid", "-");
            params.put("area", "-");
            params.put("bucket", "-");
            params.put("rank", rank);
            params.put("topic_id", "-");
            params.put("ranklist_id", "-");
            params.put("live_id", "-");
        } else if (object instanceof Favorite) {
            params.put("type", "action");
            Favorite favorite = (Favorite) object;
            params.put("cid", favorite.getCid());
            params.put("aid", favorite.getAid());
            params.put("vid", favorite.getGvid());
            params.put("seid", "-");
            params.put("area", "-");
            params.put("bucket", "-");
            params.put("rank", rank);
            params.put("topic_id", "-");
            params.put("ranklist_id", "-");
            params.put("live_id", "-");
        } else if (object instanceof VideoDetailItem) {
            params.put("type", "action");
            VideoDetailItem videoDetailItem = (VideoDetailItem) object;
            params.put("cid", videoDetailItem.getCategory_id());

            if (!TextUtils.isEmpty(videoDetailItem.getId())) {
                params.put("aid", videoDetailItem.getId());
            }
            params.put("vid", videoDetailItem.getVideoItems().get(0).getGvid());
            if (!TextUtils.isEmpty(videoDetailItem.getReid())) {
                params.put("seid", videoDetailItem.getReid());
            }
            if (!TextUtils.isEmpty(videoDetailItem.getArea_name())) ;
            {
                params.put("area", videoDetailItem.getArea_name());
            }
            params.put("bucket", videoDetailItem.getBucket());
            params.put("rank", rank);
            params.put("topic_id", "-");
            params.put("ranklist_id", "-");
            params.put("live_id", "-");
        } else if (object instanceof SearchResultInfos) {
            SearchResultInfos searchResultInfos = (SearchResultInfos) object;
//            params.put("cid",mainActivityAlbum.getCategory_id());
//            params.put("aid", mainActivityAlbum.getId());
//            params.put("vid",mainActivityAlbum.getVideos().get(0).getGvid());
            params.put("seid", searchResultInfos.getReid());
//          params.put("bucket", searchResultInfos.getBucket());

            try {
                String kw = URLEncoder.encode(searchResultInfos.getSearch_word(), "UTF-8");
                params.put("kw", kw);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put("pn", pn);
            params.put("sa", sa);
            params.put("input", input);
            params.put("type", "search");
            String aid = "-";
            String vid = "-";
            String cid = "-";
            String sid = "-";
            String lid = "-";
            String gid = "-";
            if ("1".equalsIgnoreCase(sa)) {
                int irank = Integer.parseInt(rank);
                params.put("pos", rank);
                SearchResultDataList searchResultDataList = (SearchResultDataList) searchResultInfos.getItems().get(irank);

                if (searchResultDataList != null) {
                    if (!TextUtils.isEmpty(searchResultDataList.getId())) {
                        aid = searchResultDataList.getId();
                    }

                    if (searchResultDataList.getVideos() != null && searchResultDataList.getVideos().size() > 0 && !TextUtils.isEmpty(searchResultDataList.getVideos().get(0).getGvid())) {
                        vid = searchResultDataList.getVideos().get(0).getGvid();
                    }
                    if (!TextUtils.isEmpty(searchResultDataList.getCategory_id())) {
                        cid = searchResultDataList.getCategory_id();
                    }

                }

            } else {
                params.put("pos", rank);
            }

            String extend = aid + "_" + vid + "_" + cid + "_" + sid + "_" + lid + "_" + gid;


            params.put("extend", extend);
        }
        createRequest(SarrsRequest.Method.GET, sb.toString(), null, params, null).start(null, ConstantUtils.REQUEST_UPLOAD_STAT);
    }

    public static void play_stat(Object object, String token, String ac, String ut, String retry, String play_type, String code_rate, String ref, String timing, String vlen, String seid, String peid,String pageid) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_9);
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("ver", "1.0");
        //不同上报需要改
        params.put("p", "0");
        params.put("uid", token);
        params.put("ac", ac);//点击
        params.put("extend", "-");
        params.put("ref", ref);
        params.put("type", "play");
        if ("0".equalsIgnoreCase(timing)) {
            params.put("timing", "-");
        } else {
            params.put("timing", timing);
        }
        params.put("vlen", vlen);
        params.put("retry", retry);
        params.put("play_type", play_type);
        if ("0".equalsIgnoreCase(ut)) {
            params.put("ut", "-");
        } else {
            params.put("ut", ut);
        }

        params.put("code_rate", code_rate);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String act_time = System.currentTimeMillis() + "";
        LogUtil.e("wulianshu", act_time);
        params.put("ctime", act_time);
        params.put("peid", peid);
        params.put("r", System.currentTimeMillis() + "");
        params.put("live_id", "-");
        params.put("topic_id", "-");
        params.put("ranklist_id", "-");
        params.put("extend", "-");
        params.put("player_version", "1.0.1");
        params.put("ref", ref);
        params.put("seid", seid);
        params.put("pageid", pageid);
        if (object instanceof VideoItem) {
            VideoItem videoItem = (VideoItem) object;
            params.put("aid", videoItem.getId());
            params.put("cid", videoItem.getCategory_id());
            params.put("vid", videoItem.getGvid());
        }else if(object instanceof LiveDataEntity){
            LiveDataEntity liveDataEntity = (LiveDataEntity) object;
            params.put("aid", "-");
            params.put("cid", liveDataEntity.getCid());
            params.put("vid", "-");
            //cid 是4  cid_channalid  其他  cid_id
            if("4".equals(liveDataEntity.getCid())){
                params.put("live_id", liveDataEntity.getCid()+"_"+liveDataEntity.getChannelId());
            }
//            else{
//                params.put("live_id", liveDataEntity.getCid()+"_"+liveDataEntity.getId());
//            }
        }
        createRequest(SarrsRequest.Method.GET, sb.toString(), null, params, null).start(null, ConstantUtils.REQUEST_UPLOAD_STAT);
    }

    /**
     * 直播首页接口
     *
     * @return
     */
    public static SarrsRequest<LiveDataInfo> getLiveChannelDataRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/livehall");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new LiveDataInfoParser(), params, null);
    }

    /**
     * 直播流地址接口
     *
     * @param channelId
     * @return
     */
    public static SarrsRequest<LiveStreamInfo> getLiveStreamUrlRequest(String channelId) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantUtils.HOST.DOMON_6);
        sb.append("/sarrs/livestream");
        HashMap<String, String> params = getBaseParams();
        addVer(params);
        params.put("channelId", channelId);
        params.put("withAllStreams", "1");
        return createRequest(SarrsRequest.Method.GET, sb.toString(), new LiveStreamInfoParser(), params, null);
    }
}
