package com.chaojishipin.sarrs.fragment.videoplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VideoDetailIndex;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.fragment.ChaoJiShiPinBaseFragment;
import com.chaojishipin.sarrs.fragment.videoplayer.httpd.M3u8Httpd;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.TrafficStatsUtil;
import com.chaojishipin.sarrs.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * 播放本地视频
 *
 * @author xll
 */
public class LocalMediaPlayerFragment extends ChaoJiShiPinBaseFragment implements OnClickListener {

    private ImageView mPlayBack;

    private final static String TAG = "LocalMediaPlayerFragment";

    private int mSysAPILevel = 0;
    /**
     * 播放需要用到的相关数据
     */
    private PlayData mPlayData;
    private VideoPlayerScreenListener mSceenListener;
    private Window mWindow;
    private boolean isClickBack = false;
    private Context mContext;
    private static M3u8Httpd m3u8Httpd = new M3u8Httpd(8084);
    private VideoPlayerController mPlayMediaController;
    // 切换大小屏按钮
    public ImageView mChangeFullScreen;
    private ChaoJiShiPinVideoDetailActivity mActivity;
    ArrayList<HistoryRecord> local_list = new ArrayList<HistoryRecord>();
    ArrayList<HistoryRecord> totallist = new ArrayList<HistoryRecord>();

    //定时器用于定时向本地数据库中写入播放记录
    private Timer timer;
    private TimerTask task;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (container == null) {
            return null;
        }
        View localview = inflater.inflate(R.layout.activity_local_videoplayer, container, false);

        //如果存在播放播放记录 修改playdata就好
        local_list = new HistoryRecordDao(mContext).getAll();


