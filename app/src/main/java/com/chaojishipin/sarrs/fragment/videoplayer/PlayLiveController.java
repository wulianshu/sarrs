package com.chaojishipin.sarrs.fragment.videoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinLivePlayActivity;
import com.chaojishipin.sarrs.bean.LivePlayData;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.CDEManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.TrafficStatsUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.player.LetvMediaPlayerControl;
import com.letv.component.player.LetvVideoViewBuilder;
import com.letv.pp.func.CdeHelper;
import com.letv.pp.service.CdeService;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by wangyemin on 2016/1/26.
 */
public class PlayLiveController implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, View.OnTouchListener {

    private final static String TAG = "PlayLiveController";

    private String mLiveLinkshellUrl = "";

    /**
     * 因为一个电视台的直播地址有多条，所以标示当前播放到了第几条数据.默认从头开始播放
     */
    private int mLivePosition = 0;

    private boolean mIsprepared = false;

    private boolean mIsReceiveError = false;

    private ChaojishipinLivePlayActivity mActivityLive;

    private LivePlayData mPlayData;

    private LetvVideoViewBuilder mVideoViewBuilder;


    /**
     * 播放器所处的位置
     */
    private RelativeLayout mVideoViewPosition;

    /**
     * 播放需要用到的View
     */
    private View mPlayerView;

    private LetvMediaPlayerControl mPlayContorl;

    private CDEManager mCDEManager;

    private CDEStatusReceiver mCdeStatusReiver;

    private android.content.DialogInterface.OnClickListener mDialogOnClickListener;

    private DialogInterface.OnKeyListener mDialogOnKeyListener;

    private Window mWindow;

    protected PlayerTimer mPlayerTimer;

    private TextView mTitleName;
    private RelativeLayout mMediaControllerTop;

    /**
     * 加载相关布局
     */
    private TextView mNetRate;
    private TextView mLoading_tip_text;
    private LinearLayout mLoadingLayout;

    /**
     * 播放地址全部无效，加载超时布局
     */
    private RelativeLayout mPlayErrorLayout;
    private Button mPlayErrorView;
    /**
     * 断网布局
     */
    private Button controller_net_error_play;
    private RelativeLayout mNetLayout;
    private ImageView videoplayer_net_error_icon;
    private TextView controller_net_error;
    private Button controller_net_error_setting;

    private boolean isNoNet = false; //无网络标示
    private int retryCount; // 弱网重试机制
    private String ac = "-";
    private long timing = 0; //毫秒  已经播放的视频长度
    private long begin_time;
    private long end_time;
    private long ut;//动作耗时 毫秒
//    private int vlen;//视频的总长 秒为单位
    private int retry;//重试次数
    private int play_type;//播放类型
    private String code_rate = "-";//码流对照表
    private String ref = "-";
    private int repeatcount = 0;
    private String pageid = "00S002009_1";
    private long startTime = 0l;
    private boolean isblocked = false;

    public PlayLiveController(ChaojishipinLivePlayActivity mPlayLiveActivity) {
        mActivityLive = mPlayLiveActivity;
        mVideoViewBuilder = LetvVideoViewBuilder.getInstants();
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mTitleName = (TextView) mActivityLive.findViewById(R.id.tv_video_title);
        mMediaControllerTop = (RelativeLayout) mActivityLive.findViewById(R.id.mediacontroller_top);
        mVideoViewPosition = (RelativeLayout) mActivityLive.findViewById(R.id.videoview_position);
        // 加载相关
        mNetRate = (TextView) mActivityLive.findViewById(R.id.loading_net_rate);
        mLoading_tip_text = (TextView) mActivityLive.findViewById(R.id.loading_tip_text);
        mLoadingLayout = (LinearLayout) mActivityLive.findViewById(R.id.layout_loading);
        // 加载超时界面
        mPlayErrorLayout = (RelativeLayout) mActivityLive.findViewById(R.id.videoplayer_load_timeout_layout);
        mPlayErrorView = (Button) mActivityLive.findViewById(R.id.controller_load_timeout_refresh);
        // 断网界面
        controller_net_error_play = (Button) mActivityLive.findViewById(R.id.controller_net_error_play);
        mNetLayout = (RelativeLayout) mActivityLive.findViewById(R.id.videoplayer_net_error_layout);
        videoplayer_net_error_icon = (ImageView) mActivityLive.findViewById(R.id.videoplayer_net_error_icon);
        controller_net_error = (TextView) mActivityLive.findViewById(R.id.controller_net_error);
        controller_net_error_setting = (Button) mActivityLive.findViewById(R.id.controller_net_error_setting);
        if (mActivityLive.getmSysAPILevel() >= PlayerUtils.API_14) {
            // 设置虚拟键显示和隐藏的监听
            mWindow = mActivityLive.getmWindow();
        }
        setControllerUIIdle();
    }

