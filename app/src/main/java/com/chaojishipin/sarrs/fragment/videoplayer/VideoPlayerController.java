package com.chaojishipin.sarrs.fragment.videoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinRegisterActivity;
import com.chaojishipin.sarrs.activity.PlayActivityFroWebView;
import com.chaojishipin.sarrs.adapter.VideoDetailFuScreeenExpandListAdapter;
import com.chaojishipin.sarrs.async.RequestHtmlDataTask;
import com.chaojishipin.sarrs.async.RequestResultListener;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CancelFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.CloudDiskBean;
import com.chaojishipin.sarrs.bean.DownloadEpisodeEntity;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.OutSiteData;
import com.chaojishipin.sarrs.bean.OutSiteDataInfo;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SingleInfo;
import com.chaojishipin.sarrs.bean.UpdateUrlInfo;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.dao.FavoriteDao;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.fragment.ChaoJiShiPinBaseFragment;
import com.chaojishipin.sarrs.http.parser.UpdateUrlParser;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.manager.FavoriteManager;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thread.ThreadPoolManager;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.CDEManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.TrafficStatsUtil;
import com.chaojishipin.sarrs.utils.UpdateSnifferManager;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.SarrsSlideMenuView;
import com.letv.component.player.LetvMediaPlayerControl;
import com.letv.component.player.LetvVideoViewBuilder;
import com.letv.component.player.LetvVideoViewBuilder.Type;
import com.letv.component.player.core.PlayUrl;
import com.letv.pp.func.CdeHelper;
import com.letv.pp.service.CdeService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.greenrobot.event.EventBus;

/**
 * Created by xll on 2015/7/7.
 */
