package com.chaojishipin.sarrs.fragment.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.fragment.ChaoJiShiPinBaseFragment;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.TrafficStatsUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;


/*VideoPlayerController.java
*  半屏播放顶部 fragment 类
* */
public class VideoPlayerFragment extends ChaoJiShiPinBaseFragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private VideoPlayerController mVideoPlayerController;
    private int mSysAPILevel = 0;
    private long begintime=0l;
    public String getPeid(){
     return Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce())+begintime;
    }
    //数据上报要用
    private String ref;

    public String getSeid() {
        return seid;
    }

    private String seid="-";

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    // 播放器返回按钮
    private ImageView mBack;
    // 切换大小屏按钮
    public ImageView mChangeFullScreen;
    private Context mContext;
    private ChaoJiShiPinVideoDetailActivity mActivity;
    public int recoderposition = 0;
    public int pausetype = -1;// -1代表什么都不是 0 代表 暂停退出  1代表 正在播放退出
    private HistoryRecordDao historyRecordDao;

    public VideoPlayerController getmVideoPlayerController() {
        return mVideoPlayerController;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    private String Cid;
    private int mNetType;

    public VideoDetailItem getmVideoDetailItem() {
        return mVideoDetailItem;
    }

    public void setmVideoDetailItem(VideoDetailItem mVideoDetailItem) {
        this.mVideoDetailItem = mVideoDetailItem;
    }

    private VideoDetailItem mVideoDetailItem;

//    public boolean ispause_resume = false;
    private PlayData mPlayData;
    public static boolean isactivityonresume = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    VideoItem videoItem;
    /**
     * 当activity要得到fragment的layout时，调用此方法，fragment在其中创建自己的layout(界面)。
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        begintime = System.currentTimeMillis();
        mContext = getActivity();
        mVideoDetailItem = (VideoDetailItem) getActivity().getIntent().getSerializableExtra("videoDetailItem");
        historyRecordDao = new HistoryRecordDao(mContext);
        ref = getActivity().getIntent().getStringExtra("ref");
        seid = getActivity().getIntent().getStringExtra("seid");
        if(TextUtils.isEmpty(seid)){
            seid = "-";
        }
        if (container == null) {
            return null;
        }
        View mMovie = inflater.inflate(R.layout.videoplayerfragment_mideaplayer_layout, container, false);
        return mMovie;
    }

    /**
     * 当activity的onCreated()方法返回后调用此方法。
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    /**
     *   全屏展示剧集 按钮
     * */

    public void showEpisode(){
        if(mVideoPlayerController!=null){
            mVideoPlayerController.setSelectVisibile(true);
        }
    }
    /**
     *   全屏展示剧集 按钮
     * */

    public void hideEpisode(){
        if(mVideoPlayerController!=null){
            mVideoPlayerController.setSelectVisibile(false);
        }
    }



    /**
     *   全屏展示下一集按钮
     * */
    public void showPlayNext(){
        if(mVideoPlayerController!=null){
            mVideoPlayerController.setPlayNextVisible(true);
        }
    }


    public void initView() {
        if (mContext instanceof ChaoJiShiPinVideoDetailActivity) {
            mActivity = (ChaoJiShiPinVideoDetailActivity) mContext;
        }
        mVideoPlayerController = new VideoPlayerController(this);
        mBack = (ImageView) getActivity().findViewById(R.id.mediacontroller_top_back);
        mChangeFullScreen = (ImageView) getActivity().findViewById(R.id.full_screen);
        mBack.setOnClickListener(this);
        mChangeFullScreen.setOnClickListener(this);
    }

    private void initData() {
       // mVideoDetailItem=(VideoDetailItem)getActivity().getIntent().getSerializableExtra("videoDetailItem");
        mSysAPILevel = Utils.getAPILevel();
        initTrafficStats(mSysAPILevel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("xll", "Letv onResume");
        isactivityonresume = true;
        myOnresume();
    }

    /**
     * 手机锁屏视频暂停
     */
    @Override
    public void onPause() {
        LogUtil.e("xll","Letv onPause");
           isactivityonresume = false;
           if(NetWorkUtils.isNetAvailable()){
               report();
           }
        if (mVideoPlayerController != null&&mVideoPlayerController.getmPlayContorl()!=null) {
            recoderposition = mVideoPlayerController.getCurrPosition();
            saveRecord();
            if (mVideoPlayerController.ismIsPause()) {
                pausetype = 0;
            } else {
                mVideoPlayerController.pauce();
                pausetype = 1;
            }
        }
        super.onPause();
    }

    /**
     *   播放记录（添加本地、在线）
     * */
    public void saveRecord() {
        if (mVideoPlayerController.getmPlayContorl() == null || mVideoPlayerController.getmPlayContorl().getDuration() == -1) {
          return;
        }
        videoItem = mVideoPlayerController.getCurrentVideoItem();
        if (videoItem != null) {
            //&& videoItem.getId() != null && (!"".equals(videoItem.getId()))
            LogUtil.e("xll", "record save db");
            HistoryRecord historyRecord = new HistoryRecord();
            historyRecord.setImage(videoItem.getImage());
            historyRecord.setSource(videoItem.getSource());
            LogUtil.e(TAG, "source:" + videoItem.getSource());
            historyRecord.setCategory_id(videoItem.getCategory_id());
            historyRecord.setTimestamp(System.currentTimeMillis() + "");
            int durationtime = mVideoPlayerController.getmPlayContorl().getDuration();
            historyRecord.setDurationTime(durationtime);
            String stitle = "";
            if (!TextUtils.isEmpty(videoItem.getCategory_id())) {
                if (videoItem.getCategory_id().equals(ConstantUtils.CARTOON_CATEGORYID)) {
                    historyRecord.setCategory_name(mActivity.getString(R.string.CARTOON));
                } else if (videoItem.getCategory_id().equals(ConstantUtils.TV_SERISE_CATEGORYID)) {
                    historyRecord.setCategory_name(mActivity.getString(R.string.TV_SERIES));
                } else if (videoItem.getCategory_id().equals(ConstantUtils.MOVIES_CATEGORYID)) {
                    historyRecord.setCategory_name(mActivity.getString(R.string.MOVIES));
                } else if (videoItem.getCategory_id().equals(ConstantUtils.DOCUMENTARY_CATEGORYID)) {
                    historyRecord.setCategory_name(mActivity.getString(R.string.DOCUMENTARY));
                } else if (videoItem.getCategory_id().equals(ConstantUtils.VARIETY_CATEGORYID)) {
                    historyRecord.setCategory_name(mActivity.getString(R.string.VARIETY));
                } else {
                    historyRecord.setCategory_name(mActivity.getString(R.string.OTHER));
                }
                String playtime = (mVideoPlayerController.getmPlayContorl().getCurrentPosition() / 1000) + "";
//                LogUtil.e("wulianshu", "playtime:"+playtime);
                historyRecord.setPlay_time(playtime);
                String title = "";
                if (mActivity != null && mActivity.getCurrentplayVideoDetailItem() != null && !TextUtils.isEmpty(mActivity.getCurrentplayVideoDetailItem().getFtitle())) {
                    title = mActivity.getCurrentplayVideoDetailItem().getFtitle();
                } else {
                    if (videoItem.getCategory_id().equals(ConstantUtils.VARIETY_CATEGORYID)) {
//              《欢乐喜剧人》20150613：宋小宝带伤上台反串甄嬛 乔杉修睿欢乐上演速激
//                  VideoDetailItem currentplayVideoDetailItem = ((ChaoJiShiPinVideoDetailActivity)this.getActivity()).getCurrentplayVideoDetailItem();
                        if (mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                            if (videoItem.getName().contains("：") && ((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem() != null) {
                                stitle = ((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem().getFtitle() + "  第" + videoItem.getPorder() + "期  " + videoItem.getName().split("：")[1];
                            } else if (videoItem.getName().contains(":") && ((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem() != null) {
                                stitle = ((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem().getFtitle() + "  第" + videoItem.getPorder() + "期  " + videoItem.getName().split(":")[1];
                            }



                        } else {
                            if (((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem() != null && !TextUtils.isEmpty(((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem().getFtitle())) {
                                title = ((ChaoJiShiPinVideoDetailActivity) this.getActivity()).getCurrentplayVideoDetailItem().getFtitle();
                            } else {
                                title = "";
                            }
                            if (videoItem.getTitle().contains("：")) {
                                //第三方登陆进来 mVideoDetailItem是空的 TODO 修改title构造方式
                                stitle = title + "  第" + videoItem.getOrder() + "期  " + videoItem.getTitle().split("：")[1];
                            } else if (videoItem.getTitle().contains(":")) {
                                stitle = title + "  第" + videoItem.getOrder() + "期  " + videoItem.getTitle().split(":")[1];
                            }
                        }

                    } else {
                        stitle = videoItem.getTitle();
                    }
                }
                if(TextUtils.isEmpty(stitle)){
                    stitle=videoItem.getTitle();
                }
                historyRecord.setTitle(stitle);
                historyRecord.setContent_type(videoItem.getContent_type());
                historyRecord.setId(videoItem.getId());
                historyRecord.setGvid(videoItem.getGvid());
                historyRecordDao.save(historyRecord);
            }
        }
    }




    /**
     * 手机锁屏视频暂停
     */


    private void myOnresume(){
        LogUtil.e("Media Player", "OnResume()");

        mNetType = NetWorkUtils.getNetType();
        if (mVideoPlayerController != null && mVideoPlayerController.getmPlayContorl() != null) {
            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                LogUtil.e("wulianshu","setPlayLoadingVisibile 5");
                mVideoPlayerController.setPlayLoadingVisibile(false,null);
            }
            if(videoItem !=null) {
                mVideoPlayerController.setVideoName(videoItem);
            }
            if (mNetType != -1 || mActivity.getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL) {
                // 锁屏或从后台再次进入时设置播放器控制状态栏的显示
                if (mVideoPlayerController.getmLockScreenBtn().isSelected()){
                    Message msg=new Message();
                    msg.what=ConstantUtils.DISSMISS_MEDIACONTROLLER;
                    if(mHandler.hasMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER)){
                        mHandler.removeMessages(ConstantUtils.DISSMISS_MEDIACONTROLLER);
                    }
                    mHandler.sendMessage(msg);
                }
                if (mNetType == NetWorkUtils.NETTYPE_WIFI)
                    mVideoPlayerController.hideNetView();
                //第一次进入 播放退出
                if (pausetype == 1 && mNetType == NetWorkUtils.NETTYPE_WIFI) {
                    LogUtil.e("xll", " onResume 播放退出");
                    mVideoPlayerController.getmPlayContorl().seekTo(recoderposition);
                    mVideoPlayerController.play();
                    LogUtil.e("xll", "Letv ");
                    mVideoPlayerController.updatePlayBtnBg(false);
                    //暂停退出
                } else if (pausetype == 0) {
                    LogUtil.e("xll", " onResume 暂停退出");
                    LogUtil.e("xll", " onResume 暂停退出 isPause " + mVideoPlayerController.ismIsPause());
                    mVideoPlayerController.getmPlayContorl().seekTo(recoderposition);
                    mVideoPlayerController.playerPause();
                    if(mVideoPlayerController.ismIsPause()){
                        mVideoPlayerController.updatePlayBtnBg(true);
                    }else{
                        mVideoPlayerController.updatePlayBtnBg(false);
                    }

                } else if (pausetype == -1 && mNetType == NetWorkUtils.NETTYPE_WIFI) {
                    LogUtil.e("xll"," onResume 其他退出");
                    mVideoPlayerController.play();
                    mVideoPlayerController.updatePlayBtnBg(false);
                } else if(pausetype == 1 && mActivity.getMediaType() ==  ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                    mVideoPlayerController.getmPlayContorl().seekTo(recoderposition);
                    mVideoPlayerController.play();
                    LogUtil.e("xll", "Letv ");
                    mVideoPlayerController.updatePlayBtnBg(false);
                }
            }
        }
    }




    public void btBackClick(){
        if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
          //  mActivity.getSm().unregisterListener(mActivity.getListener());
            mActivity.setSmallScreen();
            mActivity.statusBarShow(true);
            //切换成竖屏
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_BACK);
        } else {
            mActivity.finish();

        }
    }
   /**
    *  播放上报
    *  目前支持在线播放到最后一集返回详情页上报，本地播放以及推到后台不上报
    *  */
    void report(){
        if (mPlayData != null) {
                String bucket="";
                String reid="";
            if(mActivity.getData()!=null) {
                bucket = mActivity.getData().getBucket();
                reid = mActivity.getData().getReid();

            }
                VideoItem videoItem = mVideoPlayerController.getCurrentVideoItem();
                int playTime = 0;
                if (mVideoPlayerController.getmPlayContorl() != null) {
                    playTime = mVideoPlayerController.getmPlayContorl().getCurrentPosition() / 1000;
                }
                    if (videoItem != null) {
                    DataReporter.reportPlayRecord(videoItem.getGvid(),
                            videoItem.getId(),
                            videoItem.getSource(),
                            videoItem.getCategory_id(),
                            playTime,
                            UserLoginState.getInstance().getUserInfo().getToken(),
                            NetWorkUtils.getNetInfo(),
                            bucket,
                            reid);

                    LogUtil.e("xll ", "report online: aid : " + videoItem.getId() + " gvid :" + videoItem.getGvid() + " source :" + videoItem.getSource()+" cid :"+videoItem.getCategory_id()+" bucket: "+bucket+" seid : "+reid);
                }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mediacontroller_top_back:
                //本地播放 半屏返回键退出
                if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL)
                {
                    mActivity.finish();
                    return;
                }else{
                    if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
                        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            mActivity.setSmallScreen();
                            mActivity.setSCREEN(ChaoJiShiPinVideoDetailActivity.SCREEN.HALF);
                            mActivity.statusBarShow(true);
                            // 下拉菜单隐藏在半屏
                            if(mVideoPlayerController!=null){
                                mVideoPlayerController.hideAnimSelect(10);
                                mVideoPlayerController.setSlideTriggerVisible(false);
                            }
                        }else{
                            mActivity.setSCREEN(ChaoJiShiPinVideoDetailActivity.SCREEN.HALF);
                            mActivity.setSmallScreen();
                            mActivity.statusBarShow(true);
                            // 下拉菜单隐藏在半屏
                            if(mVideoPlayerController!=null){
                                mVideoPlayerController.hideAnimSelect(10);
                                mVideoPlayerController.setSlideTriggerVisible(false);
                            }
                        }
                        //切换成竖屏,通知竖屏更新收藏按钮
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(mActivity.SAVE_CHECK);
                            }
                        }, 0);
                    } else {
                        mActivity.finish();

                    }
                }
                break;
            case R.id.full_screen:
                //Local  本地播放禁止半屏
                if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                   return;
                }else{
                    // 竖屏-半屏
                    if (ChaoJiShiPinVideoDetailActivity.isFullScreen) {
                        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            mActivity.setSmallScreen();
                            mActivity.setSCREEN(ChaoJiShiPinVideoDetailActivity.SCREEN.HALF);
                            mActivity.statusBarShow(true);
                        }else{
                            mActivity.setSmallScreen();
                            mActivity.setSCREEN(ChaoJiShiPinVideoDetailActivity.SCREEN.HALF);
                            mActivity.statusBarShow(true);
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(ChaoJiShiPinVideoDetailActivity.SAVE_CHECK);
                            }
                        }, 0);
                        MobclickAgent.onEvent(getActivity(), ConstantUtils.FULLSCREEN_SWITCH);
                        //Umeng上报 全屏点击返回上报
                        MobclickAgent.onEvent(mActivity, ConstantUtils.FULLSCREEN_BACK);
                    } else {
                        // 切换成横屏-全屏
                        mActivity.statusBarShow(false);
                        if (mVideoPlayerController!= null) {
                            mVideoPlayerController.resetEpisoBtnColor(mPlayData);
                                // 在线播放单视频//或者单个视频专辑 ，隐藏剧集按钮
                                if(mPlayData!=null&&mPlayData.getmEpisodes()!=null
                                        &&mPlayData.getmEpisodes().indexOfKey(mPlayData.getKey())>=0
                                        &&mPlayData.getmEpisodes().get(mPlayData.getKey())!=null
                                        &&mPlayData.getmEpisodes().get(mPlayData.getKey()).size()<=1
                                  ){
                                        LogUtil.e("v1.1.2"," episo only one epsoviwe gone");
                                        mVideoPlayerController.setSelectVisibile(false);
                                }
                            mVideoPlayerController.updateSaveStatus();
                        }
                        mActivity.setFullScreen();
                        MobclickAgent.onEvent(getActivity(), ConstantUtils.HALFSCREEN_SWITCH);
                        mActivity.setSCREEN(ChaoJiShiPinVideoDetailActivity.SCREEN.FULL);

                    }
                }


                break;
            default:

                break;

        }

    }

    @Override
    public void onDestroy() {
        if(mVideoPlayerController != null){
            mVideoPlayerController.uploadstat(recoderposition);
            mVideoPlayerController.destroy();
        }
        super.onDestroy();
    }

    /**
     * 覆盖父类方法
     */

    @Override
    protected void handleInfo(Message msg) {

    }

    /**
     * 接收半屏页分页请求数据以及点击数据更新播放器数据以及全屏剧集
     */
    public void onEventMainThread(PlayData playData) {
        mPlayData = playData;
//        mVideoDetailItem =  ;
        if (null != playData) {
            //LogUtil.e("xll","receive pageNum "+playData.getmPageNum());
            LogUtil.e("xll", "receive data (k,p) "+playData.getKey()+""+playData.getIndex());
            LogUtil.e("xll", "receive  " + playData.getIndex());
            String from = playData.getFrom();
            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){

                if(playData.getmLocalDataLists()!=null&&playData.getmLocalDataLists().size()>0){
                    Cid=playData.getmLocalDataLists().get(0).getCid();
                }else {
                    if(playData.getmEpisodes()!=null&&playData.getmEpisodes().size()>0){
                        Cid=playData.getmEpisodes().get(playData.getKey()).get(0).getCategory_id();
                    }

                }

            }else{
                if(playData.getmEpisodes()!=null&&playData.getmEpisodes().size()>0){
                    Cid=playData.getmEpisodes().get(playData.getKey()).get(0).getCategory_id();
                }

            }
            if(Cid!=null){
                mPlayData.setCid(Cid);
            }
            LogUtil.d("dyf", "来源：" + from);
            if (ConstantUtils.PLAYER_FROM_DETAIL.equals(from)) {
                LogUtil.d("dyf", "updatePlayData");
                mVideoPlayerController.setmPlayData(mPlayData);
                //更新播放器剧集信息
                // 初次进入需要设置全屏展开tagIndex=key
                mPlayData.setTagIndex(mPlayData.getKey());
                mVideoPlayerController.resetFullScreenEpiso(mPlayData);
                // 播放器此时如果暂停需要播放继续
                LogUtil.e("update FullScreen Episo Data", " OK");
            } else if (ConstantUtils.PLAYER_FROM_DETAIL_ITEM.equals(from)) {
                LogUtil.d("dyf", "setmPlayData" + mPlayData);
                //将播放数据提供给播放控制台使用
                if(mVideoPlayerController !=null) {
                    //mVideoPlayerController.stop();
                    mVideoPlayerController.clickPauseOrPlay();
                }
                mVideoPlayerController.setmPlayData(mPlayData);
                if (mVideoPlayerController.ismEpisoShow()) {
                    mVideoPlayerController.hideAnimSelect(10);
                }
                //在当前页点击需要设置全屏展开tagIndex=key
                mPlayData.setTagIndex(mPlayData.getKey());
                // 设置全屏展开状态
                mVideoPlayerController.resetFullScreenEpiso(mPlayData);
                LogUtil.e("update FullScreen Episo Data", " OK");
            }else if (ConstantUtils.PLAYER_FROM_BOTTOM_EPISO_TAG_CLICK.equals(from)) {
                LogUtil.e("update FullScreen Episo Data", " OK");
            }

            else if (ConstantUtils.PLAYER_FROM_SPECAIL.equals(from)) {
                //更新播放器数据
                mVideoPlayerController.updatePlayData(mPlayData);
            } else if (ConstantUtils.PLAYER_FROM_SEARCH.equals(from)) {
                //更新播放器数据
                mVideoPlayerController.updatePlayData(mPlayData);
            } else if (ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK.equals(from)) {
                //更新播放器数据 不做view更新
                mVideoPlayerController.updatePlayData(mPlayData);
                //更新播放器剧集信息(展开点击tag所在分页，播放剧集不能更新)

                mVideoPlayerController.resetFullScreenEpiso(mPlayData);
                // 排行榜数据更新
            } else if (ConstantUtils.PLAYER_FROM_RANKLIST.equals(from)) {
                //更新播放器数据
                mVideoPlayerController.updatePlayData(mPlayData);
            }else{
                // 下载。。。。等播放本地使用
                mVideoPlayerController.setmPlayData(mPlayData);

            }
        }




    }

    public void onEventMainThread(String data) {
        if (data.equals(mActivity.SHARE_CANCEL))
        {
            reloadData();
        }else if(mActivity.PLAY_DATA_ERR.equals(data)){
            mVideoPlayerController.showTimeOutLayout();
            ToastUtil.showShortToast(mActivity,mActivity.getResources().getString(R.string.video_cannot_play));
        }
    }


    /**
     * 获得系统API
     */
    public int getmSysAPILevel() {
        return mSysAPILevel;
    }

    /**
     * 初始化网速
     * 2015年7月23日 16:39:24
     */
    private void initTrafficStats(int sdkVersion) {
        if (sdkVersion >= 8) {
            TrafficStatsUtil.getPreRxByte();
        }
    }

    /**
     * 大小屏幕切换控制UI变化
     *    wifi/2G/3G/4G 1 2 3 4
     * @param isFullScreen
     */
    public void setmVideoPlayerControllerUIByScreen(boolean isFullScreen) {
        if (isFullScreen) {
            if (mNetType == -1) {
                mVideoPlayerController.getmVideoPlayerNetErrorIcon().setVisibility(View.GONE);
            }
            mVideoPlayerController.getmSlideBtn().setVisibility(View.VISIBLE);
            mVideoPlayerController.getmPlayNextBtn().setVisibility(View.VISIBLE);
            mVideoPlayerController.getmLockScreenBtn().setVisibility(View.VISIBLE);
            mChangeFullScreen.setBackgroundResource(R.drawable.sarrs_pic_small_screen);
        } else {
            if (mNetType == -1) {
                mVideoPlayerController.getmVideoPlayerNetErrorIcon().setVisibility(View.GONE);
            }
            mVideoPlayerController.getmSlideBtn().setVisibility(View.GONE);
            mVideoPlayerController.hideSlideMenu();
            mVideoPlayerController.hideAnimSelect(10);
            mVideoPlayerController.getmPlayNextBtn().setVisibility(View.GONE);
            LogUtil.e("v1.1.2","next btn gone fullscreen is false");
            mVideoPlayerController.getmLockScreenBtn().setVisibility(View.GONE);
            mChangeFullScreen.setBackgroundResource(R.drawable.sarrs_pic_full_screen);
        }
        mVideoPlayerController.setSelectVisibile(isFullScreen);
        mVideoPlayerController.setMediaControllerTopVisibile(true);

    }





    /**
     * 网络状态变化，控制UI显隐
     *
     * @param netType 网络状态 -1 无网络  1 wifi 0 3G
     */
    public void setPlayerControllerBarState(int netType) {
        if (mVideoPlayerController == null) {
            return;
        }
         reloadData();
        //WIFI
        if (netType == NetWorkUtils.NETTYPE_WIFI) {
//            reloadData();
            mVideoPlayerController.hideNetView();
//            mVideoPlayerController.resetPlaystate(false);
            mVideoPlayerController.showWIFINetView();
//            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.player_net_wifi), Toast.LENGTH_SHORT).show();
            //流量

        } else if (netType == NetWorkUtils.NETTYPE_GSM) {

            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){

                LogUtil.e("xll","播放本地断开网络！");
            }else{
                if(mActivity.getEpisodeType()== ChaoJiShiPinVideoDetailActivity.EpisodeType.EPISO_LOCAL){
                    LogUtil.e("v1.1.2"," local episode not show netview");
                }else{
                    mVideoPlayerController.playerPause();
                    mVideoPlayerController.resetPlaystate(true);
                    mVideoPlayerController.showGSMNetView();
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.RPG_net_tip), Toast.LENGTH_SHORT).show();
                }

            }


        }
        // no net
        else if (netType == NetWorkUtils.NETTYPE_NO) {
            if(mActivity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){

               LogUtil.e("xll","播放本地断开网络！");
            }else{
                if(mActivity.getEpisodeType()== ChaoJiShiPinVideoDetailActivity.EpisodeType.EPISO_LOCAL){
                    LogUtil.e("v1.1.2"," local episode not show netview");
                }else{
                    mVideoPlayerController.showNoNetView();
                    mVideoPlayerController.playerPause();
                    mVideoPlayerController.resetPlaystate(true);
                    mVideoPlayerController.updatePlayBtnBg(true);
                    //把小波波加载的精疲力尽去掉
                    mVideoPlayerController.videoplayer_load_timeout_layout.setVisibility(View.GONE);
                }
            }

        }
    }

    public ChaoJiShiPinVideoDetailActivity getmActivity() {
        return mActivity;
    }

    /**
     * 上报历史记录
     *
     * @paramcid
     */
    private void uploadHistoryRecordOneRecord(String token, UploadRecord historyRecord) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.UPLOAD_HISTORY_RECORD_ONE_RECORD);
        HttpApi.
                uploadHistoryRecordoneRecord(token, historyRecord)
                .start(new UploadHistoryRecordListener(), ConstantUtils.UPLOAD_HISTORY_RECORD_ONE_RECORD);
    }

    private class UploadHistoryRecordListener implements RequestListener<HistoryRecordResponseData> {

        @Override
        public void onResponse(HistoryRecordResponseData result, boolean isCachedData) {
        }

        @Override
        public void netErr(int errorCode) {
            System.out.print(errorCode);
        }

        @Override
        public void dataErr(int errorCode) {
            System.out.print(errorCode);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantUtils.SaveJumpTologin.MEDIA_LOGIN) {
            if (mVideoPlayerController != null) {
                mVideoPlayerController.checkSave(false);
            }
        }
    }




    @Override
    public void onStop() {
        VideoPlayerFragment.isactivityonresume=false;
        super.onStop();
    }

    @Override
    public void onDetach() {
        VideoPlayerFragment.isactivityonresume=false;
        super.onDetach();
    }
    public void reloadData(){
        if(isactivityonresume) {
            myOnresume();
        }
    }
    public boolean isLockScreen(){
        return mVideoPlayerController.isLockScreen();
    }
}