    private void setListener() {
        mMediaControllerTop.setOnTouchListener(this);
        mVideoViewPosition.setOnTouchListener(this);
        mPlayErrorView.setOnClickListener(this);
        controller_net_error_play.setOnClickListener(this);
        controller_net_error_setting.setOnClickListener(this);
    }

    /**
     * 设置控制台面板为初始状态
     */
    private void setControllerUIIdle() {
        showMediaControll(false);
        setPlayLoadingVisibile(true);
        setPlayNetLayoutVisibile(false);
        setPlayErrorLayoutVisibile(false);
    }

    private void initData() {
        mCDEManager = CDEManager.getInstance(mActivityLive);
        mDialogOnClickListener = new ExitDialogOnClickListener();
        mDialogOnKeyListener = new ExitOnKeyListener();
        startPlayerTimer();
    }

    public void setmPlayData(LivePlayData playData) {
        LogUtil.e(Utils.LIVE_TAG, "##########click into live not resume##########");
        this.mPlayData = playData;
        isNoNet = false;
        if (null != mPlayData) {
            setTitleName(mPlayData.getTitle());
            // 如果当前播放器准备成功，则开始播放直播数据
            if (prepareLivePlayer()) {
                // 初始化直播数据位置
                mLivePosition = 0;
                checkCDEStatus();
            }
        }
    }