public class VideoPlayerController implements OnClickListener, OnPreparedListener, OnCompletionListener,
        OnTouchListener, OnInfoListener, SarrsSlideMenuView.onSlideMenuItemClick, MediaPlayer.OnErrorListener {

    private final String TAG = this.getClass().getSimpleName();

    private PlayData mPlayData;
    /**
     * 当前播放影片需要用到数据
     */
    private VideoItem mPlayEpisode;
    /**
     * 当前重试的次数
     */
    private int mReplayNum = 0;
    /**
     * 当前影片在剧集列表中的位置
     */
    /**
     * 当前影片在剧集列表中的位置
     * 封装的SparseArray的key和value
     * key:分页的索引
     * value：所在分页的list的index
     */
    private VideoPlayerNotifytData mNotifyData;
    /**
     * 从下载管理传入的播放列表
     */
    private ArrayList<LocalVideoEpisode> mLocalPlayLists;
    /**
     * CDE返回的URL
     */
    private String mLinkShellUrl;

    /**
     * 云盘播放地址
     */
    private ArrayList<String> mCloudPlayUrls;
    /**
     * 云盘影片播放时长
     */
    private int mCloudPlayPosition = 0;
    /**
     * 截流类型
     */
    private String mCurrSinffType = "";
    /**
     * 云盘播放类型
     */
    private String mCurrCloudType;
    private String mVideoName = null;
    private CDEManager mCDEManager;
    private CDEStatusReceiver mCdeStatusReiver;
    private VideoPlayerFragment mVideoPlayerFragment;
    private ChaoJiShiPinVideoDetailActivity mActivity;
    private LetvVideoViewBuilder mVideoViewBuilder;
    private LetvMediaPlayerControl mPlayContorl;

    private ImageView mediacontroller_top_back2;
    public boolean isplayfromweb = true;

    private boolean isLoadingviewvisible = false;

    public LetvMediaPlayerControl getmPlayContorl() {
        return mPlayContorl;
    }

    /**
     * 大数据上报相关
     */
    private String ac="-";
    private long timing=0; //毫秒  已经播放的视频长度
    private long begin_time;
    private long end_time;
    private long ut;//动作耗时 毫秒
    private int vlen;//视频的总长 秒为单位
    private int retry;//重试次数
    private int play_type;//播放类型
    private String code_rate="-";//码流对照表
    private String ref = "-";
    private int repeatcount = 0;
    private boolean isblock = false;

    private String stream = "";

    public boolean isfeedbackalready() {
        return isfeedbackalready;
    }
    public void setIsfeedbackalready(boolean isfeedbackalready) {
        this.isfeedbackalready = isfeedbackalready;
    }

    private boolean isfeedbackalready = false;
    private boolean isjscut = false;
    private boolean isstream = false;

    public int getStarttime() {
        return starttime;
    }

    private int starttime =0;
    /**
     * 当前播放的是否是本地文件
     */
    private boolean mIsPlayLocalUrl = false;
    private SparseArray<ArrayList<VideoItem>> mEpisodes;

    /**
     * 播放器的外围的容器
     */
    private RelativeLayout mVideoViewPosition;
    /**
     * 播放需要用到的View
     */
    private View mPlayerView;

    /**
     * 播放器控制栏view
     */
    private RelativeLayout mMediaController;
    /**
     * 控制栏头部view
     */
    private RelativeLayout mMediaControllerTop;
    /**
     * 控制栏底部view
     */
    private RelativeLayout mMediaControllerBottmon;
    /**
     * loading界面
     */
    private LinearLayout mLoadingLayout;

    private TextView mTitile;
    /**
     * 播放按钮
     */
    private ImageView mPlayBtn;
    /**
     * 锁屏状态按钮
     */
    private ImageButton mLockScreenBtn;
    /**
     * 播放器进度条
     */
    private SeekBar mPlaySeekBar;
    /**
     * 当前播放地址
     */
    private String mPlayUrl = "";
    /**
     * 下一集按钮
     */
    private ImageView mPlayNextBtn;

    /**
     * 选集按钮
     */
    private TextView mSelectBtn;

    /**
     * 剧集面板
     */
    FrameLayout mSelectLinearLayout;

    /**
     * 剧集面板list
     */
    ListView mSelectListView;
    /**
     * 剧集面板adapter
     */
    private VideoDetailFuScreeenExpandListAdapter tagAdapter;

    /**
     * 清晰度码流--传多个码流类型
     */
    private String letvVideoCode = PlayerUtils.PLS_TSS;
    /**
     * 默认播放清晰度为标清
     */
    private String mdefaultClarity = PlayerUtils.VIDEO_MP4_350;
    private String mHighClarity = PlayerUtils.VIDEO_MP4_720_db;
    private String mLowClarity=PlayerUtils.VIDEO_MP4;
    /**
     * 当前是否为暂停状态
     */
    public static boolean mIsPause = true;

    public int getmCurrPlayTime() {
        return mCurrPlayTime;
    }

    public void setmCurrPlayTime(int mCurrPlayTime) {
        this.mCurrPlayTime = mCurrPlayTime;
    }

    /**
     * 影片播放的当前时间点
     */
    private int mCurrPlayTime = 0;

    private String last_gvid="";

    /**
     * 当前播放时间
     */
    private TextView mCurrTime;

    /**
     * 影片总时长
     */
    private TextView mTotalTime;
    /**
     * 显示网速
     */
    private TextView mNetRate;
    /**
     * 影片的总时长
     */
    private long mDuration;
    /**
     * 播放进度条的最大值
     */
    private final long PLAY_SEEKBAR_MAX = 1000L;
    /**
     * 当前播放器是否准备完成可以播放影片
     */
    public boolean mIsPrepared = false;

    /**
     * 判断当前控制台是否展现
     */
    private boolean mIsControllerShow = false;
    /**
     * 是否能播放下一集
     */
    private boolean mCanPlayNext = false;

    protected PlayerTimer mPlayerTimer;
    /**
     * 是否需要判断视频正在缓冲
     */
    private boolean mIsNeddJudgeBuffer = true;

    private Animation mLockScreenAni;
    private Window mWindow;
    /**
     * 断网布局
     */
    private Button controller_net_error_play;
    private RelativeLayout mNetLayout;
    private ImageView videoplayer_net_error_icon;
    private TextView controller_net_error;
    private int progress_change = 0;

    /**
     * js api map
     */

    private HashMap<String, String> mApiMap;

    private HashMap<String, String> mRuleMap;

    private HashMap<String, String> mPlayUrlMap;
   /**
    *   是否需要走本地截流逻辑
    * */
   private boolean isNeedJsCut;


     /**
     * 当前播放状态
     */
    private PlayState mCurrPlayState;

    /**
     * 视频格式  MP4  优先
     */
    private String M3U8 = "m3u8";
    private String Mp4 = "mp4";

    /**
     * 用于请求单集信息的vid
     */
    private String mSingleInfoVid;
    /**
     * 下拉menu功能布局
     */
    private RelativeLayout mSlideMenuLayout;
    private SarrsSlideMenuView mSlideMenu;
    private Button mSlideTrggle;
    private boolean isSlide;
    /**
     * 默认清晰度
     */
    private String mDefaultDefinition = "";

    private String mSnifferUrl = null;
    private OutSiteDataInfo mSingleInfo;
    /**
     *   新版截流服务
     * */


    private OutSiteDataInfo mOutSiteDataInfo;

    public OutSiteData getmOutSiteData() {
        return mOutSiteData;
    }

    private OutSiteData mOutSiteData;
    private List<String> mApi_list;
    private List<String> mStream_list;
    private List<String>mAllowed_formats;
    private List<String>mApi_contentlist;
    //当前截流的清晰度
    private String current_format;
    private String mEid="";
    private String mRule = "";
    private String mOsType="";
    private String mUserAgent="";
    private String mTs="";
    private String mTe="";
    //当前外网播放地址列表对应索引
    private int mStreamIndex;
    // 是否本地截流通过这个判断
    private boolean isHasRule;
    // 退出截流循环变量
    private boolean isQuitSniff;


    /**
     * js 截流webview
     */
    private int mSniffRetryCount = 0;
    private WebView mWebView;
    private TestJavaScriptInterface mTestInterface;
    private String mSnifferParamter = "";
    private String SNIFF_SUCCESS = "success";
    private final int REPLAY_MAX = 3;
    /**
     * 外网视频 规则
     */

    /**
     * Loading 或者 网络状态
     */
    private ProgressBar progressBar;
//  private TextView tv_play;
//  private TextView tv_neterr_tip;
    private TextView loading_tip_text;
    private RelativeLayout video_player_top_id;
    /**
     * 加载超时
     */
    public RelativeLayout videoplayer_load_timeout_layout;
    /**
     * 加载超时重试
     */
    private Button controller_load_timeout_refresh;
    /**
     * Loading time
     */
    private long endTime;
    private long startTime;
    private long duringTime;
    private boolean isForcePlay;


    /**
     * 网络状态
     * -1 : 无任何网络
     * 1： wifi
     * 0：GSM
     */
    private int netType = -1;

    /**
     * 手势
     */
    private GestureDetector gestureDetector;

    /**
     * 快进快退Toast
     */
    private RelativeLayout fast_toast;
    private ImageView fast_icon;
    private TextView now_length;
    private TextView tv_total_length;
    private SeekBar fast_seekbar;
    private ImageView full_screen;
    /**
     *  是否锁屏
     * */
    private boolean isLockScreen;
    /*
       收藏
    * */
    private ImageView ic_loving;

    /**
     * 快进滑动8次全屏屏幕快进完整部电影
     */
    public final int FAST_COUNT = 10;

    private Button controller_net_error_setting;
    FavoriteDao fdao = null;

    public VideoPlayerController(ChaoJiShiPinBaseFragment videoPlayerFragment) {
        isfeedbackalready = false;
        if (videoPlayerFragment instanceof VideoPlayerFragment) {
            mVideoPlayerFragment = (VideoPlayerFragment) videoPlayerFragment;
        }
        if (mVideoPlayerFragment != null) {
            mActivity = mVideoPlayerFragment.getmActivity();
        }
        mVideoViewBuilder = LetvVideoViewBuilder.getInstants();
        initView();
        initData();
        setListener();
    }

    public void destroy(){
        Utils.destroyWebView(mWebView);
        if(mPlayerView != null && mPlayerView.getParent() != null)
            ((ViewGroup)mPlayerView.getParent()).removeView(mPlayerView);
    }

    private void initView() {
        videoplayer_load_timeout_layout = (RelativeLayout) mActivity.findViewById(R.id.videoplayer_load_timeout_layout);
        controller_load_timeout_refresh = (Button) mActivity.findViewById(R.id.controller_load_timeout_refresh);
        controller_net_error_play = (Button) mActivity.findViewById(R.id.controller_net_error_play);
        controller_net_error = (TextView) mActivity.findViewById(R.id.controller_net_error);
        videoplayer_net_error_icon = (ImageView) mActivity.findViewById(R.id.videoplayer_net_error_icon);
        mNetLayout = (RelativeLayout) mActivity.findViewById(R.id.videoplayer_error_layout);
        mSlideMenuLayout = (RelativeLayout) mActivity.findViewById(R.id.tv_tv_video_menu_layout);
        controller_net_error_setting = (Button) mActivity.findViewById(R.id.controller_net_error_setting);
        mSlideMenu = (SarrsSlideMenuView) mActivity.findViewById(R.id.tv_tv_video_menu);
        mSlideTrggle = (Button) mActivity.findViewById(R.id.tv_video_more);
        video_player_top_id = (RelativeLayout) mActivity.findViewById(R.id.video_player_top_id);
        mVideoViewPosition =
                (RelativeLayout) mActivity.findViewById(R.id.videoview_position);
        mMediaControllerTop =
                (RelativeLayout) mActivity.findViewById(R.id.mediacontroller_top);
        mMediaController =
                (RelativeLayout) mActivity.findViewById(R.id.mediacontroller_content);
        mMediaControllerBottmon =
                (RelativeLayout) mActivity.findViewById(R.id.mediacontroller_bottom);
        mediacontroller_top_back2 = (ImageView) mActivity.findViewById(R.id.mediacontroller_top_back2);
        fast_toast = (RelativeLayout) mActivity.findViewById(R.id.fast_toast);
        fast_icon = (ImageView) mActivity.findViewById(R.id.fast_icon);

        now_length = (TextView) mActivity.findViewById(R.id.now_length);
        tv_total_length = (TextView) mActivity.findViewById(R.id.tv_total_length);
        fast_seekbar = (SeekBar) mActivity.findViewById(R.id.fast_seekbar);
        fast_seekbar.setMax((int) PLAY_SEEKBAR_MAX);
        fast_seekbar.setProgress(0);
        fast_seekbar.setSecondaryProgress(0);
        full_screen=(ImageView)mActivity.findViewById(R.id.full_screen);
        mSelectLinearLayout = (FrameLayout) mActivity.findViewById(R.id.mediacontroller_select_layout);
        mSelectListView = (ListView) mActivity.findViewById(R.id.video_player_select_list);
        mLoadingLayout = (LinearLayout) mActivity.findViewById(R.id.layout_loading);
        progressBar = (ProgressBar) mLoadingLayout.findViewById(R.id.loading_iv);
//      tv_neterr_tip = (TextView) mLoadingLayout.findViewById(R.id.tv_neterr_tip);
        mTitile = (TextView) mActivity.findViewById(R.id.tv_video_title);
//      tv_play = (TextView) mLoadingLayout.findViewById(R.id.tv_play);
        loading_tip_text = (TextView) mLoadingLayout.findViewById(R.id.loading_tip_text);
//      tv_play.setOnClickListener(this);
        mPlayBtn = (ImageView) mActivity.findViewById(R.id.btn_play);
        mLockScreenBtn = (ImageButton) mActivity.findViewById(R.id.videoplayer_lockscreen);
        mLockScreenBtn.setVisibility(View.GONE);
        mCurrTime = (TextView) mActivity.findViewById(R.id.tv_currtime);
        mTotalTime = (TextView) mActivity.findViewById(R.id.tv_totaltime);
        mNetRate = (TextView) mActivity.findViewById(R.id.loading_net_rate);
        mPlayNextBtn = (ImageView) mActivity.findViewById(R.id.btn_play_next);
        mSelectBtn = (TextView) mActivity.findViewById(R.id.tv_select);
        mPlaySeekBar = (SeekBar) mActivity.findViewById(R.id.full_play_seekbar);
        mPlaySeekBar.setMax((int) PLAY_SEEKBAR_MAX);
        mPlaySeekBar.setProgress(0);
        mPlaySeekBar.setSecondaryProgress(0);
        startPlayerTimer();
        setLockScreenVisibile(false);
        // js 截流webview
        mTestInterface = new TestJavaScriptInterface();
        initWebView(mActivity);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (mActivity != null) {
            if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE) {
                LogUtil.e(TAG, "Media play online");
                mCDEManager = CDEManager.getInstance(ChaoJiShiPinApplication.getInstatnce());
            } else if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                LogUtil.e(TAG, "Media play local");
                mCDEManager = CDEManager.getInstance(ChaoJiShiPinApplication.getInstatnce());
                mPlayNextBtn.setVisibility(View.VISIBLE);

            }
            mNotifyData = new VideoPlayerNotifytData();
            mLockScreenAni = makeBlinkAnimation(mActivity);
            if (Utils.getAPILevel() >= PlayerUtils.API_14) {
                // 设置虚拟键显示和隐藏的监听
                mWindow = mActivity.getmWindow();
            }

        }
    }

    /**
     * init --save
     */

    public void initSave() {
        FrameLayout liItem = (FrameLayout) mSlideMenu.getChildAt(0);
        if (liItem.getChildAt(1) instanceof LinearLayout) {
            LinearLayout itemL = (LinearLayout) liItem.getChildAt(1);
            LinearLayout childItem = (LinearLayout) itemL.getChildAt(1);
            if (childItem.getChildAt(0) instanceof ImageView) {
                ic_loving = (ImageView) childItem.getChildAt(0);
            }

        }
    }

    /**
     * 设置监听器对象
     */
    private void setListener() {
        controller_load_timeout_refresh.setOnClickListener(this);
        mSlideTrggle.setOnClickListener(this);
        mSlideMenu.setOnSlideItemClick(this);
        controller_net_error_play.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mPlayNextBtn.setOnClickListener(this);
        mLockScreenBtn.setOnClickListener(this);
        mSelectBtn.setOnClickListener(this);
        mSelectLinearLayout.setOnClickListener(this);
        mSelectListView.setOnItemClickListener(new MyOnItemClickListener());
        mPlaySeekBar.setOnSeekBarChangeListener(mPlaySeekBarChangeListener);
        mMediaControllerTop.setOnTouchListener(this);
        mMediaControllerBottmon.setOnTouchListener(this);
        mediacontroller_top_back2.setOnClickListener(this);
        gestureDetector = new GestureDetector(mActivity, new Gesturelistener());
        controller_net_error_setting.setOnClickListener(this);
    }

    private void setPlayerListener() {
        mPlayContorl.setOnPreparedListener(this);
        mPlayContorl.setOnCompletionListener(this);
        // mPlayContorl.setOnBufferingUpdateListener(this);
        mPlayContorl.setOnErrorListener(this);
        mPlayContorl.setOnInfoListener(this);
        mPlayContorl.setOnSeekCompleteListener(mPlaySeekCompleteListener);
        mVideoViewPosition.setOnTouchListener(this);
    }


    void initSaveOnLine() {
        buidlParam();
        // 单视频
        token = UserLoginState.getInstance().getUserInfo().getToken();
        if (TextUtils.isEmpty(item.getId())) {

            id = item.getVideoItems().get(0).getGvid();
            type = "2";
        } else {
            // 专辑
            id = item.getId();
            type = "1";
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id, token, type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0 && result.isExists()) {

                    initSave();
                    if (ic_loving != null) {
                        mActivity.setIsSave(true);
                        ic_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
                    }

                } else {

                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });

    }


    /**
     * 下拉menu点击item 回调  下载、收藏、分享
     */
    @Override
    public void onItemClick(int position, View view) {

        if (isSlide) {
            switch (position) {
                case 0:
                    if(NetWorkUtils.isNetAvailable()){
                        Log.i("menu click", "click down" + position);
                        EventBus.getDefault().post(new DownloadEpisodeEntity(mPlayData.getKey(), mPlayData.getIndex(), mPlayData.getmEpisodes()));
                        //Umeng统计上报
                        MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_DOWNLOAD);
                    }else{
                        Toast.makeText(mActivity, mActivity.getString(R.string.nonet_tip), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1:
                    //收藏
                    if(NetWorkUtils.isNetAvailable()){
                        if (UserLoginState.getInstance().isLogin()) {
                            buidlParam();
                            checkSave(true);
                        } else {
                            mActivity.startActivityForResult(new Intent(mActivity, ChaojishipinRegisterActivity.class), ConstantUtils.SaveJumpTologin.MEDIA_LOGIN);
                        }
                        MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_COLLECTION);
                    }else{
                        Toast.makeText(mActivity, mActivity.getString(R.string.nonet_tip), Toast.LENGTH_SHORT).show();
                    }

                    Log.i("menu click", "click save" + position);
                    break;
                case 2:
                    if(NetWorkUtils.isNetAvailable()){
                        Log.i("menu click", "click share" + position);
                        // 上报需先设置参数
                        buidlParam();
                        EventBus.getDefault().post(item);
                        DataReporter.reportAddShare(id, source, cid, type, token, nt, "" + bucket, "" + reid);
                        MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_SHARE);
                    }else{
                        Toast.makeText(mActivity, mActivity.getString(R.string.nonet_tip), Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
            hideSlideMenu();
        }
        LogUtil.e("Slide ", "" + position);

    }


    /**
     * 构造 添加、是否存在、取消收藏统一参数
     */
    String id = "";
    String token = UserLoginState.getInstance().getUserInfo().getToken();
    String type = "";
    String cid = "";
    String source="";

    String bucket="";
    String reid="";

    VideoItem item = null;
    LocalVideoEpisode localItem;
    String nt = NetWorkUtils.getNetInfo();
    // 上报参数
    void buidlParam() {

        if(mActivity.getData()!=null){
            bucket=mActivity.getData().getBucket();
            reid=mActivity.getData().getReid();
        }
        ArrayList<VideoItem> items=null;
            //  本地在线剧集格式一致
            items = mPlayData.getmEpisodes().get(mPlayData.getKey());
            item = items.get(mPlayData.getIndex());
            source = item.getSource() + "";
            cid = item.getCategory_id();
            if (TextUtils.isEmpty(item.getId())) {
                id = item.getGvid();
                type = "2";
            } else {
                // 专辑
                id = item.getId();
                type = "1";
            }

    }

    /**
     * cancel save
     */
    void cancelSaveOnLine() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_CANCEL_FAVORITE);
        HttpApi.cancelFavorite(id, token, type).start(new RequestListener<CancelFavorite>() {
            @Override
            public void onResponse(CancelFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0) {
                    mActivity.setIsSave(false);
                    ic_loving.setImageResource(R.drawable.vedio_detail_loving_normal);
                    ToastUtil.showShortToast(mActivity, mActivity.getString(R.string.save_cancel));
                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }


    /**
     * save onLine
     */
    void doSaveOnLine() {
        if (TextUtils.isEmpty(item.getId())) {
            id = item.getGvid();
            type = "2";
        } else {
            // 专辑
            id = item.getId();
            type = "1";
        }

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ADD_FAVORITE);
        HttpApi.addFavorite(id, token, type, cid, nt, source, "" + bucket, "" + reid).start(new RequestListener<AddFavorite>() {
            @Override
            public void onResponse(AddFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0) {
                    mActivity.setIsSave(true);
                    ic_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
                    ToastUtil.showShortToast(mActivity, mActivity.getString(R.string.save_success));
                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }




    /**
     * 点击收藏按钮
     */
  public  void checkSave(final boolean isByClick) {
        initSave();
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id, token, type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0 && result.isExists()) {
                    if (isByClick) {
                        cancelSaveOnLine();
                    }

                } else {
                    doSaveOnLine();
                    //上报
                    // doReport();
                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }

    /**
     * 上报 收藏
     */

    void doReport() {
        DataReporter.reportAddCollection(id, source, cid, type, token, nt);
    }

    void showSlideMenu() {
        if (!isSlide) {
            mSlideMenuLayout.setVisibility(View.VISIBLE);
            isSlide = true;
        }
    }

    void hideSlideMenu() {
        if (isSlide) {
            mSlideMenuLayout.setVisibility(View.GONE);
            isSlide = false;
        }
    }

    public boolean ismIsPause() {
        return mIsPause;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.controller_load_timeout_refresh:
                retry ++;
                hideTimeOutLayout();
                setmPlayData(mPlayData);
                break;
            case R.id.controller_net_error_play:
                if(mPlayData == null) {
                    ToastUtil.showShortToast(mActivity, mActivity.getResources().getString(R.string.play_no_data));
                    return;
                }
                isForcePlay = true;
                LogUtil.e("xll ","Letv isPause : "+mIsPause);
                /*startTime=System.currentTimeMillis();*/
                //updatePlayBtnBg(false);
                mIsPause=true;
                hideNetView();
                clickPauseOrPlay();
                executePrePare();
                break;
            case R.id.tv_video_more:
                if (isSlide) {
                    hideSlideMenu();
                } else {

                    showSlideMenu();
                    if (isSlide) {
                        if(UserLoginState.getInstance().isLogin()&&NetWorkUtils.isNetAvailable()){
                            initSaveOnLine();
                        }

                    }
                }


                break;
            case R.id.btn_play:
                    clickPauseOrPlay();
                    mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
                    mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                            ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
                break;
            case R.id.btn_play_next:
                //播放结束 需要上报（切换剧集）
                showMediaControll();
                mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
                mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                        ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
                playNext();
                MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_NEXT);

                break;
            // 用户点击锁屏按钮
            case R.id.videoplayer_lockscreen:
                boolean isSelected = mLockScreenBtn.isSelected();
                mLockScreenBtn.setSelected(!isSelected);
                // 如果当前是锁屏状态则
                if (isSelected) {
                    showMediaControll();
                } else {
                    dissMissMediaControll();
                }
                if (isShowEpiso) {
                    hideAnimSelect(300);
                }
                if (isSlide) {
                    hideSlideMenu();
                }
                mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                        ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
                break;
            // 剧集
            case R.id.tv_select:
                if (null != mSelectLinearLayout && !mSelectLinearLayout.isShown()) {
                    if (!isShowEpiso) {
                        showAnimSelect();
                    } else {
                        hideAnimSelect(300);
                    }
                    MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_EPISODE);
                }
                break;
            // 点击剧集收起
            case R.id.mediacontroller_select_layout:
                if (isShowEpiso) {
                    hideAnimSelect(300);
                }
                break;
//            case R.id.tv_play:
//                break;
            case R.id.controller_net_error_setting:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                mActivity.startActivity(intent);
                mActivity.startActivityForResult(intent, mActivity.NET_SETTING_REQUEST_CODE);
                break;
            case R.id.mediacontroller_top_back2:
                mVideoPlayerFragment.btBackClick();
                break;
            default:
                break;
        }
    }

    /**
     * 生命周期 reset播放器状态
     */
    public void resetPlaystate(boolean pause) {
        mIsPause = pause;
    }

    /**
     * pause()
     */
    public void pauce() {
        if (mPlayContorl != null) {
            if (mPlayContorl.isPlaying() && mPlayContorl.canPause()) {
                mIsPause = true;
                playerPause();
                stopPlayerTimer();
                mCurrPlayTime = mPlayContorl.getCurrentPosition();
            }
        }

    }

    public void setmIsPause(boolean isPause) {
        this.mIsPause = isPause;
    }

    /**
     * play()
     */
    public void play() {
        mIsPause = false;
        if (mPlayContorl != null) {
            mPlayContorl.start();
        }
        startPlayerTimer();
    }

    /**
     * 点击播放按钮
     */
    public void clickPauseOrPlay() {

        if (null != mPlayContorl) {
            /**
             *  播放器是否在播放（实际状态从这里获取）
             * */
            if (mPlayContorl.isPlaying() && mPlayContorl.canPause()) {
                mIsPause = true;
                playerPause();
                stopPlayerTimer();
                mCurrPlayTime = mPlayContorl.getCurrentPosition();
            } else {
                    mIsPause = false;
                    mPlayContorl.start();
                    startPlayerTimer();
            }
        } else {

            if (mIsPause) {
                if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                    executeLocalChangePlay();
                } else if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE && mPlayData !=null) {
                        executeProcessBySite(mPlayData.getSite());
                }

            }
        }
        updatePlayBtnBg(mIsPause);
    }


    /**
     * 播放器异常回调
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.e(TAG, "Media error call back");
        LogUtil.e("xll", "NEW xll Media error call back");
        mStreamIndex++;
        executeOutSitePlayCore(mStreamIndex);
        // 播放失败通知服务端更新
        requestUpdateStream(mPlayUrl,mFormat,mEid);
        // TODO update stream-list  report
       // handlerOutSiteError();
        return false;
    }

    /**
     * 更新播放按钮背景
     */
    public void updatePlayBtnBg(boolean isPauseState) {
        if (null != mPlayBtn) {
            if (isPauseState) {
                // 暂停状态设置为播放按钮
                mPlayBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_play);
            } else {
                mPlayBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_pause);
            }
        }
    }

    /**
     * 获取当前播放数据
     * daipei 2015年7月20日 10:53:11
     * modify by xll 2015 11 17
     */
    private VideoItem getCurrEpisode() {
        retryCount=0;
        mStreamIndex=0;
         isHasRule=false;
         isQuitSniff=false;
        // 设置当前剧集在播放列表中的位置
        mNotifyData.setKey(mPlayData.getKey());
        mNotifyData.setPosition(mPlayData.getIndex());
        VideoItem episode = null;
        if (null != mEpisodes) {
            ArrayList<VideoItem> episodes = mEpisodes.get(mNotifyData.getKey());
            if (null != episodes && episodes.size() > mNotifyData.getPosition()) {
                episode = episodes.get(mNotifyData.getPosition());
            }
        }
        return episode;
    }
    /**
     *  下载页进入全屏页播放
     *
     * */

   void initLocalFullScreenView(){
       setPlayLoadingVisibile(false, null);
       full_screen.setBackgroundResource(R.drawable.sarrs_pic_small_screen);
       mSlideTrggle.setVisibility(View.VISIBLE);

       mPlayNextBtn.setVisibility(View.VISIBLE);

       if(mPlayData.getmLocalDataLists()!=null&&mPlayData.getmLocalDataLists().size()>1){
           mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_next);
           mSelectBtn.setVisibility(View.VISIBLE);
           LogUtil.e("v1.1.2","play local size>=1 show episo&next btn ");
       }else if(mPlayData.getmEpisodes()!=null&&mPlayData.getmEpisodes().indexOfKey(mPlayData.getKey())>=0&&mPlayData.getmEpisodes().get(mPlayData.getKey()).size()>1){
           mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_next);
           mSelectBtn.setVisibility(View.VISIBLE);
           LogUtil.e("v1.1.2","play local & click online item");
       }else

       {
           mSelectBtn.setVisibility(View.GONE);
           LogUtil.e("v1.1.2", "play local size<1 show episo&next btn ");
           mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_no_next);
       }

       // mpladata //联网、缓存入口进等 时需要根据porder 重新算出 key  position

       if(NetworkUtil.isNetworkAvailable(mActivity)){
           resetIndexKey();
       }



   }


  void resetIndexKey(){
      if(mPlayData!=null&&mPlayData.getmEpisodes()!=null&&mPlayData.getmEpisodes().size()>0){
          String cuPorder=null;
          int cuKey=0;
          int cuPosition=0;
          boolean needBreak=false;
          for(int i=0;i<mPlayData.getmEpisodes().size();i++){

              ArrayList<VideoItem> items=mPlayData.getmEpisodes().get(i);
              if(needBreak){
                  break;
              }
              if(items!=null&&items.size()>0){
                  for(int j=0;j<items.size();j++){
                      VideoItem vitem=items.get(j);
                      if(!TextUtils.isEmpty(vitem.getOrder())){
                          cuPorder=vitem.getOrder();
                      }

                      if(!TextUtils.isEmpty(vitem.getPorder())){
                          cuPorder=vitem.getPorder();
                      }

                      if(!TextUtils.isEmpty(cuPorder)&&cuPorder.equalsIgnoreCase(mPlayData.getPorder())){
                           cuKey=i;
                           cuPosition=j;
                           mPlayData.setKey(cuKey);
                           mPlayData.setIndex(cuPosition);
                           needBreak=true;
                           LogUtil.e("v1.1.2","receive data reset (k,v)" +cuKey+" , "+cuPosition);
                           break;

                      }


                  }


              }



          }


      }


  }




  /**
   *  播放本地 或者播放在线 数据初始化
   *
   * */
    public void setmPlayData(PlayData playData) {
        starttime = (int) (System.currentTimeMillis()/1000);
        hideTimeOutLayout();
        mIsPause = true;
        mPlayData = playData;
        setControllerUI();
        // 从现在页进来UI展示
        if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
            initLocalFullScreenView();
        }else{

           if( mActivity.getSCRREN()== ChaoJiShiPinVideoDetailActivity.SCREEN.HALF){
               LogUtil.e("v1.1.2","play on line half screen hide episo");
               mSelectBtn.setVisibility(View.GONE);
               full_screen.setBackgroundResource(R.drawable.sarrs_pic_full_screen);

           }
        }
        if(playData==null){
            LogUtil.e("v1.1.2","play data is null ");
        }

        if (null != mPlayData) {
            //mIsPlayLocalUrl = mPlayData.getIsLocalVideo();
                // 从除下载页进入半屏页播放
                String from = playData.getFrom();
                // modify by xll 2015/09/18
                if (ConstantUtils.PLAYER_FROM_DETAIL.equals(from)) {
                    mEpisodes = mPlayData.getmEpisodes();
                    // 获得当前剧集在播放列表中的位置
                    mPlayEpisode = getCurrEpisode();
                    LogUtil.d("xll", "在线播放_详情页点击");

                } else if (ConstantUtils.PLAYER_FROM_DETAIL_ITEM.equals(from)) {
                    mEpisodes = mPlayData.getmEpisodes();
                    // 获得当前剧集在播放列表中的位置
                    mPlayEpisode = getCurrEpisode();
                    LogUtil.d("xll", "在线播放_详情页点击");

                }
                LogUtil.d("dyf", "！！！！！！！！！！！！！！！！");
                if (mPlayEpisode == null) {
                    LogUtil.d("dyf", "data is null from " + from);
                    return;
                }
                // 播放本地
                if(mPlayEpisode.isLocal()){
                    LogUtil.e("v1.1.2", " play local " + mPlayEpisode.getTitle());
                    mActivity.setEpisodeType(ChaoJiShiPinVideoDetailActivity.EpisodeType.EPISO_LOCAL);
                    executeLocalPlayLogic(mPlayEpisode);

                }else{
                    LogUtil.e("v1.1.2", " play online " + mPlayEpisode.getTitle());
                    mActivity.setEpisodeType(ChaoJiShiPinVideoDetailActivity.EpisodeType.EPISO_ONLINE);
                    executeCommonPlay(mPlayEpisode);

                }
                setNextState();


        }

    }


    /**
     * 当前播放的视频
     *
     * @param porder xll 2015年11月6日
     */
    int localIndex=0;
    public LocalVideoEpisode getCurrLocalEisode(String porder) {
        // 单视频porder 是空
        if(TextUtils.isEmpty(porder)){
             if( null != mLocalPlayLists && mLocalPlayLists.size() > 0){
                  LogUtil.e("xll ","当前播放单视频 GVid "+mLocalPlayLists.get(0).getGvid());
                  return mLocalPlayLists.get(0);
              }

        }else{
            // 专辑porder 取剧集
            if (!TextUtils.isEmpty(porder) && null != mLocalPlayLists && mLocalPlayLists.size() > 0) {
                int playSize = mLocalPlayLists.size();
                boolean isFindEpisode = false;
                // 查找当前影片位置
                for (int i = 0; i < playSize; i++) {
                    LocalVideoEpisode localVideoEpisode = mLocalPlayLists.get(i);
                    if (null != localVideoEpisode) {
                        if (porder.equals(localVideoEpisode.getPorder())) {
                            mNotifyData.setPosition(i);
                            localIndex=i;
                            isFindEpisode = true;
                            LogUtil.e("xll ","当前播视频 GVid "+localVideoEpisode.getGvid());
                            return localVideoEpisode;
                        }
                    }
                }
                // 如果没找到则取第一个
                if (!isFindEpisode) {
                    LogUtil.e("xll", "NEW local finish find ");
                    //mActivity.finish();
                    mNotifyData.setPosition(0);
                    LogUtil.e("xll ", "当前播视频 GVid " + mLocalPlayLists.get(0));
                    return mLocalPlayLists.get(0);
                }
            }

        }




        return null;
    }

    /**
     * 暂停音乐播放器（sarrs启动播放器需要暂停QQ、酷我等音乐播放器）
     */
    private void pauseMusic() {
        ActivityManager am = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> tasks = am.getRunningServices(10);
        am.killBackgroundProcesses("");
    }

    private void executeLocalChangePlay() {
        LogUtil.e("v1.1.2","executeLocalChangePlay");
        if (mLocalPlayLists != null) {
            if (mPlayData.getIndex() < mLocalPlayLists.size() - 1) {
                mPlayData.setIndex(mPlayData.getIndex() + 1);
                mPlayData.setRecordposition(0);
                LocalVideoEpisode localVideoEpisode = mLocalPlayLists.get(mPlayData.getIndex());
                // TODO 存储播放历史
                executeLocalPlayLogic(localVideoEpisode);
            } else {
                mCanPlayNext = false;
                mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_no_next);
                LogUtil.e(TAG, "index out range");
            }

        }

    }

    /**
     *  从除下载入口进入播放页，播放本地剧集
     */

    private void executeLocalPlayLogic(VideoItem localVideoEpisode) {
        LogUtil.e(TAG, "msg from Local exute  ！");
        LogUtil.e("v1.1.2","executeLocalPlayLogic "+ localVideoEpisode.getTitle());
        if(mNotifyData!=null){
            LogUtil.e("xll","send  "+mNotifyData.getPosition());
            EventBus.getDefault().post(mNotifyData);
            setisPlayEpisode();
        }



        setTitleText(mActivity.getString(R.string.local_tag)+localVideoEpisode.getTitle());
        String downloadType = localVideoEpisode.getDownLoadType();
        String playUrl = "";
        if (PlayerUtils.DOWNLOAD_M3U8.equalsIgnoreCase(downloadType)) {
            PlayerUtils.DEFAULT_PLAYER_TYPE = Type.MOBILE_H264_M3U8;
            StringBuffer sbff = new StringBuffer();
            sbff.append(PlayerUtils.LOCAL_M3U8_DOMAIN);
            sbff.append(localVideoEpisode.getPlay_url());
            playUrl = sbff.toString();
        } else {
            PlayerUtils.DEFAULT_PLAYER_TYPE = Type.MOBILE_H264_MP4;
            playUrl = localVideoEpisode.getPlay_url();
        }

        // 根据当前的播放器类型创建播放器View
        if (null != mPlayerView) {
            mVideoViewPosition.removeView(mPlayerView);
        }

        //获取播放器的Controller
        mPlayContorl = mVideoViewBuilder.build(mActivity, PlayerUtils.DEFAULT_PLAYER_TYPE);
        // 将播放器对象添加至容器中
        mPlayerView = mPlayContorl.getView();
        mVideoViewPosition.addView(mPlayerView);
        setPlayerListener();
        if (!TextUtils.isEmpty(playUrl)) {
            playUrl = playUrl.replace(" ", "%20");
            setVideoUri(playUrl);
            Log.d("Path", " playUrl is " + playUrl);
        }
    }




    /**
     * 本地播放逻辑
     */

    private void executeLocalPlayLogic(LocalVideoEpisode localVideoEpisode) {
        LogUtil.e(TAG, "msg from Local exute  ！");
        setTitleText(localVideoEpisode.getName());
        String downloadType = localVideoEpisode.getDownType();
        String playUrl = "";
        if (PlayerUtils.DOWNLOAD_M3U8.equalsIgnoreCase(downloadType)) {
            PlayerUtils.DEFAULT_PLAYER_TYPE = Type.MOBILE_H264_M3U8;
            StringBuffer sbff = new StringBuffer();
            sbff.append(PlayerUtils.LOCAL_M3U8_DOMAIN);
            sbff.append(localVideoEpisode.getPlay_url());
            playUrl = sbff.toString();
        } else {
            PlayerUtils.DEFAULT_PLAYER_TYPE = Type.MOBILE_H264_MP4;
            playUrl = localVideoEpisode.getPlay_url();
        }

        // 根据当前的播放器类型创建播放器View
        if (null != mPlayerView) {
            mVideoViewPosition.removeView(mPlayerView);
        }

        //获取播放器的Controller
        mPlayContorl = mVideoViewBuilder.build(mActivity, PlayerUtils.DEFAULT_PLAYER_TYPE);
        // 将播放器对象添加至容器中
        mPlayerView = mPlayContorl.getView();
        mVideoViewPosition.addView(mPlayerView);
        setPlayerListener();
        if (!TextUtils.isEmpty(playUrl)) {
            playUrl = playUrl.replace(" ", "%20");
            setVideoUri(playUrl);
            Log.d("Path", " playUrl is " + playUrl);
        }
    }


    /**
     * 设置播放下一集的状态
    */
    private void playNext() {
        /**
         * 先上报再切换下一集
         */
        if (mPlayContorl != null) {
            ac = "finish";
            ut = 0;
            if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                play_type = 2;
            } else {
                play_type = 0;
            }
            timing = mPlayContorl.getCurrentPosition()/1000;
            vlen = mPlayContorl.getDuration() / 1000;
            if(NetworkUtil.isNetworkAvailable(mActivity)){
                UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "",mVideoPlayerFragment.getSeid(),mVideoPlayerFragment.getPeid());
            }
        }

        retry = 0;
        retryCount=0;
        mStreamIndex=0;
        isHasRule=false;
        isQuitSniff=false;
        hideTimeOutLayout();
        //保存播放记录
         mIsPause=true;
        // 设置下一集数据，发送数据给底部fragment更新剧集状态
            VideoItem episode = getNextEpisode();
            if(episode!=null){
                mPlayEpisode=episode;
                LogUtil.e("v1.1.2","getNext episode "+episode.getTitle());
            }

            // 点击下一集 没有发送EventBus 不会接收回调
            setNextState();
            if (null != episode) {
                if(episode.isLocal()){
                    executeLocalPlayLogic(episode);
                }else{
                    executeCommonPlay(episode);
                }



            }else {
                return;
            }

        }



    /**
     * 设置播放下一集按钮是否可用
     */
    private void setNextState() {
            if (null != mPlayData && null != mEpisodes) {
            int key = mPlayData.getKey();
            int index = mPlayData.getIndex();
            LogUtil.e("xll"," request next (k,p) "+mPlayData.getKey()+""+mPlayData.getIndex());
            ArrayList<VideoItem> episodes = mEpisodes.get(key);//当前分页的剧集list
            ArrayList<VideoItem> nextPageEpisodes = mEpisodes.get(key + 1);//下一个分页的剧集list
            if (null != episodes && (episodes.size() > (index + 1))) {
                //当前分页
                //不是当前分页最后一集
                mCanPlayNext = true;
                mPlayNextBtn.setEnabled(true);
                mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_next);
            } else if (null != nextPageEpisodes && nextPageEpisodes.size() > 0) {
                //下一个分页
                mCanPlayNext = true;
                mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_next);
                LogUtil.e("xll","当前分页 有下一个分页，且下一个分页剧集数 "+nextPageEpisodes.size());

            } else {

                mCanPlayNext = false;
                mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_no_next);
            }
        } else {
            mCanPlayNext = false;
            mPlayNextBtn.setBackgroundResource(R.drawable.sarrs_pic_videoplayer_no_next);
        }
    }



    /**
     * 获取下一集数据
     */
    //
    private VideoItem getNextEpisode() {
        VideoItem videoItem = null;
        if (null != mNotifyData) {
            int key = mNotifyData.getKey();
            int index = mNotifyData.getPosition();
            ArrayList<VideoItem> episodes = mEpisodes.get(key);//当前分页的剧集list
            ArrayList<VideoItem> nextPageEpisodes = mEpisodes.get(key + 1);//下一个分页的剧集list
            if (null != episodes && (episodes.size() >(index + 1))) {
                //当前分页
                //不是当前分页最后一集
                videoItem = episodes.get(index + 1);
                mNotifyData.setPosition(index + 1);
                LogUtil.e("xll","build data key"+key);
                LogUtil.e("xll", "build data index" + index+1);
                LogUtil.e("xll", "当前分页 播放器下一集切换传递给底部index ： " + mNotifyData.getPosition());
            } else if (null != nextPageEpisodes && nextPageEpisodes.size() > 0) {
                //下一个分页第一个数据
                LogUtil.e("xll","下一个分页 第一条数据 ");
                videoItem = nextPageEpisodes.get(0);
                mNotifyData.setKey(key + 1);
                mPlayData.setKey(key + 1);
                mNotifyData.setPosition(0);
            }else {
                 // 最后一集
                if(episodes!=null&&episodes.size()>0&&index==episodes.size()-1){
                    mNotifyData.setPosition(index);
                }
            }
            //播放记录对专辑只记录最新一条， 切换剧集时播放记录置0
            if(mPlayData!=null){
                mPlayData.setRecordposition(0);
            }

        }
        return videoItem;
    }

    /**
     * 更新list数据
     *
     * @param playData
     */
    public void updatePlayData(PlayData playData) {
        if (null != playData && null != playData.getmEpisodes() && playData.getmEpisodes().size() > 0) {
            mPlayData = playData;
            mPlayData.setmEpisodes(playData.getmEpisodes());
            mPlayData.setPage_titles(playData.getPage_titles());//分页list
            if (null != playData && ConstantUtils.PLAYER_FROM_DETAIL.equals(playData.getFrom())) {
                mPlayData.setCid(playData.getCid());
            }
            mEpisodes = playData.getmEpisodes();
            //更新数据后，重新判断能否播放下一集
            setNextState();
            //全屏剧集展示


        }

    }

    /**
     * 更新全屏剧集面板
     */
    void resetFullScreenEpiso(PlayData playData) {

        if (tagAdapter == null) {
            tagAdapter = new VideoDetailFuScreeenExpandListAdapter(playData.getPage_titles(), mActivity, playData.getmEpisodes(), playData.getCid());
            mSelectListView.setAdapter(tagAdapter);

        }
        // 剧集三角形
        //根据实际播放key来更新全屏剧集展示
        LogUtil.e("xll","receive data 全屏剧集自动展开 key "+playData.getKey() +" position "+playData.getIndex());
       tagAdapter.updateCurrentVideoInPage(playData.getKey(), playData.getIndex(), playData.getTagIndex(),playData.getTagIndex2(), playData.getmEpisodes());



    }


    /**
     * 公共的播放方法
     */
    private void executeCommonPlay(VideoItem episode) {
        if(videoplayer_load_timeout_layout!=null&&videoplayer_load_timeout_layout.isShown()){
            videoplayer_load_timeout_layout.setVisibility(View.GONE);
        }
        // 在线播放默认使用软解播放
        isplayfromweb = true;
        PlayDecodeMananger.setmNeedSysDecoder(false);
        PlayerUtils.DEFAULT_PLAYER_TYPE = PlayDecodeMananger.getCurrPlayerType();
        mPlayEpisode = episode;
        // 设置标题栏名称
        setVideoName(episode);
        if(TextUtils.isEmpty(mPlayEpisode.getSource())){
            LogUtil.e("xll","播放器source字段为空！");
        }
        executeProcessBySite(mPlayEpisode.getSource());
    }

    /**
     * 判断是否分页
     */
    public boolean isNextPage(int key, int position) {

        ArrayList<VideoItem> pageList = mPlayData.getmEpisodes().get(key);
        if (pageList != null) {
            if (position < pageList.size() - 1) {
                return false;
            } else {

                return true;
            }
        }
        return false;


    }

   /* *//**
     * 接收播放器开始播放消息更新剧集展示&底部弹出view展开状态时，
     *//*
    void updateFullScreenWhenStartPlay(VideoPlayerNotifytData data) {
        if (!data.isFirst()) {
            // 判断是否是跨页
            if (isNextPage(data.getKey(), data.getPosition())) {
                LogUtil.e("Expand ", " key " + data.getKey() + " position " + data.getPosition());

                tagAdapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), data.getLastKey(), data.getLastPosition(), mEpisodes, mPlayData.getCid(), data.getType());
            } else {
                tagAdapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), data.getLastKey(), data.getLastPosition(), mEpisodes, mPlayData.getCid(), data.getType());
            }
        }

    }*/
    /**
     *   跟新当前显示的剧集播放状态
     * */
    void setisPlayEpisode(){
        // 更新剧集
        if(tagAdapter!=null){
            //TODO need check xll
            if(mEpisodes!=null){
                for(int i=0;i<mEpisodes.size();i++){
                  List<VideoItem> items=mEpisodes.get(i);
                    if(items!=null&&items.size()>0){
                        for(int j=0;j<items.size();j++){
                            mEpisodes.get(i).get(j).setIsPlay(false);
                        }
                    }
                }
            }
            LogUtil.e(TAG,"juji2"+mEpisodes.toString());
            if(mEpisodes.indexOfKey(mNotifyData.getKey())>=0&&mEpisodes.get(mNotifyData.getKey()).size()>mNotifyData.getPosition()){
                mEpisodes.get(mNotifyData.getKey()).get(mNotifyData.getPosition()).setIsPlay(true);
            }
           // LogUtil.e(TAG,"juji3"+mEpisodes.toString());
        }
    }



    /**
     * 刚进入半屏页播放逻辑
     * <p/>
     * 区分站点，执行播放
     *
     * @param site
     */
    private void executeProcessBySite(String site) {
        LogUtil.e("xll", "source " + site);
        mLinkShellUrl = null;
        if (!TextUtils.isEmpty(site)) {
            //
            if (mPlayData == null) {
                return;
            }
            if (mPlayData.getFrom() != null) {
                LogUtil.e("xll","send  "+mNotifyData.getPosition());
                EventBus.getDefault().post(mNotifyData);
                setisPlayEpisode();
                if (!TextUtils.isEmpty(mPlayEpisode.getSource()) && mPlayEpisode.getSource().equalsIgnoreCase(PlayerUtils.SIET_LETV)) {
                    CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
                    // 判断当前CDE服务是否启动
                    if (cdeHelper.isReady()) {
                        LogUtil.e(TAG, "!!!!!!!CDE服务是启动的");
                        LogUtil.e("xll","NEW insite progress");
                        executeInSiteProcess();
                    } else {
                        mCdeStatusReiver = new CDEStatusReceiver();
                        mActivity.registerReceiver(mCdeStatusReiver, new IntentFilter(
                                CdeService.ACTION_CDE_READY));
                        mCDEManager.startCde();
                    }
                } else {
                    LogUtil.e(TAG, "exuteCommon outSite Progress");
                    executeOutSiteProgress();
                }
            } else {

                LogUtil.e(TAG, " U need register flag for media！");
            }

        }
    }

    /**
     * 执行站外播放流程 xll 2015年8月26日 下午1:29:08
     */
    private void executeOutSiteProgress() {
        LogUtil.e("xll","NEW insite progress");
        LogUtil.e(TAG, "NEW  outSite " + mPlayEpisode.getSource());
        playLogic(mPlayEpisode);
    }

    /**
     * 执行播放逻辑
     *
     * @param episode xll 2015年8月27日 下午16:47
     */
    private void playLogic(VideoItem episode) {
        mPlayEpisode = episode;
        mPlayUrl = episode.getPlay_url();
        LogUtil.e("wulianshu","mPlayUrl:"+mPlayUrl);
        mSingleInfoVid = episode.getGvid();
        // 如果包含视频信息的对象或者请求单集信息的ID为空则直接跳抓网页
        if (null != mPlayEpisode && !TextUtils.isEmpty(mSingleInfoVid)) {
            // 如果是云盘资源则执行云盘自己的请求
            if (PlayerUtils.SITE_CLOUDDISK.equals(episode.getSource())) {
                if (PlayDecodeMananger.ismNeedSysDecoder()) {
                    PlayDecodeMananger.setmNeedSysDecoder(false);
                }
                String cloudType = PlayDecodeMananger.getCloudSourceType();
                LogUtil.e("xll", "playType net =" + cloudType);
                LogUtil.e("xll", "NEW cloud progress" + mPlayEpisode.getSource());
                requestCloudPlay(cloudType);
            } else {
                // 执行除云盘外其他站点逻辑
                LogUtil.e("xll", "NEW outsite progress" + mPlayEpisode.getSource());
                LogUtil.e("xll", "NEW outsite gvid" + mPlayEpisode.getGvid());
                requsetOutSiteData();


            }
        } else {
            jumpToWebPlayeActivity();
        }
    }

    /**
     * 请求云盘防盗链接口
     *
     * @param type
     */
    private void requestCloudPlay(String type) {
        mCurrCloudType = type;
        // 执行网盘相关请求
        String gvid = mPlayEpisode.getGvid();
        String vType = letvVideoCode;
        String playid = "0";//播放类型：0:点播 1:直播 2:下载
        String uuid = Utils.getDeviceId(mActivity);
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_CLOUDDISKVIDEO_TAG);
        HttpApi.getPlayUrlCloudRequest(gvid, vType, playid, type, uuid,mFormat).start(new RequestCoudDiskPlayUrlListener(), ConstantUtils.REQUEST_CLOUDDISKVIDEO_TAG);
    }

    /**
     * 请求防盗链接口(云盘除外的外网数据使用) xll 2014年12月12日 下午2:22:31
     */
    private int retryCount=0;
    private String mFormat="0,1,2,3,4";
    private void requsetOutSiteData() {
        isfeedbackalready = false;
        isjscut = false;
        isstream = false;
        // 执行除云盘之外的其他站点的逻辑
        //重置截流逻辑
        retryCount++;
        isNeedJsCut=false;
        String gvid = mPlayEpisode.getGvid();
        LogUtil.e("xll","NEW gvid "+mPlayEpisode.getGvid());
        String playid = "0";//播放类型：0:点播 1:直播 2:下载
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SIGNLEVIDEO_TAG);
        HttpApi.requestOutSiteData(gvid, null, playid, mFormat).start(new OutSiteDataListener());
    }

    /**
     *  流地址更新接口
     *  调用时机： 在返回stream-list不空，并且不能播放
     *  如果防盗链接口获取stream-list 不为空，但是不能播放需要调用此接口
     *  @param playUrl  防盗链返回的播放地址
     *  @param format   清晰度，0-normal，1-high，2-super，3-super2，4-real
     *  @param eid      新版外网流地址接口会返回该字段，上报时回传，用于统计播放失败率
                        （iOS和Android流地址的两个eid中选择一个上报即可）
     *
     *
     * */

    void requestUpdateStream(String playUrl,String format,String eid){
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPDATEURL);
        HttpApi.updateUrl( playUrl,  format,  eid).start(new UpdateStreamListListener());
    }








    /**
     *  设置清晰度优先级
     * */
    String requestFormat="";

  /**
   *  retry logic
   * */

    void retryUrl(){
        if(mOutSiteDataInfo!=null&&!mOutSiteDataInfo.isHasStreamList()) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (retryCount<=1) {
                        LogUtil.e("xll", "NEW retry streamList " + retryCount);
                        requsetOutSiteData();
                    } else {
                        // 代理层501 也会保证反正url
                        LogUtil.e("xll", "retry 2 time failed !  do play");
                        mPlayUrl = mOutSiteDataInfo.getOutSiteDatas().get(0).getUrl();
                        handlerOutSiteError();
                    }

                }
            }, 2000);

            return;

        }
    }

  /**
   *   return back  os-Type   IOS ： M3U8格式  ANDROID  ： MP4
   * */


    public class OutSiteDataListener implements RequestListener<OutSiteDataInfo> {
        @Override
        public void onResponse(OutSiteDataInfo result, boolean isCachedData) {
            LogUtil.e("xll ", "NEW  execute OutSite result ok !");
            mOutSiteDataInfo=result;
            // 默认设置M3U8 有清晰度选项
            // 芒果源单独处理 执行mp4
            //||mPlayEpisode.getSource().equalsIgnoreCase("qq")
            if(mPlayEpisode.getSource().equalsIgnoreCase("imgo")){
                LogUtil.e("xll","NEW imgo|qq source ");
                    //MP4 格式
                PlayDecodeMananger.setmNeedSysDecoder(true);
                PlayerUtils.DEFAULT_PLAYER_TYPE = PlayDecodeMananger.getCurrPlayerType();
                //mp4 req map
                Map<String,OutSiteData> map =   mOutSiteDataInfo.getOutSiteDataMap();
                if(map!=null&&map.size()>0){
                    if(map.containsKey(ConstantUtils.ClarityType.NORMAL)){
                        mOutSiteData=map.get(ConstantUtils.ClarityType.NORMAL);
                        code_rate = "";
                        mStream_list=mOutSiteData.getStream_list();
                    }
                    if(map.containsKey(ConstantUtils.ClarityType.HIGH)){
                        mOutSiteData=map.get(ConstantUtils.ClarityType.HIGH);
                        mStream_list=mOutSiteData.getStream_list();
                    }
                    if(map.containsKey(ConstantUtils.ClarityType.SUPER)){
                        mOutSiteData=map.get(ConstantUtils.ClarityType.SUPER);
                        mStream_list=mOutSiteData.getStream_list();
                    }
                    if(map.containsKey(ConstantUtils.ClarityType.SUPER2)){
                        mOutSiteData=map.get(ConstantUtils.ClarityType.SUPER2);
                        mStream_list=mOutSiteData.getStream_list();
                    }
                }
                if(mStream_list!=null&&mStream_list.size()>0){
                    LogUtil.e("xll","NEW imgo|qq source doplay");
                    mOutSiteData.getRequest_format();
                    doPlay(PlayerUtils.DEFAULT_PLAYER_TYPE, mStream_list.get(0));
                }else{
                    if(mOutSiteData!=null){
                        mPlayUrl= mOutSiteData.getUrl();
                        LogUtil.e("TestStreamUrl", "webview " + mPlayUrl);
                        Log.e("TestStreamUrl",""+  "webview "+mPlayUrl);
                        LogUtil.e("xll","NEW imgo|qq source webview");
                        jumpToWebPlayeActivity();
                    }else{
                        //retry logic
                        LogUtil.e("xll","NEW imgo|qq source retry");
                       retryUrl();
                    }
                }
               return;
            }
            executeOutSitePlayCore(mStreamIndex);
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e("xll "," execute OutSite net error");
            showNoNetView();
        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e("xll ", " execute OutSite data error");
            showTimeOutLayout();
        }
    }
    /**
     *   stream-list 接口更新回调
     * */

    public class UpdateStreamListListener implements RequestListener<UpdateUrlInfo> {
        @Override
        public void onResponse(UpdateUrlInfo result, boolean isCachedData) {
            LogUtil.e("xll ", " execute update stream list ok");



        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e("xll ", " execute update stream net error");
            showNoNetView();
        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e("xll ", " execute update stream data error");
            showTimeOutLayout();
        }
    }
    String mCurrentType=ConstantUtils.OutSiteDateType.M3U8;
    /**
     * 执行除云盘之外其他站点源视频逻辑
     * 优先M3U8 --->MP4
     * @param  index 当前流地址列表索引
     *
     */
    private void executeOutSitePlayCore(int index) {
//        if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
//            return;
//        }
        LogUtil.e("xll","NEW current priority "+index);
//        if(mOutSiteDataInfo!=null  && mOutSiteDataInfo.getOutSiteDatas()==null||!mOutSiteDataInfo.isHasStreamList()){
//            retryUrl();
//            return;
//        }
        if((mOutSiteDataInfo==null || !mOutSiteDataInfo.isHasStreamList()) || mOutSiteDataInfo.getOutSiteDatas()==null||mOutSiteDataInfo.getOutSiteDatas().size()==0){
            retryUrl();
            return;
        }
        if (null != mOutSiteDataInfo) {

            if(index<0||index>=mOutSiteDataInfo.getOutSiteDatas().size()){
                LogUtil.e("xll","NEW index out of range");
                mPlayUrl=mOutSiteDataInfo.getOutSiteDatas().get(0).getUrl();
                LogUtil.e("TestStreamUrl", "webview " + mPlayUrl);
                Log.e("TestStreamUrl",""+  "webview "+mPlayUrl);
                jumpToWebPlayeActivity();
                return;
            }
            mOutSiteData= mOutSiteDataInfo.getOutSiteDatas().get(index);
            LogUtil.e("xll","NEW execute OutSite url is "+mOutSiteDataInfo.getOutSiteDatas().get(index).getUrl());
            LogUtil.e("xll","NEW execute OutSite format is "+requestFormat);
            mAllowed_formats=mOutSiteData.getAllowed_formats();
            mApi_list=mOutSiteData.getApi_list();
            current_format = mOutSiteData.getRequest_format();
            mStream_list=mOutSiteData.getStream_list();
            mPlayUrl=mOutSiteData.getUrl();
            LogUtil.e("TestStreamUrl", "webview " + mPlayUrl);
            Log.e("TestStreamUrl",""+  "webview "+mPlayUrl);
            mEid=mOutSiteData.getEid();
            mUserAgent=mOutSiteData.getHeader();
            if(mOutSiteData.isHasRule()){
                mTs=mOutSiteData.getTs();
                mTe=mOutSiteData.getTe();
                if(!TextUtils.isEmpty(mTe)&&!TextUtils.isEmpty(mTs)){
                    isHasRule=true;
                }else{
                    isHasRule=false;
                }


            }else{
                isHasRule=false;
            }

            mOsType=mOutSiteData.getOs_type();
            LogUtil.e("xll", "NEW execute OutSite play ");
            //  M3U8
            if(mApi_list!=null&&mApi_list.size()>0&&isHasRule){
                LogUtil.e("xll","NEW execute  jsCut true");
                isNeedJsCut=true;
            }
            if(!TextUtils.isEmpty(mOsType)&&mOsType.equalsIgnoreCase(ConstantUtils.OutSiteDateType.M3U8)){
                //M3U8 格式
                PlayDecodeMananger.setmNeedSysDecoder(false);
            }else{
                //MP4 格式
                PlayDecodeMananger.setmNeedSysDecoder(true);
            }
            // 获取当前
            PlayerUtils.DEFAULT_PLAYER_TYPE = PlayDecodeMananger.getCurrPlayerType();
             // 截流
            if(isNeedJsCut){
                if(mStream_list!=null&&mStream_list.size()>0&&mStream_list.size()==1){
                    LogUtil.e("xll","NEW mstream-list ok!");
                    // 截流  确保每个api都执行请求apiConent
                    mApi_contentlist=new ArrayList<>();
                    LogUtil.e("xll", "NEW js cut start");
                    /*if(mPlayEpisode.getSource().equalsIgnoreCase("pptv"))
                    {
                        // streamList多个不处理直接切换优先级
                        if(mStream_list.size()>1){
                            LogUtil.e("xll","NEW pptv source streamList >1 prority ++");
                            mStreamIndex++;
                            executeOutSitePlayCore(mStreamIndex);
                            return;
                        }else{
                            LogUtil.e("xll","NEW pptv source streamList ok ");
                            executeSniff();
                        }

                    }else{

                    }*/
                    executeSniff();

                }else{
                    // 切换清晰度
                    LogUtil.e("xll","NEW mstream-list is null request outDataInfo!");
                     // 执行stream-list 请求防盗链接口
                       // TODO 切换优先级
                    mStreamIndex++;
                    executeOutSitePlayCore(mStreamIndex);
                    /**/
                }
            }else{
                LogUtil.e("xll", "NEW api  null   !  do not cut js just play ");
                // streamList 不为空直接播放1个
                if (mStream_list != null && mStream_list.size()>0&&mStream_list.size()==1){
                    doPlay(PlayerUtils.DEFAULT_PLAYER_TYPE, mStream_list.get(0));
                }else{
                    LogUtil.e("xll", "NEW api  null   !  do not cut js just play priorty -1");
                    mStreamIndex++;
                    executeOutSitePlayCore(mStreamIndex);
                }

            }
        }
    }


    /**
     *   发送js参数给js文件执行
     *   截流逻辑（保证每个api 每条都请求到,以及mstream-list每个都请求到）
     *
     * */
  void batchRuquestApi() {
          /* for(int m=0;m<mApi_list.size();m++){
                           mCurrentApi=m;
                           requsetApiData(mApi_list.get(m), mPlayUrl);
                           LogUtil.e("xll","NEW request apidata ！" +m);
               }*/
      //  设置请求api 阀值 6s // 执行js请求 需要等待api请求完毕


      if (mApi_list != null && mApi_list.size() > 0) {
          LogUtil.e("xll", "NEW js api size is " + mApi_list.size());
          // 多线请求api
          try {
              ThreadPoolManager.getInstanse().createPool();
              ThreadPoolManager.getInstanse().exeTaskTimOut(mApi_list, mPlayUrl, mUserAgent, 2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          } catch (ExecutionException e) {
              e.printStackTrace();
          }


      }

  }

   void batchSendJsparam(){
       // 关闭线程池不接受新的任务，如果此时后台有未执行完毕的任务取消掉直接，
       ThreadPoolManager.getInstanse().shutdown();
       while(!ThreadPoolManager.getInstanse().isTerminated()){
           //等待执行完毕
           LogUtil.e("xll","NEW wait for pool task finish ");
       }
               mApi_contentlist=ThreadPoolManager.getInstanse().getResultList();

               ThreadPoolManager.getInstanse().shutdown();
               if(mApi_contentlist==null||mApi_contentlist.size()==0){
                   LogUtil.e("xll", "NEW request apiContentList is 0 ！ ");
                   handlerOutSiteError();
                   return;
               }
               if(mStream_list==null||mStream_list.size()==0){
                   LogUtil.e("xll", "NEW request streamList is 0 ！ ");
                   handlerOutSiteError();
                   return;
               }
               LogUtil.e("xll","NEW out of time set apicontentList "+mApi_contentlist.size());
               if (mApi_contentlist != null && mApi_contentlist.size() > 0 && mStream_list != null && mStream_list.size() > 0) {
                   LogUtil.e("xll", "NEW send js apicontentList size " + mApi_contentlist.size());
                   LogUtil.e("xll", "NEW send js StreamList size " + mStream_list.size());

                   // sendCutRequest(mPlayUrl, mApi_contentlist.get(0), mStream_list.get(0));
                   //TODO task to execute cut sniff
                   for (int i = 0; i < mApi_contentlist.size(); i++) {
                       for (int j = 0; j < mStream_list.size(); j++) {

                           LogUtil.e("xll", "NEW send js data (api-content) " + mApi_contentlist.get(i));
                           LogUtil.e("xll", "NEW send js data (stream-list) " + mStream_list.get(j));
                           if (isQuitSniff) {
                               break;
                           }
                           // TODO 做一个超时逻辑
                           sendCutRequest(mPlayUrl, mApi_contentlist.get(i), mStream_list.get(j));
                       }
                   }
               } else {
                   //TODO 超出阀值后播放逻辑

                   LogUtil.e("xll", "NEW request api out of timeset ！ ");
                   handlerOutSiteError();
               }
   }
    /**
     *  js 截流优化
     * */
   void newBatchSendJs(){


               // 关闭线程池不接受新的任务，如果此时后台有未执行完毕的任务取消掉直接，
               ThreadPoolManager.getInstanse().shutdown();
               while(!ThreadPoolManager.getInstanse().isTerminated()){
                   //等待执行完毕
                   LogUtil.e("xll","NEW wait for pool task finish ");
               }
               mApi_contentlist=ThreadPoolManager.getInstanse().getResultList();
               if(mApi_contentlist==null||mApi_contentlist.size()==0){
                   LogUtil.e("xll", "NEW request apiContentList is 0 ！ ");
                   handlerOutSiteError();
                   return;
               }
               if(mStream_list==null||mStream_list.size()==0){
                   LogUtil.e("xll", "NEW request streamList is 0 ！ ");
                   handlerOutSiteError();
                   return;
               }
               LogUtil.e("xll","NEW out of time set apicontentList "+mApi_contentlist.size());
               for (int i = 0; i < mApi_contentlist.size(); i++) {
                   for (int j = 0; j < mStream_list.size(); j++) {
                       LogUtil.e("xll", "NEW send js data (api-content) " + mApi_contentlist.get(i));
                       LogUtil.e("xll", "NEW send js data (stream-list) " + mStream_list.get(j));
                       try{
                           if(isQuitSniff){
                               ThreadPoolManager.getInstanse().shutdown();
                               break;
                           }else{
                               ThreadPoolManager.getInstanse().exeTaskTimOut(mHandler,mApi_contentlist.get(i),mStream_list.get(j),mPlayUrl,mTs,mTe,isHasRule,2000);
                           }

                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       } catch (ExecutionException e) {
                           e.printStackTrace();
                       }
                   }
               }
   }
    void executeSniff(){

           // 请求所有api得到所有apiContent
           batchRuquestApi();
          // 5秒钟后
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                batchSendJsparam();
            }
        }, 6000);


   }



    /**
     * 跳转至网页播放 xll 2014年5月4日 下午3:56:53
     */
    private void jumpToWebPlayeActivity() {

        if (!"letv".equals(mPlayEpisode.getSource()) && !"nets".equals(mPlayEpisode.getSource()) && mOutSiteData !=null) {
            int durantion = (int) (System.currentTimeMillis() / 1000) - starttime;
            UploadStat.playfeedback(mOutSiteData.getUrl(), 3, 0, mPlayEpisode.getSource(), mPlayEpisode.getId(), durantion);
        }

        isplayfromweb = true;
        LogUtil.e("exuteCommon", " webview");
        Intent intent = new Intent(mActivity, PlayActivityFroWebView.class);
        intent.putExtra("url", mPlayUrl);
        intent.putExtra("title", mVideoName);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    /**
     * 执行站内播放流程
     */
    private void executeInSiteProcess() {
        if (null != mPlayEpisode && !TextUtils.isEmpty(mPlayEpisode.getGvid())) {
            requestGetPlayUrl();
        }
    }

    /**
     * 执行请求播放器（letv源）数据
     */
    private void requestGetPlayUrl() {
        String gvid = mPlayEpisode.getGvid();
        String playid = "0";//播放类型：0:点播 1:直播 2:下载
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_GETPLAYURL_TAG);
        HttpApi.getPlayUrlRequest(gvid, letvVideoCode, playid,mFormat).start(new RequestPlayUrlListener(), ConstantUtils.REQUEST_GETPLAYURL_TAG);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsPrepared) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!mLockScreenBtn.isSelected()) {
                    fast_toast.setVisibility(View.GONE);
                    if (progress_change != 0) {
                        int nowprogress = mPlayContorl.getCurrentPosition() + progress_change;
                        if (nowprogress <= 0) {
                            mPlayContorl.seekTo(0);
                        } else if (nowprogress > mDuration) {
                            mPlayContorl.seekTo(nowprogress);
                        } else {
                            mPlayContorl.seekTo(nowprogress);
                        }
                        // 直接播放
                        progress_change = 0;
                    }
                }

                break;
        }
        switch (v.getId()) {
            case R.id.mediacontroller_top:
            case R.id.mediacontroller_bottom:
                return true;
            case R.id.videoview_position:

                if (isShowEpiso) {
                    hideAnimSelect(300);
                }
                if (isSlide) {
                    hideSlideMenu();
                }
               //dynamicShowControll();


                gestureDetector.onTouchEvent(event);
                return true;

            // 一定要返回true，不然获取不到完整的事件
//               return false;
            default:
                break;
        }

        return false;
    }

    /**
     * 电影需要将剧集按钮置成灰色
     */

    public void resetEpisoBtnColor(PlayData data) {

        if (data != null && !TextUtils.isEmpty(data.getCid())) {
            if (data.getCid().equalsIgnoreCase(String.valueOf(ConstantUtils.TV_SERISE_CATEGORYID))||data.getCid().equalsIgnoreCase(String.valueOf(ConstantUtils.TV_SERISE_CATEGORYID))||data.getCid().equalsIgnoreCase(String.valueOf(ConstantUtils.CARTOON_CATEGORYID))||data.getCid().equalsIgnoreCase(String.valueOf(ConstantUtils.DOCUMENTARY_CATEGORYID))) {
//                mSelectBtn.setTextColor(mActivity.getResources().getColor(R.color.color_666666));
                mSelectBtn.setVisibility(View.VISIBLE);
            }else{
                LogUtil.e("v1.1.2","episo btn movie logic");
                mSelectBtn.setVisibility(View.GONE);
            }
        }
    }
    /**
     *  调用cde播放码流  link shell
     *  @param url 码流地址
     * */
    void doLinkShellByCde(String url){
            LogUtil.e(TAG, "$$$$$$$$url$$$$$$" + url);
            CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
            mLinkShellUrl = cdeHelper.getLinkshellUrl(url);
            LogUtil.e(TAG, "$$$$$$$$mLinkShellUrl$$$$$$" + mLinkShellUrl);
            String  resultUrl = cdeHelper.getPlayUrl(mLinkShellUrl);
            Type currPlayType = PlayDecodeMananger.getCurrPlayerType();
            if(currPlayType==Type.MOBILE_H264_M3U8){
                PlayDecodeMananger.setmNeedSysDecoder(true);
            }else{
                PlayDecodeMananger.setmNeedSysDecoder(false);
            }
            if (!TextUtils.isEmpty(resultUrl)) {
                doPlay(currPlayType, resultUrl);
            }
    }


    /**
     * 执行请求播放器（letv源）数据
     */
    private class RequestPlayUrlListener implements RequestListener<VStreamInfoList> {

        @Override
        public void onResponse(VStreamInfoList result, boolean isCachedData) {
            if (null != result ) {
                if(result.get(mHighClarity)!=null&&!TextUtils.isEmpty(result.get(mHighClarity).getMainUrl())){
                        LogUtil.e("xll ","TSS high");
                        doLinkShellByCde(result.get(mHighClarity).getMainUrl());
                } else if(result.get(mdefaultClarity)!=null&&!TextUtils.isEmpty(result.get(mdefaultClarity).getMainUrl())){
                    LogUtil.e("xll ","TSS middle");
                    doLinkShellByCde(result.get(mdefaultClarity).getMainUrl());

                } else if(result.get(mLowClarity)!=null&&!TextUtils.isEmpty(result.get(mLowClarity).getMainUrl())){
                    LogUtil.e("xll ","TSS low");
                    doLinkShellByCde(result.get(mLowClarity).getMainUrl());
                }


            }
        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }


    /**
     * 执行请求播放器（云盘源）数据
     */
    private class RequestCoudDiskPlayUrlListener implements RequestListener<CloudDiskBean> {

        @Override
        public void onResponse(CloudDiskBean result, boolean isCachedData) {
            // 如果首请求云盘MP4类型播放地址为空时则进行二次请求 请求m3u8播放源
            if (null != result && null != result.getmPlayUrls() && result.getmPlayUrls().size() > 0) {
                mCloudPlayUrls = result.getmPlayUrls();
                if (mCloudPlayPosition < mCloudPlayUrls.size()) {
                    String cloudPlayUrl = mCloudPlayUrls.get(mCloudPlayPosition);
                    String resultUrl = "";
                    CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
                    // 判断当前CDE服务是否启动
                    if (cdeHelper.isReady()) {
                        LogUtil.e(TAG, "!!!!!!!CDE服务是启动的");
                        if (!TextUtils.isEmpty(cloudPlayUrl)) {
                            cloudPlayUrl = PlayerUtils.addPlatCode(cloudPlayUrl);
                            LogUtil.e("dyf", "$$$$$$$$url$$$$$$" + cloudPlayUrl);
//                            CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
                            String linkShellUrl = cdeHelper.getLinkshellUrl(cloudPlayUrl);
                            LogUtil.e("dyf", "$$$$$$$$mLinkShellUrl$$$$$$" + linkShellUrl);
                            resultUrl = cdeHelper.getPlayUrl(linkShellUrl);
                        }
                        if (!StringUtil.isEmpty(resultUrl)) {
                            LogUtil.e("dyf", "云盘cdeurl-----------" + resultUrl);
                            doPlay(PlayerUtils.DEFAULT_PLAYER_TYPE, resultUrl);
                        }
                    } else {
                        mCdeStatusReiver = new CDEStatusReceiver();
                        mActivity.registerReceiver(mCdeStatusReiver, new IntentFilter(
                                CdeService.ACTION_CDE_READY));
                        mCDEManager.startCde();
                    }


                }
            } else {
                // 如果当前请求云盘的播放格式是M3u8那么可以开始请求MP4播放源
                if (mCurrCloudType == PlayDecodeMananger.CLOUD_M3U8) {
                    PlayDecodeMananger.setmNeedSysDecoder(!PlayDecodeMananger.ismNeedSysDecoder());
                    // 切换播放器类型然后进行相关请求
                    PlayerUtils.DEFAULT_PLAYER_TYPE = PlayDecodeMananger.getCurrPlayerType();
                    // 如果切换播放器成功了则开始发送请求请求m3u8资源
                    if (changePlayer(PlayerUtils.DEFAULT_PLAYER_TYPE)) {
                        requestCloudPlay(PlayDecodeMananger.getCloudSourceType());
                    }
                }
            }
        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }


    /**
     * CDE注册广播
     */
    private class CDEStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                LogUtil.e("onReceive", " exute letv");
                return;
            }

            if (CdeService.ACTION_CDE_READY.equals(intent.getAction())) {
                unRegisterReceiver();

                if (PlayerUtils.SIET_LETV.equals(mPlayEpisode.getSource())) {
                    // 如果CDE已经准备就绪则开始执行内网播放逻辑
                    LogUtil.e("onReceive", " exute letv");

                    executeInSiteProcess();
                } else {
                    LogUtil.e("onReceive", " exute outsite");
                    // 如果CDE已经准备就绪则开始执行外网播放逻辑中的云盘播放
                    executeOutSiteProgress();
                }

            }
        }

    }




    /**
     *  发送js截流请求
     *  @param apiContent  api-list 请求每一条api对应的api-content
     *  @param requestUrl  防盗链接口返回播放地址
     *  @param streamUrl  防盗链接口返回流数组对应的每一条url数据
     *
     * */
     void sendCutRequest(String requestUrl,String apiContent,String streamUrl){
         com.alibaba.fastjson.JSONObject obj = new  com.alibaba.fastjson.JSONObject();
         LogUtil.e("xll", "NEW send js requestUrl:" + requestUrl + " streamUrl " + streamUrl);
         obj.put("requestUrl", Base64.encodeToString(requestUrl.getBytes(), Base64.DEFAULT));
         obj.put("uStream", Base64.encodeToString(streamUrl.getBytes(), Base64.DEFAULT));
         obj.put("apiContent", Base64.encodeToString(apiContent.getBytes(), Base64.DEFAULT));
         //TODO rule 值获取
         if(isHasRule){
             LogUtil.e("xll", "NEW js has rule");
             com.alibaba.fastjson.JSONObject ruleObj = new  com.alibaba.fastjson.JSONObject();

             if(!TextUtils.isEmpty(mTs)){
                 ruleObj.put("ts",mTs);
             }
             if(!TextUtils.isEmpty(mTe)){
                 ruleObj.put("te",mTe);
             }
             obj.put("rule", ruleObj);
             LogUtil.e("xll", "NEW js rule " + ruleObj.toString());
             LogUtil.e("xll", "NEW js ts te (" +mTs+")  ("+mTe+")");
         }else{
             LogUtil.e("xll", "NEW js no rule");
         }

         mSnifferParamter = obj.toString().replace("\\n","");
         String fileName="request.html";
         FileUtils.writeHtmlToData(mActivity,fileName,mSnifferParamter);
         LogUtil.e(TAG, "@@@@@@@" + mSnifferParamter);
         // 发送消息调用JS代码
         mHandler.sendEmptyMessage(Utils.GET_JS_RESULT);
     }





    /**
     * CDE取消注册广播
     */
    public void unRegisterReceiver() {
        if (null != mActivity && null != mCdeStatusReiver) {
            mActivity.unregisterReceiver(mCdeStatusReiver);
            mCdeStatusReiver = null;
        }
    }


    /**
     * 切换播放器
     *
     * @param currPlayType zhangshuo 2014年5月4日 上午11:41:43
     */
    private boolean changePlayer(Type currPlayType) {
        try {
            if (null != mVideoViewPosition) {
                // 释放播放资源
                resetPlayerData();
                PlayerUtils.DEFAULT_PLAYER_TYPE = currPlayType;
                if (null != mPlayerView) {
                    // 移除播放器
                    mVideoViewPosition.removeView(mPlayerView);
                }
                mPlayContorl =
                        mVideoViewBuilder.build(mActivity, PlayerUtils.DEFAULT_PLAYER_TYPE);
                mPlayerView = mPlayContorl.getView();
                mVideoViewPosition.addView(mPlayerView);
                LogUtil.e("xll", "player width "+mVideoViewPosition.getWidth());
                LogUtil.e("xll","player height "+mVideoViewPosition.getHeight());

                // 设置播放器监听事件
                setPlayerListener();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void resetPlayerData() {
        mIsPause = false;
        mIsPrepared = false;
        mIsControllerShow = false;
        // 重试次数置为0
        mReplayNum = 0;
        // 重置云盘播放位置
        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
    }


    /**
     * 执行播放逻辑
     *
     * @
     */
    private void doPlay(final Type currPlayerType, final String playUrl) {
        // UI线程中添加
        if(TextUtils.isEmpty(playUrl)){
            return;
        }
        PlayerUtils.DEFAULT_PLAYER_TYPE = currPlayerType;


        if (null != mVideoViewPosition && null != mPlayerView) {
            mVideoViewPosition.removeView(mPlayerView);
        }
        mPlayContorl = mVideoViewBuilder.build(mActivity, PlayerUtils.DEFAULT_PLAYER_TYPE);
        mPlayerView = mPlayContorl.getView();
        mVideoViewPosition.addView(mPlayerView);
        setPlayerListener();
        setVideoUri(playUrl);
        LogUtil.e("TestStreamUrl", "" + playUrl);
        Log.e("TestStreamUrl",""+ playUrl);
        }

    private void setVideoUri(String playUrl) {
        if (!TextUtils.isEmpty(playUrl)) {
            mIsPrepared = false;
            //mPlayContorl.setVideoPath(playUrl);
            PlayUrl play = new PlayUrl();
            play.setUrl(playUrl);
            LogUtil.e("play_url", "" + playUrl);
            mPlayContorl.setVideoPlayUrl(play);
        }
    }

    void showTimeOutLayout() {
        if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
            return;
        }
        if(!mIsPause) {
        setMediaControllerTopVisibile(true);
            videoplayer_load_timeout_layout.setVisibility(View.VISIBLE);
        }
    }

    void hideTimeOutLayout() {
        videoplayer_load_timeout_layout.setVisibility(View.GONE);
       // setPlayLoadingVisibile(true);

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.e(TAG, "!!!!!!!!!onPrepared");
        LogUtil.e("v1.1.2", " onprepare  start");
        //初始化上报
        if (mPlayContorl != null) {
            ac = "init";
            begin_time = System.currentTimeMillis();
            ut = 0;
            if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                play_type = 2;
            } else {
                play_type = 0;
            }
            timing = 0;
            vlen = mPlayContorl.getDuration() / 1000;
            LogUtil.e("wulianshu","ac = init");
            UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "",mVideoPlayerFragment.getSeid(),mVideoPlayerFragment.getPeid());
        }
          isplayfromweb = false;
            if (mActivity.getNetWork() == ChaoJiShiPinVideoDetailActivity.NetWork.OFFLINE) {
                LogUtil.e(TAG, "Media no net !");
                LogUtil.e("v1.1.2", " onprepare net error");
                if (null != mPlayContorl && !mIsPrepared) {
                    executePrePare();
                }

            } else if (mActivity.getNetWork() == ChaoJiShiPinVideoDetailActivity.NetWork.GSM && isForcePlay) {
                LogUtil.e(TAG, "Media gsm net !");
                if (null != mPlayContorl && !mIsPrepared) {
                    LogUtil.e("v1.1.2"," onprepare gsm");
                    executePrePare();
                }
            } else if (mActivity.getNetWork() == ChaoJiShiPinVideoDetailActivity.NetWork.WIFI) {
                LogUtil.e(TAG, "Media wifi net !");
                if (null != mPlayContorl && !mIsPrepared) {
                    LogUtil.e("v1.1.2"," onprepare wifi");
                    executePrePare();
                }else{
                    LogUtil.e("v1.1.2"," onprepare wifi mIspareed "+mIsPrepared);
                }

            }else{
                LogUtil.e("v1.1.2", " onprepare unknown state !");
            }
        /*}*/

        //开始播放上报
        if (mPlayContorl != null) {
            ac = "play";
            if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                play_type = 2;
            } else {
                play_type = 0;
            }
            timing = 0;
            vlen = mPlayContorl.getDuration() / 1000;
            //Object object,String ac,String ut,String retry,String play_type,String code_rate,String ref,String timing,String vlen
            end_time = System.currentTimeMillis();
            ut = end_time - begin_time;
            LogUtil.e("wulianshu","ac = play");
            UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "",mVideoPlayerFragment.getSeid(),mVideoPlayerFragment.getPeid());
        }

    }

    /**
     * executePrepare
     */
    void executePrePare() {

        if (mPlayContorl == null) {
            return;
        }

        mIsPrepared = true;
        mIsPause=true;
        mDuration = mPlayContorl.getDuration();
        // 设置影片总长
        mTotalTime.setText(PlayerUtils.toStringTime((int) mDuration));
        tv_total_length.setText(PlayerUtils.toStringTime((int) mDuration));
        // 设置当前播放时长
        mCurrTime.setText(PlayerUtils.toStringTime(mPlayContorl.getCurrentPosition()));


//      setPlayLoadingVisibile(false,mActivity.getString(R.string.player_loading_tip));
        // 影片缓冲成功展现整个控制栏并且5秒后消失
        showControllerLogic();
        if (mPlayData.getRecordposition() > 0 && Math.abs(mDuration-mPlayData.getRecordposition() * 1000)>1000) {
            mCurrPlayTime = mPlayData.getRecordposition();
            mPlayContorl.seekTo(mPlayData.getRecordposition() * 1000);
            setPlayLoadingVisibileHasRecord(true, mActivity.getString(R.string.player_loading_record));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPlayLoadingVisibileHasRecord(false,null);
                    LogUtil.e("xll","loading dismiss 2 second");
                }
            },2000);


        }else if( Math.abs(mDuration-mPlayData.getRecordposition() * 1000)<1000){
            mCurrPlayTime = 0;
            mPlayContorl.seekTo(0);
        }
        else if (mPlayEpisode !=null && mCurrPlayTime > 0 && last_gvid!=null && last_gvid.equals(mPlayEpisode.getGvid())) {  //PlayerUtils.SITE_CLOUDDISK.equals(mPlayData.getSite()) &&
            mPlayContorl.seekTo(mCurrPlayTime);
        }


        startPlayer();
        startPlayerTimer();
        if(mPlayEpisode!=null){
            last_gvid = mPlayEpisode.getGvid();
        }

    }


    /**
     * 播放完成状态
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放结束
        LogUtil.e("xll","OnCompletion () "+localIndex);


        //播完 播放记录上报
        if(mPlayContorl!=null&&mPlayContorl.getDuration()!=-1) {
            mCurrPlayTime = getmPlayContorl().getCurrentPosition();
            mVideoPlayerFragment.saveRecord();

        }

        if (mCanPlayNext) {
            playNext();
        } else {
            // 播放完上报 上报统一移动到videoplayerfragment中
            LogUtil.e("xll", "onCompletion 播放完成 , 上报！");
        }
    }

    // 上报播放记录
    private void reportPlayRecord() {
        LogUtil.e("reportPlayRecord","reportPlayRecord");
        buidlParam();
        if(mPlayContorl!=null){
            int playTime = mPlayContorl.getCurrentPosition() / 1000;
            if(mPlayEpisode!=null){
                Log.d("isPlaying", " play end report-----title " + mPlayEpisode.getTitle() + " id " + mPlayEpisode.getGvid() + " src " + mPlayEpisode.getSource() + " cid " + mPlayEpisode.getCategory_id());
                DataReporter.reportPlayRecord(mPlayEpisode.getGvid(),
                        mPlayEpisode.getId(),
                        mPlayEpisode.getSource(),
                        mPlayEpisode.getCategory_id(),
                        playTime,
                        UserLoginState.getInstance().getUserInfo().getToken(),
                        NetWorkUtils.getNetInfo(),
                        bucket,
                        reid);
            }
        }

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        LogUtil.e(TAG, "!!!!onInfo!!!!what!!" + what);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BUFFERING_START");
                bufferStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BUFFERING_END");
                bufferEnd();
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
               // setPlayLoadingVisibile(true);
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_NOT_SEEKABLE");
                break;
            default:
                LogUtil.e(TAG, "!!!!onInfo!!!!!!MEDIA_INFO_DEFAULT");
                break;
        }
        return false;
    }

    public void startPlayer() {
        if (null != mPlayContorl && mIsPrepared && mIsPause) {
            LogUtil.e("v1.1.2"," onprepare excute start()");
            mPlayContorl.start();
            mIsPause = false;
            if (!isplayfromweb) {
               final Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // 向本地数据库中写入数据
                        VideoItem videoItem = getCurrentVideoItem();
                        if(videoItem!=null){
                           // LogUtil.e("v1.1.2","record "+videoItem.getTitle());
                        }

                        if(!VideoPlayerFragment.isactivityonresume) {
                                timer.cancel();
                            }
                            Log.e(TAG, "task is running");
                            if(mPlayContorl!=null&&mPlayContorl.getDuration()!=-1) {
//                                mCurrPlayTime = getmPlayContorl().getCurrentPosition();
                                  mVideoPlayerFragment.saveRecord();


                            }
                    }
                };
                mCurrPlayTime = getmPlayContorl().getCurrentPosition();
                mVideoPlayerFragment.saveRecord();
                timer.schedule(task, 5000, 5000);

            }
        }else{
           // LogUtil.e("v1.1.2"," block end  no start()");
        }

    }

    public void stop() {
        if (null != mPlayContorl) {
            mPlayContorl.stopPlayback();
        }
    }


    protected void playerPause() {
        LogUtil.e("wulianshu","不知道为什么被暂停了。");
        if (null != mPlayContorl && mIsPrepared) {
            mCurrPlayTime = getCurrPosition();
//            savePlayRecord(mCurrPlayTime);
        }
        if (null != mPlayContorl) {
            mPlayContorl.pause();
        }
    }

    /**
     * 开启计时器，秒刷新界面
     */
    protected void startPlayerTimer() {
        // 当前不是暂停状态并且播放器已经准备好了才开始刷新页面
        if (!mIsPause) {
            stopPlayerTimer();
            if (mPlayerTimer == null) {
                mPlayerTimer = new PlayerTimer(mHandler, ConstantUtils.PROGRESS_CHANGE);
                Timer m_musictask = new Timer();
                m_musictask.schedule(mPlayerTimer, 0, 1000);
            }
        }
    }

    /**
     * 停止播放器计时
     */
    protected void stopPlayerTimer() {
        try {
            if (mPlayerTimer != null) {
                mPlayerTimer.cancel();
                mPlayerTimer = null;


            }
            if (null != mHandler && mHandler.hasMessages(ConstantUtils.JUDGE_BUFFER)) {
                mIsNeddJudgeBuffer = true;
                mHandler.removeMessages(ConstantUtils.JUDGE_BUFFER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前的播放位置
     */
    public int getCurrPosition() {
        if (null != mPlayContorl) {
            LogUtil.e(TAG, "当前播放位置：" + mPlayContorl.getCurrentPosition());
            return mPlayContorl.getCurrentPosition();
        }

        return 0;
    }

    /**
     * 获取影片总长
     *
     * @return
     */
    private int getDuation() {
        if (null != mPlayContorl) {
            return mPlayContorl.getDuration();
        }
        return 0;
    }


    /**
     * 设置标题栏影片名称
     */
    public void setVideoName(VideoItem episode) {
        mVideoName = episode.getTitle();
        if(episode.isLocal()){
            setTitleText(mActivity.getString(R.string.local_tag)+mVideoName);
        }else{
            setTitleText(mVideoName);
        }

    }

    /**
     * 设置顶部标题名称
     */
    private void setTitleText(String text) {
        if (null != mTitile && !TextUtils.isEmpty(text)) {
            mTitile.setVisibility(View.VISIBLE);
            if (mIsPlayLocalUrl) {
                mTitile.setText(text);
            } else {
                mTitile.setText(text);
            }
        } else if (null != mTitile && TextUtils.isEmpty(text)) {
            mTitile.setVisibility(View.GONE);
        }
    }

    /**
     * 处理消息的handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.PROGRESS_CHANGE:
                    // 显示网速
                    if(!isHasRecord){
                       // showNetRate();
                        setPlayLoadingVisibile(true,mActivity.getString(R.string.player_loading_tip));
                    }
                    if (mIsPrepared) {
                        //小于5秒切换到下一集
                        long no_play_duration = mDuration - mPlayContorl.getCurrentPosition();
                        if (no_play_duration <= 5 * 1000) {
                            if (mCanPlayNext) {
                                //切换下一集前 播放完成上报（自动切换下一集）

                                playNext();
                            } else {
                                if (mPlayContorl != null) {
                                    ac = "finish";
//                              end_time = System.currentTimeMillis();
                                    ut = 0;
                                    if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                                        play_type = 2;
                                    } else {
                                        play_type = 0;
                                    }
                                    timing = getmCurrPlayTime();
                                    vlen = mPlayContorl.getDuration() / 1000;
                                    UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "",mVideoPlayerFragment.getSeid(),mVideoPlayerFragment.getPeid());
                                }
                               // playNext();
                                LogUtil.e("xll","canPlayNext "+mCanPlayNext);
                                if (no_play_duration <= 2* 1000) {
                                    mPlayContorl.seekTo(mPlayContorl.getDuration() - 2000);
                                    clickPauseOrPlay();

                                    if(!mCanPlayNext&&mActivity.getMediaType()==ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                                        LogUtil.e("xll", "Local Play finish ");
                                       // LogUtil.e("xll", "Local Play current " + mPlayContorl.getCurrentPosition());
                                        LogUtil.e("xll", "Local Play total  " + mPlayContorl.getDuration());
                                        mActivity.finish();

                                    }

                                    if(NetWorkUtils.isNetAvailable()){
                                        reportPlayRecord();
                                    }

                                }
                            }
                        }

                        refreshPlaySeekBar();
                        //时间做赋值  检测这次时间和上次时间是否相等 相等调用  executeBufferLogic
//                        long postion = getCurrPosition();
                       // repeatSame();
                        reportBlock();
                        // 每隔3秒判断视频是否缓冲卡
//                        if (mIsNeddJudgeBuffer && !mIsPlayLocalUrl) {
//                            executeBufferLogic();
//                            mHandler.removeMessages(ConstantUtils.JUDGE_BUFFER);
//                            mHandler.sendEmptyMessageDelayed(ConstantUtils.JUDGE_BUFFER,
//                                    ConstantUtils.JUDGE_BUFFER_DELAY_TIME);
//                            mIsNeddJudgeBuffer = false;
//                        }
                    }
                    break;
//                // 控制栏消失
                case ConstantUtils.DISSMISS_MEDIACONTROLLER:
                    dissMissMediaControll();
                    setLockScreenVisibile(false);
                    hideSlideMenu();
                    break;
//                case ConstantUtils.JUDGE_BUFFER:
//                    mIsNeddJudgeBuffer = true;
//                    break;
                case Utils.GET_JS_RESULT:
                            LogUtil.e("xll","NEW js call loadUrl !");
                            mWebView.clearCache(true);
                            LogUtil.e("xll","NEW　js send params : "+mSnifferParamter);
                            mWebView.loadUrl("javascript:TestJavaScriptInterface.startFunction(dealWithRequest('" + mSnifferParamter + "'));");
                    break;
                default:
                    break;
            }
        }
    };
   boolean blockReport=false;

   boolean eblockReport=false;

    void reportBlock(){
        if(mCurrPlayTime == getCurrPosition()){
            //相等卡顿
            repeatcount++;
        }else{
            //不相等正常播放
            isblock = false;
            hideTimeOutLayout();
            repeatcount = 0;
            mCurrPlayTime = getCurrPosition();
            blockReport=false;
            //外站源播放成功需要上报
            if(mPlayEpisode!=null&&!TextUtils.isEmpty(mPlayEpisode.getSource())){
                if (!"letv".equals(mPlayEpisode.getSource()) && !"nets".equals(mPlayEpisode.getSource())) {
                    int durantion = (int)(System.currentTimeMillis()/1000)-starttime;
                    if(!isfeedbackalready){
                        if(mOutSiteData!=null){
                            if(isjscut) {
                                UploadStat.streamupload(mOutSiteData.getUrl(), stream, mOutSiteData.getRequest_format());
                                UploadStat.playfeedback(mOutSiteData.getUrl(), 1, 0, mPlayEpisode.getSource(), mPlayEpisode.getId(), durantion);
                            }else{
                                UploadStat.playfeedback(mOutSiteData.getUrl(), 2, 0, mPlayEpisode.getSource(), mPlayEpisode.getId(), durantion);
                            }
                        }

                        isfeedbackalready = true;
                    }
                }
            }
        }
        //间隔一秒
        if(repeatcount >=1){

            //卡顿上报   第一次卡顿上报Loading还没出来
            if(progressBar!=null&&progressBar.isShown()&&!blockReport) {
                if (mPlayContorl != null) {
                    ac = "block";
                    ut = 0;
                    if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                        play_type = 2;
                    } else {
                        play_type = 0;
                    }

                    timing = getmCurrPlayTime();
                    vlen = mPlayContorl.getDuration() / 1000;
                    UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "", mVideoPlayerFragment.getSeid(), mVideoPlayerFragment.getPeid());
                    blockReport=true;
                    eblockReport=false;
                    startTime=System.currentTimeMillis();
                    LogUtil.e("xll","block start time "+startTime);


                }
            }
            executeBufferLogic();
        }else {

            //eblock上报
            if (progressBar!=null&&progressBar.isShown()&&!eblockReport) {
                // 小波波加载超时逻辑
                endTime=System.currentTimeMillis();
                LogUtil.e("xll","block end time "+endTime);
                if(endTime-startTime>=1000&&!mIsPrepared){

                    showTimeOutLayout();
                }
                LogUtil.e("xll", "report eblock ");
                if (mPlayContorl != null) {
                    ac = "eblock";
                    ut = 0;
                    if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                        play_type = 2;
                    } else {
                        play_type = 0;
                    }

                    timing = getmCurrPlayTime();
                    vlen = mPlayContorl.getDuration() / 1000;
                    UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "", mVideoPlayerFragment.getSeid(), mVideoPlayerFragment.getPeid());
                    eblockReport=true;
                    LogUtil.e("xll", "handle msg eblock report ok ");

                }
            }
            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPlayLoadingVisibile(false, null);
                    }
                },500);
            }else{
                setPlayLoadingVisibile(false, null);
            }

        }

    }

    /**
     * 刷新播放进度条
     */
    public void refreshPlaySeekBar() {
        if (null != mPlayContorl && null != mPlaySeekBar) {
            int repeat = 0;
            int currPosition = mPlayContorl.getCurrentPosition();

            if (mDuration <= 0 && mPlayContorl.isPlaying()) {
                mDuration = mPlayContorl.getDuration();
            }
            long progress = mDuration <= 0 ? 0 : (PLAY_SEEKBAR_MAX * currPosition / mDuration);
            mPlaySeekBar.setProgress((int) progress);
            int percent = mPlayContorl.getBufferPercentage();
//                  LogUtil.d("dyf",percent+"");
            mPlaySeekBar.setSecondaryProgress(percent * 10);
            mPlaySeekBar.setSecondaryProgress(percent * 10);
            if (null != mCurrTime) {
                String currTimeStr = PlayerUtils.toStringTime(currPosition);
                if (!TextUtils.isEmpty(currTimeStr)) {
                    mCurrTime.setVisibility(View.VISIBLE);
                    mCurrTime.setText(currTimeStr);
                } else {
                    mCurrTime.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 刷新播放进度条
     */
    public void refreshsmallSeekBar(int position) {
        position = (int) ((position * PLAY_SEEKBAR_MAX) / mDuration);
        fast_seekbar.setProgress(position);
    }

    public void refreshplaySeekBar(int position) {
        position = (int) ((position * PLAY_SEEKBAR_MAX) / mDuration);
        mPlaySeekBar.setProgress(position);
    }

    private OnSeekBarChangeListener mPlaySeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER, ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
            int seekBarProgress = seekBar.getProgress();
            long position = 0;
            if (mDuration > 0 && null != mPlayContorl && null != mCurrTime) {
                position = (mDuration * seekBarProgress / PLAY_SEEKBAR_MAX);
//                if (mDuration - position <= 5 * 1000 && mDuration - position > 4 * 1000) {
//                    ToastUtil.showShortToast(mActivity, mActivity.getResources().getString(R.string.play_finish));
//                }
                if(Math.abs(mDuration-position)<=1*1000){
                    position = mDuration-1*1000;
                }
                mPlayContorl.seekTo((int) position);

                // 播放完毕拖动进度条 重置播放状态
                if(mIsPause){
                    mIsPause=false;
                    if(mPlayContorl!=null&&!mPlayContorl.isPlaying()){
                        mPlayContorl.start();
                        startPlayerTimer();
                        updatePlayBtnBg(mIsPause);

                    }
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopPlayerTimer();
            mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            long duration = mDuration;

            int mUserDragTime = (int) ((duration * seekBar.getProgress()) / PLAY_SEEKBAR_MAX);
            String dragTime = PlayerUtils.toStringTime(mUserDragTime);
            mCurrTime.setText(dragTime);

        }

    };


    /**
     * 拖动seekbar完成
     */
    private OnSeekCompleteListener mPlaySeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            // 当前拖动完成并且不是暂停状态时开始播放并刷新UI页面
            if (null != mPlayContorl && !mIsPause) {
                mPlayContorl.start();

                startPlayerTimer();
            }
        }
    };

    /**
     * 重置播放器相关数据
     */
    public void resetPlayData() {
        resetDataState();
        if (null != mPlayContorl) {
            mPlayContorl.stopPlayback();
            mPlayContorl = null;
        }
        stopCdeSource();

    }

    private void resetDataState() {
        mIsPause = true;
        mIsPrepared = false;
        mCanPlayNext = false;
        mIsControllerShow = false;
        PlayDecodeMananger.setmNeedSysDecoder(false);
        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
        stopPlayerTimer();
    }

    private void stopCdeSource() {
        LogUtil.e(TAG, "@@@@@@@@@@mLinkShellUrl@@@@@@@@" + mLinkShellUrl);
        if (!TextUtils.isEmpty(mLinkShellUrl)) {
            CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
            cdeHelper.stopPlay(mLinkShellUrl);
            LogUtil.e(TAG, "***********mLinkShellUrl**********" + mLinkShellUrl);
        }
    }

    /**
     * 设置控制栏展现UI
     */
    private void setControllerUI() {
        // 加载影片时不显示底部控制栏
        //本地播放不显示加载进度loading
        LogUtil.e("wulianshu","setPlayLoadingVisibile 2");
        setPlayLoadingVisibile(true,mActivity.getString(R.string.player_loading_tip));
        setMediaControllerTopVisibile(true);
        // 切换选集上下集时更新播放暂停按钮
        updatePlayBtnBg(false);
        if (null != mPlaySeekBar) {
            mPlaySeekBar.setProgress(0);
            mPlaySeekBar.setSecondaryProgress(0);
        }
    }

    /**
     * 控制loading界面显示
     *
     * @param flag
     * @param showTxt 展示文字
     */
    public void setPlayLoadingVisibile(boolean flag,String showTxt) {


        if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
            mLoadingLayout.setVisibility(View.GONE);
            return;
        }

      /* if((mLoadingLayout.getVisibility() == View.VISIBLE) && flag){//已经是在loading了
            return;
        }

        if(mLoadingLayout.getVisibility() == View.GONE && !flag){//已经是不显示loading界面了
            return;
        }*/

        isHasRecord=false;
        if (null != mLoadingLayout) {

            if (flag) {
                    progressBar.setVisibility(View.VISIBLE);
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    loading_tip_text.setVisibility(View.VISIBLE);
                    mNetRate.setVisibility(View.VISIBLE);
                    loading_tip_text.setText(showTxt) ;
                    showNetRate();
                //上报卡顿
            } else {
                // 界面切到后台丢失状态
                progressBar.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.GONE);
                loading_tip_text.setVisibility(View.GONE);
                mNetRate.setVisibility(View.GONE);
                //上报不卡
            }
        }

    }

  /**
   *   loading :有播放历史时
   * */
   boolean isHasRecord;
    public void setPlayLoadingVisibileHasRecord(boolean flag,String showTxt) {

        if (null != mLoadingLayout) {
            if (flag) {
                progressBar.setVisibility(View.VISIBLE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                loading_tip_text.setVisibility(View.VISIBLE);
                mNetRate.setVisibility(View.GONE);
                loading_tip_text.setText(showTxt) ;
                isHasRecord=true;

            } else {
                // 界面切到后台丢失状态
                progressBar.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.GONE);
                loading_tip_text.setVisibility(View.GONE);
                mNetRate.setVisibility(View.GONE);
                isHasRecord=false;

            }
        }
    }



    public void setMediaControllerTopVisibile(boolean flag) {
        if (null != mMediaControllerTop) {
            if (flag) {

                if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
                    video_player_top_id.setBackgroundColor(mActivity.getResources().getColor(R.color.color_b2000000));
                    mActivity.statusBarShow(false);
//                    controller_net_error_setting.setVisibility(View.GONE);
//                    controller_net_error_play.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMediaControllerTop.getLayoutParams();
//                    int topMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_mediacontroller_margintop);
//                  int topMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.sarrs_dimen_0dp);
                    layoutParams.setMargins(0, 0, 0, 0);
                    mMediaControllerTop.setLayoutParams(layoutParams);

                } else {
//                    controller_net_error_setting.setVisibility(View.VISIBLE);
//                    controller_net_error_play.setVisibility(View.GONE);
                    video_player_top_id.setBackgroundColor(mActivity.getResources().getColor(R.color.color_00000000));
                    mActivity.statusBarShow(true);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMediaControllerTop.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                    mMediaControllerTop.setLayoutParams(layoutParams);

                }
                mMediaControllerTop.setVisibility(View.VISIBLE);
//                NetworkInfo networkInfo = NetWorkUtils.getAvailableNetWorkInfo();
//                if(networkInfo != null) {
//                    mVideoPlayerFragment.setPlayerControllerBarState(networkInfo.getType());
//                }else{
//                    mVideoPlayerFragment.setPlayerControllerBarState(NetWorkUtils.NETTYPE_NO);
//                }
            } else {
                mMediaControllerTop.setVisibility(View.GONE);
//                setChangeClarityVisibile(false);
            }

        }
    }
   /**
    *   播放器底部控制栏
    *
    * */
    public void setMediaControllerBottomVisibile(boolean flag) {
        if (null != mMediaControllerBottmon) {
            if (flag&&!getmLockScreenBtn().isSelected()) {
                mMediaControllerBottmon.setVisibility(View.VISIBLE);
            } else {
                mMediaControllerBottmon.setVisibility(View.GONE);
            }
        }
    }

    public void showControllerLogic(){
        if (mLockScreenBtn.isSelected()) {
            if (mLockScreenBtn.isShown()) {
                mLockScreenBtn.startAnimation(mLockScreenAni);
            } else {
                setLockScreenVisibile(true);
            }
            // 有网络时延迟x秒隐藏操作栏
//            mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
//            mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER, ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
        } else {
            dynamicShowControll();
        }
    }

    /**
     * 显示当前网速
     */
    private void showNetRate() {
        // 如果当前API level > 2.2则显示当前网速
        if (mActivity != null && mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE) {
            if (Utils.getAPILevel() >= PlayerUtils.API_8) {
                if (mLoadingLayout.isShown()) {
                    String currentRate = TrafficStatsUtil.countCurRate();
                    mNetRate.setVisibility(View.VISIBLE);
                    mNetRate.setText(currentRate);
                }
            } else {
                mNetRate.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * 展现控制栏
     */
    public void showMediaControll() {
        if (null != mActivity) {
            if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
                mActivity.statusBarShow(false);
            } else {
                mActivity.statusBarShow(true);
            }

            if (Utils.getAPILevel() >= PlayerUtils.API_14 && ChaoJiShiPinVideoDetailActivity.isFullScreen) {
                PlayerUtils.showVirtualKey(mWindow);
            }
        }
        if (null != mMediaControllerTop && null != mMediaControllerBottmon) {
            mIsControllerShow = true;
            if (!mMediaControllerTop.isShown()&&!getmLockScreenBtn().isSelected()) {
                mMediaControllerTop.setVisibility(View.VISIBLE);
            }
            if (!mMediaControllerBottmon.isShown() &&!getmLockScreenBtn().isSelected()) {
                mMediaControllerBottmon.setVisibility(View.VISIBLE);
            }


        }
    }

    /**
     * 隐藏控制栏
     */
    public void dissMissMediaControll() {
        if (null != mMediaControllerTop && null != mMediaControllerBottmon) {
            mIsControllerShow = false;
            mMediaControllerTop.setVisibility(View.GONE);
            mMediaControllerBottmon.setVisibility(View.GONE);
        }
    }



    /**
     * 动态展现控制栏
     */
    private void dynamicShowControll() {
        if (mIsControllerShow) {
            dissMissMediaControll();
            setLockScreenVisibile(false);
        } else {
            if (mActivity != null) {
                if (ChaoJiShiPinVideoDetailActivity.isFullScreen){
                    setLockScreenVisibile(true);
                    dissMissMediaControll();
                }else{
                    setLockScreenVisibile(false);
                }


            }
            showMediaControll();
            mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                    ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
        }

    }

    /**
     * 执行视频缓冲逻辑
     */
    private void executeBufferLogic() {
        int position = getCurrPosition();
        int duration = getDuation();
//        LogUtils.e(TAG, "!!!!!!!!!!position!!!!" + position);
//        LogUtils.e(TAG, "!!!!!!!!!!mCurrPlayTime!!!!" + mCurrPlayTime);
        boolean isBuffer = PlayerUtils.judgeBuffering(mCurrPlayTime, position);
        if (isBuffer) {
            if (position != duration) {
                bufferStart();
            } else {
                bufferEnd();
            }
        }else {
            bufferEnd();
        }
//        else{
//            setPlayLoadingVisibileHasRecord(false, null);
//        }
//        if (position > PlayerUtils.PLAY_CORRECT_MIN_POSITION) {
//            mCurrPlayTime = getCurrPosition();
//        }
    }


    private void bufferStart() {
        LogUtil.e("xll", "bufferStart call");
        // M3U8 格式播放过程中显示loading状态屏蔽
        //卡顿上报
        if(!isHasRecord){
            LogUtil.e("wulianshu","setPlayLoadingVisibile 3");
            setPlayLoadingVisibile(true,mActivity.getString(R.string.player_loading_tip));

//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setPlayLoadingVisibile(false,mActivity.getString(R.string.player_loading_tip));
//                }
//            },1000);
        }

    }

    private void bufferEnd(){
        //卡顿结束上报
        LogUtil.e("wulianshu","setPlayLoadingVisibile 4");
//        setPlayLoadingVisibile(false,null);
        startPlayer();
    }

    public ImageView getmPlayNextBtn() {
        return mPlayNextBtn;
    }
    public void setPlayNextVisible(boolean isShow){
        if(isShow){
            mPlayNextBtn.setVisibility(View.VISIBLE);
        }else{
            mPlayNextBtn.setVisibility(View.GONE);
        }

    }

    public Button getmSlideBtn() {
        return mSlideTrggle;
    }

    public void setSlideTriggerVisible(boolean isShow){
        if(!isShow){
            if(mSlideTrggle!=null){
                mSlideTrggle.setVisibility(View.GONE);

            }

        }

    }


    public RelativeLayout getmNetErrorView() {
        return mNetLayout;
    }

    public ImageView getmNetErrorIcon() {
        return videoplayer_net_error_icon;
    }
    public void setSelectVisibile(boolean isShow) {

        if (null != mSelectBtn) {
            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                //
                if(isShow&&mPlayData!=null&&mPlayData.getmLocalDataLists()!=null&&mPlayData.getmLocalDataLists().size()>=1){
                    mSelectBtn.setVisibility(View.VISIBLE);
                    LogUtil.e("v1.1.2","play local size>=1 ");
                }else{
                    mSelectBtn.setVisibility(View.GONE);
                    LogUtil.e("v1.1.2", "play local size<1 ");
                }

            }else{
                // 剧集数大于一展示剧集按钮
                if(isShow){
                    // 从除缓存页进入断网&本地剧集>1
                     if(mActivity.getIslocaEpisoSize()&&!NetworkUtil.isNetworkAvailable(mActivity)){

                         mSelectBtn.setVisibility(View.VISIBLE);
                         LogUtil.e("v1.1.2"," epsiso view init playdata undone logic");
                         return;
                     }
                    if(mPlayData!=null&&mPlayData.getmEpisodes()!=null&&mPlayData.getmEpisodes().indexOfKey(mPlayData.getKey())>=0&&mPlayData.getmEpisodes().get(mPlayData.getKey()).size()>1){
                        mSelectBtn.setVisibility(View.VISIBLE);
                    }else{
                        mSelectBtn.setVisibility(View.GONE);
                    }
                }else{
                    mSelectBtn.setVisibility(View.GONE);
                    LogUtil.e("v1.1.2"," epsiso view gone");
                }

                }
        }
    }

    private void resetSniffToIdle() {
        mSniffRetryCount = 0;
        mHandler.removeMessages(Utils.GET_JS_RESULT);
    }

    private void retrySniff() {
        Message msg = new Message();
        msg.what = Utils.GET_JS_RESULT;
        mHandler.sendMessage(msg);

    }
    public class TestJavaScriptInterface {
       @JavascriptInterface
        public void startFunction(final  String result) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("xll", "xxxxxstartFunctionxxx" + result);
                            if (!TextUtils.isEmpty(result)) {
                                parseSniffResult(result);
                            } else {
                                handlerOutSiteError();
                            }
                        }
                    });

                }
    }

    private void parseSniffResult(String result) {
        LogUtil.e("xll", "Js parse return back !" + result);
        try {
            JSONObject obj = new JSONObject(result);
            stream = obj.getString("stream");
            if(!TextUtils.isEmpty(stream)){
                LogUtil.e("xll", "NEW sniff finish result ok");
                isQuitSniff=true;
                String fileName="jscutresult.html";
                FileUtils.writeHtmlToData(mActivity, fileName, stream);
                doPlay(PlayerUtils.DEFAULT_PLAYER_TYPE, stream);
                isjscut = true;
            }else{
                //本次截流失败
                isstream = true;
                isQuitSniff=false;
                LogUtil.e("xll", "NEW sniff finish result error");
                handlerOutSiteError();
            }


        } catch (Exception e) {
            e.printStackTrace();
            handlerOutSiteError();
        }
    }

 /**
  *   外站源异常处理
  * */

   void handlerOutSiteError(){
       if(mStream_list!=null&&mStream_list.size()>0){
           doPlay(PlayerUtils.DEFAULT_PLAYER_TYPE, mStream_list.get(0));
       }else{
           jumpToWebPlayeActivity();
       }


    }

    /**
     * 锁屏显隐
     *
     * @param flag
     */
    private void setLockScreenVisibile(boolean flag) {
        if (null != mLockScreenBtn) {
            if (flag) {
                mLockScreenBtn.setVisibility(View.VISIBLE);
            } else {
                mLockScreenBtn.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 创建锁屏动画
     *
     * @param context
     */
    private Animation makeBlinkAnimation(Context context) {
        Animation blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.blink_alpha);
        blinkAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                        ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
            }
        });
        return blinkAnimation;
    }

    private void setSelectClickable(boolean flag) {
        if (null != mSelectBtn) {
            mSelectBtn.setClickable(flag);
        }
    }

    public ImageButton getmLockScreenBtn() {
        return mLockScreenBtn;
    }

    /**
     * 展示播放器全屏剧集  //
     * 电影剧集不需要展示
     */

    public void showAnimSelect() {
        if(mPlayData==null){
            LogUtil.e("v1.1.2","playdata is null not show AnimaSelect");
            return;
        }
        if (mPlayData != null && !TextUtils.isEmpty(mPlayData.getCid())) {
            if (mPlayData.getCid().equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_2))) {
                return;
            }
        }


        if (isSlide) {
            hideSlideMenu();
        }
        if (hasAnim) {
            mSelectLinearLayout.clearAnimation();
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 0);
            mShowAction.setDuration(300);
            mSelectLinearLayout.startAnimation(mShowAction);
        }
        mSelectLinearLayout.setVisibility(View.VISIBLE);
        if (tagAdapter == null) {
            tagAdapter = new VideoDetailFuScreeenExpandListAdapter(mPlayData.getPage_titles(), mActivity, mEpisodes, mPlayData.getCid());
            mSelectListView.setAdapter(tagAdapter);
        } else {
            tagAdapter.setFenyeList(mPlayData.getmEpisodes());
           // LogUtil.e(TAG,"juji"+mEpisodes.toString());
            tagAdapter.notifyDataSetChanged();
        }

        // 显示剧集 隐藏顶部以及底部操作栏
        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER, 0);
        isShowEpiso = true;

    }





    /**
     * 隐藏播放器全屏剧集
     */

    boolean isShowEpiso;
    boolean hasAnim = true;
   /**
    *  隐藏剧集动画
    * */
    public void hideAnimSelect(int duringTime) {
        if (hasAnim) {
            mSelectLinearLayout.clearAnimation();
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0);
            mHiddenAction.setDuration(duringTime);
            mSelectLinearLayout.startAnimation(mHiddenAction);
            LogUtil.e("xll","controller measure select width "+mSelectLinearLayout.getMeasuredWidth());
           /* RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            ll.setMargins(mSelectLinearLayout.getMeasuredWidth(), 0, 0, 0);
            mSelectLinearLayout.setLayoutParams(ll);*/
        }
        mSelectLinearLayout.setVisibility(View.GONE);
        isShowEpiso = false;
    }
    /**
     *  隐藏剧集
     *  @param isVisible
     * */
    public void setmSelectLaoutVisible(boolean isVisible) {
        if(isVisible){
            mSelectLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mSelectLinearLayout.setVisibility(View.GONE);
        }

    }
    boolean isWebLoaded=false;

    void setIsLoaded(boolean isLoad){
        this.isWebLoaded=isLoad;
    }

    //@TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Activity activity) {
        setConfigCallback((WindowManager) activity.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE));
        mWebView = new WebView(mActivity);
        try {
            mWebView.getSettings().setJavaScriptEnabled(true);
        }catch(Throwable e){
            e.printStackTrace();
        }
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);
        //请求jscode 接口
        UpdateSnifferManager.getInstance(activity).startUpdate();
        String htmlUrl = UpdateSnifferManager.getInstance(activity).getHtmlURL();
        LogUtil.e("xll", "NEW js file get from " + htmlUrl);
        mWebView.loadUrl(htmlUrl);
        mWebView.addJavascriptInterface(mTestInterface, "TestJavaScriptInterface");
        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, " url:" + url);


                view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //
                setIsLoaded(true);
                LogUtil.e("xll", " NEW js webview loaded ok");
               // mWebView.loadUrl(htmlUrl);

            }

        });