        return localview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    public VideoPlayerController getmPlayMediaController() {
        return mPlayMediaController;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    public ChaoJiShiPinVideoDetailActivity getmActivity() {
        return mActivity;
    }

    private void initView() {
        if (mContext instanceof ChaoJiShiPinVideoDetailActivity) {
            mActivity = (ChaoJiShiPinVideoDetailActivity) mContext;
        }
        if (mPlayMediaController == null) {
            mPlayMediaController = new VideoPlayerController(LocalMediaPlayerFragment.this);
        }
//        mWindow = getActivity().getWindow();
//        // 设置当前屏幕不锁屏
//        mWindow.setFlags(
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mPlayBack = (ImageView) getActivity().findViewById(R.id.mediacontroller_top_back);
        mChangeFullScreen = (ImageView) getActivity().findViewById(R.id.full_screen);
        mPlayBack.setOnClickListener(this);
    }

    private void initData() {
        mSysAPILevel = Utils.getAPILevel();
        initTrafficStats(mSysAPILevel);
        Intent intent = getActivity().getIntent();
        if (null != intent) {
            setWifiTo3GFlag(intent.getBooleanExtra(Utils.PLAY_3G_NET, false));
        }
        if (null != intent
                && null != intent.getSerializableExtra(Utils.PLAY_DATA)) {
            mPlayData = (PlayData) intent.getSerializableExtra(Utils.PLAY_DATA);
            //将播放数据提供给播放控制台使用
            // 通知半屏页取详情
            if (mPlayData != null) {

//                if (UserLoginState.getInstance().isLogin()) {
//                    requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
//                } else {
//                    //本地的播放记录
//                    totallist = local_list;
//                    preparePlay();
//                }
                mPlayMediaController.setmPlayData(mPlayData);
                // 通知半屏页取详情
//                VideoPlayerNotifytData mNotifyData = new VideoPlayerNotifytData();
//                mNotifyData.setIsFirst(true);
//                mNotifyData.setIsLocal(true);
//                mNotifyData.setType(mPlayData.getFrom());
//                EventBus.getDefault().post(mNotifyData);
            }
        }
    }

    /**
     * 初始化网速
     *
     * @param sdkVersion xll
     *                   2014年8月19日 下午2:06:31
     */
    private void initTrafficStats(int sdkVersion) {
        if (sdkVersion >= 8) {
            TrafficStatsUtil.getPreRxByte();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (!m3u8Httpd.isAlive()) {
                m3u8Httpd.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (m3u8Httpd.isAlive()) {
            m3u8Httpd.stop();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e(TAG, "onResume");
        if (!Utils.getScreenLockStatus()) {
            if (mPlayMediaController != null) {
                mPlayMediaController.resetPlaystate(true);
                mPlayMediaController.clickPauseOrPlay();
            }
        }

        timer= new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 向本地数据库中写入数据
                VideoItem videoItem = mPlayMediaController.getCurrentVideoItem();
                if (videoItem != null) {
                    save2LocalDB(videoItem);
                }
            }
        };
        timer.schedule(task, 0, 5000);
    }


    @Override
    public void onPause() {
        super.onPause();
        LogUtil.e(TAG, "!!!!!!!!!!!!!!!onPause");
        // 停止播放器页面刷新
        if (null != mPlayMediaController) {
            mPlayMediaController.resetPlaystate(true);
            mPlayMediaController.clickPauseOrPlay();
        }
        save2LocalandServe();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayMediaController != null){
            mPlayMediaController.destroy();
            mPlayMediaController = null;
        }
        LogUtil.e(TAG, "!!!!!player!!!!!onDestroy!!!!!!!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mediacontroller_top_back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    public void excuteLocalVideoFullPlay(ChaoJiShiPinVideoDetailActivity context) {
        context.setFullScreen();
    }

    /**
     * 大小屏幕切换控制UI变化
     *
     * @param isFullScreen
     */
    public void setmVideoPlayerControllerUIByScreen(boolean isFullScreen, VideoPlayerController controller) {
        if (isFullScreen) {
            controller.getmSlideBtn().setVisibility(View.VISIBLE);
            controller.getmPlayNextBtn().setVisibility(View.VISIBLE);
            controller.getmLockScreenBtn().setVisibility(View.VISIBLE);
            mChangeFullScreen.setBackgroundResource(R.drawable.sarrs_pic_small_screen);
        }
        controller.setSelectVisibile(isFullScreen);
        controller.setMediaControllerTopVisibile(true);
    }
    public void save2LocalandServe() {
        VideoItem videoItem = mPlayMediaController.getCurrentVideoItem();
        if (UserLoginState.getInstance().isLogin()) {
            String token = UserLoginState.getInstance().getUserInfo().getToken();
            if (videoItem != null) {
                UploadRecord uploadRecord = new UploadRecord();
                uploadRecord.setCid(Integer.parseInt(videoItem.getCategory_id()));
                uploadRecord.setAction(0);
                uploadRecord.setDurationTime(mPlayMediaController.getmPlayContorl().getDuration());
                uploadRecord.setPid(videoItem.getId());
                uploadRecord.setPlayTime(mPlayMediaController.getCurrPosition() / 1000);
                uploadRecord.setUpdateTime(System.currentTimeMillis());
                uploadRecord.setSource(videoItem.getSource());
                uploadRecord.setVid(videoItem.getGvid());
                uploadHistoryRecordOneRecord(token, uploadRecord);
            }
        }
        if (videoItem != null) {
            save2LocalDB(videoItem);
        }
    }

    public void save2LocalDB(VideoItem videoItem) {
        if (mPlayMediaController.getmPlayContorl() != null) {
            HistoryRecord historyRecord = new HistoryRecord();
            historyRecord.setImage(videoItem.getImage());
            historyRecord.setSource(videoItem.getSource());
            historyRecord.setCategory_id(videoItem.getCategory_id());
            historyRecord.setTimestamp(System.currentTimeMillis() + "");
            historyRecord.setDurationTime(mPlayMediaController.getmPlayContorl().getDuration());
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
            historyRecord.setPlay_time((mPlayMediaController.getCurrPosition() / 1000) + "");
            historyRecord.setTitle(videoItem.getTitle());
            historyRecord.setContent_type(videoItem.getContent_type());
            historyRecord.setId(videoItem.getId());
            historyRecord.setGvid(videoItem.getGvid());
            HistoryRecordDao historyRecordDao = new HistoryRecordDao(mContext);

            historyRecordDao.save(historyRecord);
        }
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
            System.out.print(result.getCode());
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
    /**
     * 根据TOKEN获取所有的历史记录
     *
     * @param token
     */
    private void requestHistoryRecordData(String token) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_HISTORYRECORD_DETAIL);
        HttpApi.
                getHistoryRecordList(token)
                .start(new RequestHistoryRecordListener(), ConstantUtils.REQUEST_HISTORYRECORD_DETAIL);
    }

    private class RequestHistoryRecordListener implements RequestListener<SarrsArrayList> {
        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            //进行展现的相关操作


            ArrayList<HistoryRecord> netlist = result;
            if (netlist != null) {
                if (local_list != null) {
                    if (local_list.size() == 0) {
                        totallist = netlist;
                    } else if (null == netlist || netlist.size() == 0) {
                        totallist = local_list;
                    } else if (local_list.size() > 0 && netlist.size() > 0) {
                        totallist.addAll(netlist);
                        for (int i = 0; i < local_list.size(); i++) {
                            for (int j = 0; j < netlist.size(); j++) {
                                //相同视频需要合并取时间最近的一个记录
                                if (local_list.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                                    if (local_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                        totallist.remove(netlist.get(j));
                                        totallist.add(local_list.get(i));
                                    }
                                    break;
                                }
                                if (j == netlist.size() - 1) {
                                    totallist.add(local_list.get(i));
                                }
                            }
                        }
                    }
                    preparePlay();
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
     * TODO NULL
     */
    public void preparePlay() {
        if (totallist.size() > 0) {
            for (int i = 0; i < totallist.size(); i++) {
                //有播放记录
                if (totallist.get(i) != null) {
                    if (totallist.get(i).getId() != null) {
                        if (totallist.get(i).getId().equals(mPlayData.getmEpisodes().get(0).get(0).getId())) {
                            requestVideoDetailIndex(totallist.get(i).getId(), totallist.get(i).getGvid());
                            //保存播放的时长
                            int playtime = Integer.parseInt(totallist.get(i).getPlay_time().trim());
                            mPlayData.setRecordposition(playtime);
                            break;
                        }
                        if (i == totallist.size() - 1) {
                            if (mPlayData != null) {
                                mPlayMediaController.setmPlayData(mPlayData);
                            }
//                            EventBus.getDefault().post(mPlayData);
                        }
                    }
                }
            }
        }else{
            if (mPlayData != null) {
                mPlayMediaController.setmPlayData(mPlayData);
            }
//            EventBus.getDefault().post(mPlayData);
        }
    }

    public void requestVideoDetailIndex(String aid, String gvid) {
        //执行请求首页侧边栏
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG);
        HttpApi.
                getVideoDetailIndexRequest(aid, gvid)
                .start(new RequestVideoDetailIndexListener2(gvid), ConstantUtils.REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG);
    }

    /*
        *    请求半屏页剧集信索引息回调
        * */
    private class RequestVideoDetailIndexListener2 implements RequestListener<VideoDetailIndex> {
        //
        String gvid;

        public RequestVideoDetailIndexListener2(String gvid) {
            this.gvid = gvid;
        }

        @Override
        public void onResponse(VideoDetailIndex result, boolean isCachedData) {
            if (result != null) {
                LogUtil.e(TAG, "" + result.toString());
                // 请求剧集详情
                mPlayData.setKey(result.getPn());
                mPlayData.setIndex(result.getIndex() - 1);
                mPlayData.setmGvid(gvid);
                if (mPlayData != null) {
                    mPlayMediaController.setmPlayData(mPlayData);
                }
//                EventBus.getDefault().post(mPlayData);
            }
        }

        @Override
        public void dataErr(int errorCode) {
        }

        @Override
        public void netErr(int errorCode) {
        }
    }
}