    private boolean isStreamListEnd(ArrayList<String> streams) {
        boolean isEnd = false;
        try {
            if (mLivePosition >= 0 && mLivePosition < (streams.size() - 1)) {
                isEnd = false;
            } else {
                isEnd = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isEnd;
    }

    private void checkCDEStatus() {
        CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!CDE服务是否成功启动!!!!!!!!!!!!" + cdeHelper.isReady());
        if (cdeHelper.isReady()) {
            playLiveSource();
        } else {
            mCdeStatusReiver = new CDEStatusReceiver();
            mActivityLive.registerReceiver(mCdeStatusReiver, new IntentFilter(
                    CdeService.ACTION_CDE_READY));
            mCDEManager.startCde();
        }
    }

    /**
     * 播放直播资源 zhangshuo 2015年2月4日 下午2:47:20
     */
    private String url;
    private void playLiveSource() {
        ArrayList<String> streams = (ArrayList) mPlayData.getLiveStreams();
        if (null != streams && streams.size() > 0) {
            String liveOrgUrl = streams.get(mLivePosition);
            String result = getLiveUrl(liveOrgUrl);
            url = result;
            LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!当前播放的直播地址!!!!!!!" + result);
            setVideoPath(result);
        } else {
            LogUtil.e(Utils.LIVE_TAG, "###############streams is null################");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.PROGRESS_CHANGE:
                    showNetRate();
                    break;
                case ConstantUtils.DISSMISS_MEDIACONTROLLER:
                    dissMissMediaControll();
                    break;
                default:
                    break;
            }
        }
    };

    private void setTitleName(String name) {
        if (null != mTitleName) {
            if (!TextUtils.isEmpty(name)) {
                mTitleName.setVisibility(View.VISIBLE);
                mTitleName.setText(name);
            } else {
                mTitleName.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取直播的播放地址
     *
     * @param orgLiveUrl
     * @return
     */
    private String getLiveUrl(String orgLiveUrl) {
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!!!!!!!!!!!!before addPlatCode orgLiveUrl is " + orgLiveUrl);
        LogUtil.e("wym", "#####################origin liveUrl is " + orgLiveUrl);
        if (!TextUtils.isEmpty(orgLiveUrl)) {
            String livePLayUrl = "";
            if (orgLiveUrl.contains("splatid"))
                livePLayUrl = orgLiveUrl.replace("splatid=1047", "splatid=1060201002");
            else
                livePLayUrl = orgLiveUrl + "&splatid=1060201002";
            LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!!!!!!!!!!!!after add splatid livePlayUrl is " + livePLayUrl);
            LogUtil.e("wym", "!!!!!!!!!!!!!!!!!!!!!after add splatid livePlayUrl is " + livePLayUrl);
            CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
            mLiveLinkshellUrl = cdeHelper.getLinkshellUrl(livePLayUrl);
            return cdeHelper.getPlayUrl(mLiveLinkshellUrl);
        }
        return orgLiveUrl;
    }

    private boolean prepareLivePlayer() {
        try {
            removeCurrPlayerView();
            // 直播数据默认采用m3u8播放器播放
            mPlayContorl = mVideoViewBuilder.build(mActivityLive, LetvVideoViewBuilder.Type.MOBILE_H264_M3U8);
            mPlayerView = mPlayContorl.getView();
            // 将播放器对象添加至容器中
            mVideoViewPosition.addView(mPlayerView);
            setPlayerListener();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置播放器的相关监听事件
     */
    private void setPlayerListener() {
        mPlayContorl.setOnPreparedListener(this);
        mPlayContorl.setOnCompletionListener(this);
        mPlayContorl.setOnBufferingUpdateListener(this);
        mPlayContorl.setOnErrorListener(this);
        mPlayContorl.setOnInfoListener(this);
        mVideoViewPosition.setOnTouchListener(this);
    }


    private void setVideoPath(String playUrl) {
        mIsprepared = false;
        mActivityLive.isFromBackground = false;
        mIsReceiveError = false;
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!mLivePosition!!!!!!!!!!!" + mLivePosition);
        if (!TextUtils.isEmpty(playUrl)) {
            mPlayContorl.setVideoPath(playUrl);
        }
    }

    public void onResume() {
        LogUtil.e(Utils.LIVE_TAG, "!!!!onResume mActivityLive.isFromBackground!!!!"
                + mActivityLive.isFromBackground);
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!mIsReceiveError is " + mIsReceiveError);
        // 只有之前缓冲
        if (null != mActivityLive
                && mActivityLive.isFromBackground) {
            normalPlay();
        }
    }

    public void onPause() {
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!onPause!!!!!!!!");
        // 目前mIsReceiveError为true 只有一种情况,即当所有地址均尝试过后.弹出错误提示框用户还没有点击重试时
        if (!mIsReceiveError) {
            playerExitCompletely();
        }
    }

    public void continePlayLive() {
        if (null == mPlayContorl) {
            prepareLivePlayer();
        }
        setControllerUIIdle();
        startPlayerTimer();
        checkCDEStatus();
    }

    /**
     * 退出的Dialog监听
     *
     * @author zhangshuo
     */
    private class ExitDialogOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            exitForDialog();
        }
    }

    private class ExitOnKeyListener implements DialogInterface.OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            LogUtil.e(Utils.LIVE_TAG, "!!!!ExitOnKeyListener!!!!!!!");
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                dialog.dismiss();
                exitForDialog();
            }
            return false;
        }
    }

    private void exitForDialog() {
        exitNormal();
        if (null != mActivityLive) {
            mActivityLive.finish();
        }
    }

    public void exitNormal() {
        mIsprepared = false;
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!exitNormal!!!!!!");
        playerExitCompletely();
        removeCurrPlayerView();
    }

    private void removeCurrPlayerView() {
        // 根据当前的播放器类型创建播放器View
        if (null != mPlayerView) {
            mVideoViewPosition.removeView(mPlayerView);
        }
    }

    private class CDEStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            if (CdeService.ACTION_CDE_READY.equals(intent.getAction())) {
                LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!CDE成功启动!!!!!!!!!!!!!!!!!");
                unRegisterReceiver();
                playLiveSource();
            }
        }
    }

    public void unRegisterReceiver() {
        if (null != mActivityLive && null != mCdeStatusReiver) {
            mActivityLive.unregisterReceiver(mCdeStatusReiver);
            mCdeStatusReiver = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsprepared) {
            return false;
        }
        switch (v.getId()) {
            case R.id.mediacontroller_top:
                return true;
            case R.id.videoview_position:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dynamicShowControll();
                }
                return true;
            default:
                break;
        }
        return false;
    }

    private void dynamicShowControll() {
        if (mMediaControllerTop.isShown()) {
            dissMissMediaControll();
        } else {
            showMediaControll(true);
        }
    }

    /**
     * 展现控制栏 zhangshuo 2014年5月4日 下午8:41:41
     */
    private void showMediaControll(boolean isDelayDismiss) {
        if (null != mActivityLive) {
//            mActivityLive.statusBarShow(true);
            mActivityLive.statusBarShow(false);
            if (mActivityLive.getmSysAPILevel() >= PlayerUtils.API_14) {
                PlayerUtils.showVirtualKey(mWindow);
            }
        }
        setMediaControllerTopVisibile(true);
        if (isDelayDismiss) {
            sendHideControMsg();
        }
    }


    /**
     * 隐藏控制栏 zhangshuo 2014年5月4日 下午8:42:00
     */
    private void dissMissMediaControll() {
        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
        if (null != mActivityLive) {
            mActivityLive.statusBarShow(false);
            // 隐藏虚拟键操作
            if (mActivityLive.getmSysAPILevel() >= PlayerUtils.API_14) {
                PlayerUtils.hideVirtualKey(mWindow);
            }
        }
        setMediaControllerTopVisibile(false);
    }


    private void sendHideControMsg() {
        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(ConstantUtils.DISSMISS_MEDIACONTROLLER,
                ConstantUtils.MEDIA_CONTROLLER_DISMISS_TIME);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!what!!" + what);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BUFFERING_START");
                bufferStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BUFFERING_END");
                bufferEnd();
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!!!MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!!!MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                LogUtil.e(Utils.LIVE_TAG, "!!!!onInfo!!!!!!MEDIA_INFO_NOT_SEEKABLE");
                break;
            default:
                break;
        }
        return false;
    }

    private void bufferStart() {
        setPlayLoadingVisibile(true);
    }

    private void bufferEnd() {
        setPlayLoadingVisibile(false);
        startPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.e(Utils.LIVE_TAG, "##############onError called#######################");
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!mp!!!!" + mp + "!!!!!!what!!!!!" + what + "!!!!!extra!!!!!!" + extra);
        // 收到错误后立即切换下一条Url，如果所有的播放地址均已尝试过了则弹出错误提示框从头开始
        handleLiveError();
        if (!NetWorkUtils.isNetAvailable()) {
            LogUtil.e(Utils.LIVE_TAG, "#############onError net is unavailable##############");
            setPlayLoadingVisibile(false);
        }
        return false;
    }

    private void handleLiveError() {
        LogUtil.e(Utils.LIVE_TAG, "#############handleLiveError called#############");
        if (!mIsReceiveError) {
            mIsReceiveError = true;
            // 播放器首先完全退出
            playerExitCompletely();
            if (null != mPlayData) {
                ArrayList<String> streams = (ArrayList) mPlayData.getLiveStreams();
                if (!isStreamListEnd(streams)) {
                    ++mLivePosition;
                    LogUtil.e(Utils.LIVE_TAG, "!!!!onError!!!mLivePosition!!!" + mLivePosition);
                    continePlayLive();
                } else {
                    LogUtil.e(Utils.LIVE_TAG, "!!!!onError!!!mLivePosition out of index!!!");
                    setPlayLoadingVisibile(false);
                    // 已经尝试过所有的播放地址了，弹出错误提示框提示用用户重试
                    setPlayErrorLayoutVisibile(true);
                }
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // 由于直播属于持续的节目，如果收到播放完成的回调则判断当前直播是否还在播放，如果正在播放则忽略，没有播放则按出错处理
        if (!isVideoPlaying()) {
            handleLiveError();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        ac = "init";
        begin_time = System.currentTimeMillis();
        ut = System.currentTimeMillis() - begin_time;
        play_type = 1;
        timing = 0;
        LogUtil.e("wulianshu","ac = init");
        UploadStat.uploadplaystat(mActivityLive.getLiveDataEntity(), ac, ut + "", retry + "", play_type + "", code_rate, mActivityLive.getRef(), timing + "", "-", "-", mActivityLive.getPeid(), pageid);
        ut = 0;
        mIsprepared = true;
        setPlayLoadingVisibile(false);
        sendHideControMsg();
        startPlayer();


        ac = "play";
        play_type = 1;
        timing = 0;
        //Object object,String ac,String ut,String retry,String play_type,String code_rate,String ref,String timing,String vlen

        LogUtil.e("wulianshu", "ac = play");
        UploadStat.uploadplaystat(mActivityLive.getLiveDataEntity(), ac, ut + "", retry + "", play_type + "", code_rate, mActivityLive.getRef(), timing + "", "-", "-", mActivityLive.getPeid(), pageid);
    }

    public void startPlayer() {
        if (null != mPlayContorl) {
            mPlayContorl.start();
        }
    }

    public void pausePlayer() {
        LogUtil.e(Utils.LIVE_TAG, "!!!!!mPlayContorl.isPlaying()!!!!!!" + mPlayContorl.isPlaying());
        if (null != mPlayContorl && mPlayContorl.isPlaying() && mPlayContorl.canPause()) {
            mPlayContorl.pause();
        }
    }

    public void stopPlayer() {
        if (null != mPlayContorl) {
            mPlayContorl.stopPlayback();
        }
    }

    private void stopCdeSource() {
        if (!TextUtils.isEmpty(mLiveLinkshellUrl)) {
            CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
            cdeHelper.stopPlay(mLiveLinkshellUrl);
        }
    }

    /**
     * 播放器完全退出 zhangshuo 2015年2月3日 下午5:33:35
     */
    public void playerExitCompletely() {
        // 停止计时
        stopPlayerTimer();
        pausePlayer();
        stopPlayer();
        stopCdeSource();
    }

    public boolean isVideoPlaying() {
        if (null != mPlayContorl) {
            return mPlayContorl.isPlaying();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 用户点击了出错重试按钮
            case R.id.controller_load_timeout_refresh:
                retry++;
                // 首先重置当前的播放位置
                mLivePosition = 0;
                // 然后选取当前位置的播放地址开始播放
                continePlayLive();
                break;
            case R.id.controller_net_error_play:
                // 移动网络 点击播放
                if (mPlayData == null) {
                    ToastUtil.showShortToast(mActivityLive, mActivityLive.getResources().getString(R.string.play_no_data));
                    return;
                }
                // 可能存在弱网情况，需要先显示loading
                setPlayLoadingVisibile(true);
                retryCount = 0;
                userClickPlay();
                break;
            case R.id.controller_net_error_setting:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                mActivityLive.startActivity(intent);
                mActivityLive.startActivityForResult(intent, mActivityLive.NET_SETTING_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    private void setMediaControllerTopVisibile(boolean flag) {
        if (null != mMediaControllerTop) {
            if (flag) {
                mMediaControllerTop.setVisibility(View.VISIBLE);
            } else {
                mMediaControllerTop.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 加载ui
     *
     * @param flag
     */
    private void setPlayLoadingVisibile(boolean flag) {
        if (null != mLoadingLayout) {
            if (flag) {
                if (!mLoadingLayout.isShown()) {
                    // 显示
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mLoading_tip_text.setVisibility(View.VISIBLE);
                    mLoading_tip_text.setText(mActivityLive.getResources().getString(R.string.player_loading_tip));
                    LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!!!!!block!!!!!!!!!!!!!!");

                    ac = "block";
                    ut = 0;
                    play_type = 1;
                    UploadStat.uploadplaystat(mActivityLive.getLiveDataEntity(), ac, ut + "", retry + "", play_type + "", code_rate, mActivityLive.getRef(), "-", "-", "-", mActivityLive.getPeid(), pageid);
                    startTime = System.currentTimeMillis();
                    isblocked = true;
                }
            } else {
                if (mLoadingLayout.isShown()) {
                    mLoading_tip_text.setVisibility(View.GONE);
                    if (isblocked) {
                        ac = "eblock";
                        ut = 0;
                        play_type = 1;
                        ut = System.currentTimeMillis() - startTime;
                        UploadStat.uploadplaystat(mActivityLive.getLiveDataEntity(), ac, ut + "", retry + "", play_type + "", code_rate, mActivityLive.getRef(), timing + "", "-", "-", mActivityLive.getPeid(), pageid);
                        LogUtil.e("xll", "handle msg eblock report ok ");
                        // 不显示
                        mLoadingLayout.setVisibility(View.GONE);
                        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!!!!!eblock!!!!!!!!!!!!!!");
                        isblocked = false;
                    }
                }
            }
        }
    }

    /**
     * 地址全部无效，加载超时
     *
     * @param flag
     */
    public void setPlayErrorLayoutVisibile(boolean flag) {
        if (null != mPlayErrorLayout) {
            if (flag) {
                mPlayErrorLayout.setVisibility(View.VISIBLE);
            } else {
                mPlayErrorLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 无网络或移动网络相关ui显示与否
     *
     * @param flag
     */
    public void setPlayNetLayoutVisibile(boolean flag) {
        if (null != mNetLayout) {
            if (flag) {
                mNetLayout.setVisibility(View.VISIBLE);
            } else {
                mNetLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示移动网络界面ui
     */
    public void showGSMNetView() {
        setPlayNetLayoutVisibile(true);
        controller_net_error.setText(mActivityLive.getResources().getString(R.string.RPG_net_tip));
        controller_net_error_play.setVisibility(View.VISIBLE);
        controller_net_error_setting.setVisibility(View.GONE);
        setPlayLoadingVisibile(false);
    }

    /**
     * 无网络，设置网络ui
     */
    public void showNoNetView() {
        setPlayNetLayoutVisibile(true);
        controller_net_error.setText(mActivityLive.getResources().getString(R.string.nonet_tip));
        controller_net_error_setting.setVisibility(View.VISIBLE);
        controller_net_error_play.setVisibility(View.GONE);
        setPlayLoadingVisibile(false);
    }

    /**
     * 显示当前网速
     */
    private void showNetRate() {
        // 如果当前API level > 2.2则显示当前网速
        if (mActivityLive.getmSysAPILevel() >= PlayerUtils.API_8) {
            if (mLoadingLayout.isShown()) {
                String currentRate = TrafficStatsUtil.countCurRate();
                mNetRate.setVisibility(View.VISIBLE);
                mNetRate.setText(currentRate);
            }
        } else {
            mNetRate.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 开启计时器，秒刷新界面
     */
    protected void startPlayerTimer() {
        // 当前不是暂停状态并且播放器已经准备好了才开始刷新页面
        stopPlayerTimer();
        if (mPlayerTimer == null) {
            mPlayerTimer = new PlayerTimer(mHandler, ConstantUtils.PROGRESS_CHANGE);
            Timer m_musictask = new Timer();
            m_musictask.schedule(mPlayerTimer, 0, 1000);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChaojishipinLivePlayActivity getmActivityLive() {
        return mActivityLive;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub
    }

    /**
     * 网络状态变化，控制UI显隐
     *
     * @param netType 网络状态 -1 无网络  1 wifi 0 3G
     */
    public void setPlayerControllerBarState(int netType) {
        LogUtil.e(Utils.LIVE_TAG, "##############setPlayerControllerBarState called##############");
        if (netType == NetWorkUtils.NETTYPE_WIFI) {
            LogUtil.e(Utils.LIVE_TAG, "##################cur netType is wifi##################");
            setPlayNetLayoutVisibile(false);
            // 无网络切换到有网络调用
            if (isNoNet && !isVideoPlaying()) {
                isNoNet = false;
                normalPlay();
            } else
                LogUtil.e(Utils.LIVE_TAG, "############is playing#############");
        } else if (netType == NetWorkUtils.NETTYPE_GSM) {
            LogUtil.e(Utils.LIVE_TAG, "##################cur netType is gsm##################");
            onPause();
            showGSMNetView();
            Toast.makeText(mActivityLive, mActivityLive.getResources().getString(R.string.RPG_net_tip), Toast.LENGTH_SHORT).show();
        } else if (netType == NetWorkUtils.NETTYPE_NO) {
            LogUtil.e(Utils.LIVE_TAG, "##################cur netType is no##################");
            isNoNet = true;
            onPause();
            showNoNetView();
            //把小波波加载的精疲力尽去掉
            mPlayErrorLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 正常播放
     */
    private void normalPlay() {
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!normalPlay called!!!!!!!!!!");
        LogUtil.e(Utils.LIVE_TAG, "!!!!!!!!!!mIsReceiveError is " + mIsReceiveError);

        // 只有之前缓冲
        if (NetWorkUtils.isNetAvailable()) {
            if (!mIsReceiveError) {
                continePlayLive();
            } else {
                setPlayLoadingVisibile(false);
                // 已经尝试过所有的播放地址了，弹出错误提示框提示用用户重试
                setPlayErrorLayoutVisibile(true);
            }
        } else {
            LogUtil.e(Utils.LIVE_TAG, "##########normalPlay############# but net is unavailable");
            setPlayLoadingVisibile(false);
        }
    }

    /**
     * 移动网络下点击播放 (弱网情况下1s后重试，且只重试一次)
     */
    private void userClickPlay() {
        if (NetWorkUtils.isNetAvailable()) {
            LogUtil.e(Utils.LIVE_TAG, "%%%%%%%%%%%%%%%user click play%%%%%%%%%%%%%%%");
            normalPlay();
        } else {
            if (retryCount < 1) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e(Utils.LIVE_TAG, "%%%%%%%%%%%%%%%net is weak and retry one time%%%%%%%%%%%%%%%");
                        retryCount++;
                        userClickPlay();
                    }
                }, 1000);
            } else {
                LogUtil.e(Utils.LIVE_TAG, "%%%%%%%%%%%%%%%%%net is weak but retryTime out%%%%%%%%%%%%%%%%%");
                setPlayLoadingVisibile(false);
            }
        }
    }

    /**
     * 大数据上报
     */
    public void uploadstat(long timing) {
        if (mPlayContorl != null) {
            ac = "finish";
            ut = 0;
            play_type = 1;
            UploadStat.uploadplaystat(mActivityLive.getLiveDataEntity(), ac, ut + "", retry + "", play_type + "", code_rate, mActivityLive.getRef(), timing + "", "-", "-", mActivityLive.getPeid(), pageid);
        }
    }
}