//        mWebView.evaluateJavascript("TestJavaScriptInterface", new ValueCallback<String>() {
//
//            @Override
//            public void onReceiveValue(String value) {
//                Log.d(TAG, "onReceiveValue value=" + value);
//
//
//            }
//        });



    }

    /**
     * WebView防止内存泄露
     *
     * @param windowManager zhangshuo 2014年12月25日 下午5:38:12
     */
    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);
            if (null == configCallback) {
                return;
            }
            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {

        }
    }

    public boolean ismEpisoShow() {
        return isShowEpiso;
    }


    public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view.getTag() instanceof VideoDetailFuScreeenExpandListAdapter.EpisodesTagHolder
                    && parent.getAdapter() instanceof VideoDetailFuScreeenExpandListAdapter) {
                VideoDetailFuScreeenExpandListAdapter tagAdapter = (VideoDetailFuScreeenExpandListAdapter) parent.getAdapter();
                if (tagAdapter.getInStatePosition(VideoDetailFuScreeenExpandListAdapter.STATE_EXPANDED) == position) {
                    tagAdapter.collaspPosition(position);
                } else if (tagAdapter.getCachedData(position) != null) {
                    LogUtil.e("Cache ", " not null expand");
                    tagAdapter.expandPosition(position, mEpisodes);
                    parent.setSelection(position);

                } else {
                    LogUtil.e("Cache ", " is null expand");
                    // 发送跨分页请求给半屏页
                    tagAdapter.setPositionInLoading(position);
                    VideoPlayerNotifytData notifytData = new VideoPlayerNotifytData();
                    notifytData.setIsFirst(false);
                    // 点击不需要更新剧集点击位置,但是需要保存之前点击位置
                    notifytData.setKey(mNotifyData.getKey());
                    notifytData.setPosition(mNotifyData.getPosition());
                    notifytData.setReqKey(position);
                    notifytData.setReqPosition(0);
                    LogUtil.e("Media", "request updatekey" + position);
                    //
                    LogUtil.e("Media", "request key" + mNotifyData.getKey());
                    LogUtil.e("Media", "request  positon" + mNotifyData.getPosition());
                    notifytData.setType(ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK);
                    EventBus.getDefault().post(notifytData);
                }
            }

        }
    }


    private void releaseMap() {
        if (null != mApiMap) {
            mApiMap.clear();
            mApiMap = null;
        }

        if (null != mRuleMap) {
            mRuleMap.clear();
            mRuleMap = null;
        }

        if (null != mPlayUrlMap) {
            mPlayUrlMap.clear();
            mPlayUrlMap = null;
        }
    }

    public void showGSMNetView() {
        mNetLayout.setVisibility(View.VISIBLE);
        controller_net_error.setText(mActivity.getResources().getString(R.string.RPG_net_tip));
        controller_net_error_play.setVisibility(View.VISIBLE);
        controller_net_error_setting.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        hideBottomControllView();
    }

    public void showWIFINetView() {
        mNetLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        controller_net_error_play.setVisibility(View.GONE);
        controller_net_error_setting.setVisibility(View.GONE);
        showBottomControllView();

    }

    public void showNoNetView() {
        mNetLayout.setVisibility(View.VISIBLE);
        controller_net_error.setText(mActivity.getResources().getString(R.string.nonet_tip));
        controller_net_error_play.setVisibility(View.GONE);
        controller_net_error_setting.setVisibility(View.VISIBLE);
        setPlayLoadingVisibile(false,null);
        hideBottomControllView();
    }

    public ImageView getmVideoPlayerNetErrorIcon() {
        return videoplayer_net_error_icon;
    }


    public void setNetErrorMsg(String error) {
        controller_net_error.setText(error);
    }

    public void hideNetView() {
        mNetLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    public void hideBottomControllView() {
//        if (mMediaControllerBottmon.isShown()) {
            mMediaControllerBottmon.setVisibility(View.GONE);
//        }
    }

    public void showBottomControllView() {
        if (!mMediaControllerBottmon.isShown()&&!getmLockScreenBtn().isSelected()) {
            mMediaControllerBottmon.setVisibility(View.VISIBLE);
        }

    }


    private class Gesturelistener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        private int verticalMinDistance = 20;
        private int minVelocity = 0;

        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            LogUtil.e("xll"," action down Tap ");
            showControllerLogic();
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            LogUtil.e("xll"," single Tap ");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                return false;
            }
            if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
            } else {
                mActivity.setFullScreen();
                mActivity.statusBarShow(false);
                //切换成横屏
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
            switch (e.getAction()) {
                case MotionEvent.ACTION_UP:
                    fast_toast.setVisibility(View.GONE);
                    break;
            }
        }

        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            fast_toast.setVisibility(View.GONE);
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            if (mLockScreenBtn.isSelected()) {
                return false;
            }
            float length = 0f;
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            mVideoViewPosition.measure(w, h);
            int width = mVideoViewPosition.getMeasuredWidth();
            //获取当前的播放进度
            //获取总长度
            length = Math.abs(e1.getX() - e2.getX());
            int currentposition = mPlayContorl.getCurrentPosition();

            fast_seekbar.setProgress(currentposition);


            fast_toast.setVisibility(View.VISIBLE);
            progress_change = (int) ((length * mDuration) / width);
            progress_change = progress_change / FAST_COUNT;
            if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(distanceX) > minVelocity) {
//              切换Activity
                fast_icon.setBackgroundResource(R.drawable.fast_backward);
                progress_change = 0 - progress_change;
            } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(distanceX) > minVelocity) {
//                获取控件的宽度
//              ToastUtil.showShortToast(mActivity, "快进：" + progress_change);
                fast_icon.setBackgroundResource(R.drawable.fast_forward);
            }
            int temp_progress = getCurrPosition() + progress_change;
            if (temp_progress < 0) {
                temp_progress = 0;
            }
            now_length.setText(PlayerUtils.toStringTime(temp_progress));
            refreshsmallSeekBar(temp_progress);
            refreshplaySeekBar(temp_progress);


            if(mIsPause){
                mIsPause = false;
                mPlayContorl.start();
                startPlayerTimer();
            }
            updatePlayBtnBg(mIsPause);
            return false;
        }

        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            return true;
        }

    }



    /**
     * 获取当前播放的ViedeoItem
     */
    public VideoItem getCurrentVideoItem() {
        return mPlayEpisode;
    }


    /**
     *  获取当前播放器数据
     * */
     public PlayData getCurrentPlayData(){
         return mPlayData;
     }

    /**
     * 收藏状态更新
     */
    public void updateSaveStatus()
    {
        if (mActivity != null && ic_loving != null) {
            if (mActivity.isSave()) {
                ic_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
            } else {
                ic_loving.setImageResource(R.drawable.vedio_detail_loving_normal);
            }
        }
    }

    public boolean isLockScreen(){
      return mLockScreenBtn.isSelected();
    }

    /**
     * 大数据上报
     */
    public void uploadstat(int recoderposition){
       if( mPlayContorl !=null) {
           ac = "finish";
//      end_time = System.currentTimeMillis();
           ut = 0;
           if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
               play_type = 2;
           } else {
               play_type = 0;
           }
           timing = recoderposition/1000;
           vlen = mPlayContorl.getDuration() / 1000;
           UploadStat.uploadplaystat(mPlayEpisode, ac, ut + "", retry + "", play_type + "", code_rate, mVideoPlayerFragment.getRef(), timing + "", vlen + "",mVideoPlayerFragment.getSeid(),mVideoPlayerFragment.getPeid());
       }
    }
 }
