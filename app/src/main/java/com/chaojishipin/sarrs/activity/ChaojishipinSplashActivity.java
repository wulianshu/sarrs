package com.chaojishipin.sarrs.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.InterestAdapter;
import com.chaojishipin.sarrs.bean.InterestEntity;
import com.chaojishipin.sarrs.bean.InterestRecommend;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.UpgradeHelper;
import com.chaojishipin.sarrs.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wangyemin on 2015/10/12.
 *
 * @des 应用启动页
 */
public class ChaojishipinSplashActivity extends ChaoJiShiPinBaseActivity implements
        SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener {
    private long startTime;
    private boolean isDebug = false;
    private static final String TAG = "ChaojishipinSplashActivity";
    private static final String FILENAME = "chaojishipin.mp4";
    private Context mContext;
    private SharedPreferences setting;
    private Runnable mTask;
    private AsyncTask mInitTask;
    private boolean isFrist = true;

    private GestureDetector gestureDetector;
    // 兴趣上报list
    private ArrayList<InterestEntity> mReportLists;

    private ImageView mSplash; // 黑色背景
    private RelativeLayout mLogo; // Logo

    //    private Display currDisplay;
    //    private int vWidth, vHeight;
    private SurfaceView surfaceView;
    private boolean PasueFlag = true;
    private SurfaceHolder holder;
    private MediaPlayer player;
    // 记录暂停播放的位置
    private int currentPosition = 0;

    private TextView mStartWatch;
    private TextView mInterestTv;

    private ImageView mShowGuide;
    private GridView mGridView;
    private InterestAdapter mAdapter;
    // 接口返回list
    private SarrsArrayList<InterestEntity> mList;
    private RelativeLayout mSlogan;
    private RelativeLayout mInterestGuide;
    private RelativeLayout mStartWatchBtn;
    private RelativeLayout mBottom;

    private UpgradeInfo mUpdateData;

    private float sX;
    private float sY;
    private float eY;
    private boolean isSlide;
    private float scope_SX;
    private float scope_EX;
    private float scope_SY;
    private float scope_EY;
    private float FLING_MIN_DISTANCE; // Y轴滑动最小距离

    private void redirectToHome() {
        Intent intent = new Intent(ChaojishipinSplashActivity.this, ChaoJiShiPinMainActivity.class);
        intent.putExtra(UpgradeHelper.UPGRADE_DATA, mUpdateData);
        intent.putExtra(UpgradeHelper.FROM_SPLASH, true);
        LogUtil.e(TAG, " put " + mUpdateData);
        startActivity(intent);
        finish();
        this.overridePendingTransition(0, R.anim.activity_finish_alpha);
    }

    /**
     * 第一次启动，请求兴趣推荐,进入兴趣页
     */
    private void redirectToInterest() {
        LogUtil.e(TAG, "go interset");
        if (NetWorkUtils.isNetAvailable()) {
            LogUtil.e(TAG, "net is available and request interestdata");
            startTime = System.currentTimeMillis();
            LogUtil.e(TAG, "request interset start time is " + startTime);
            getInterestRecommentRequest(UserLoginState.getInstance().getUserInfo().getToken());
        }
        /*else
            redirectToHome();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, Utils.getScreenWidth(this) + "");
        SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT, Utils.getScreenHeight(this) + "");
        LogUtil.e("xll", "init screen Width " + Utils.getScreenWidth(this));
        LogUtil.e("xll", "init screen Height " + Utils.getScreenHeight(this));
        gestureDetector =new GestureDetector(this, new Gesturelistener());
        if (isFrist) {
            setListener();
        }
        initData();
    }


    private void initView() {
        mContext = this;
        setContentView(R.layout.chaojishipin_splashactivity_layout);
        mReportLists = new ArrayList<>();

        setting = getSharedPreferences(ConstantUtils.SHARE_APP_TAG, 0);
        isFrist = setting.getBoolean("FIRST", true);
//        if (isFrist) {
//            setting.edit().putBoolean("FIRST", false).commit();
//        }

        if (isFrist) {
            mInterestGuide = (RelativeLayout) findViewById(R.id.interest_guide);
            mStartWatchBtn = (RelativeLayout) findViewById(R.id.startWatchBtn);
            mBottom = (RelativeLayout) findViewById(R.id.bottom);
            mStartWatch = (TextView) findViewById(R.id.startWatch);
            mInterestTv = (TextView) findViewById(R.id.startWatchTV);
            mShowGuide = (ImageView) findViewById(R.id.showGuide);
            mGridView = (GridView) findViewById(R.id.interest_view);

            FLING_MIN_DISTANCE = Utils.getHeightPixels(mContext) / 5;
            scope_EX = Utils.getWidthPixels(mContext) - (Utils.getWidthPixels(mContext) - (Utils.getViewWidth(mStartWatch) + Utils.getViewWidth(mShowGuide) + Utils.dip2px(12))) / 2;
            scope_SX = scope_EX - Utils.getViewWidth(mShowGuide);
            scope_EY = Utils.getHeightPixels(mContext) - Utils.dip2px(27);
            scope_SY = scope_EY - Utils.getViewHeight(mShowGuide);
        }

        mLogo = (RelativeLayout) findViewById(R.id.logo);
        mSplash = (ImageView) findViewById(R.id.splash);
        mSlogan = (RelativeLayout) findViewById(R.id.solganLayout);

//        //然后，我们取得当前Display对象
//        currDisplay = this.getWindowManager().getDefaultDisplay();
        surfaceView = (SurfaceView) this.findViewById(R.id.videoview);
        //给SurfaceView添加CallBack监听
        holder = surfaceView.getHolder();
        //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }

    private void setListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    mAdapter.setSelectItem(position, view);
                    if (mAdapter.mSelectArr != null) {
                        for (boolean isSelected : mAdapter.mSelectArr) {
                            if (isSelected) {
                                mInterestTv.setText(getResources().getString(R.string.excuteWatch));
                                break;
                            } else
                                mInterestTv.setText(getResources().getString(R.string.startWatch));
                        }
                    }
                }
            }
        });
        mShowGuide.setOnTouchListener(this);
        mStartWatch.setOnTouchListener(this);
        mStartWatchBtn.setOnClickListener(this);
        mStartWatch.setOnClickListener(this);
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    private void initData() {
        /*mInitTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // 执行耗时的初始化工作
                if (NetWorkUtils.isNetAvailable()) {
                    LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity start requestUpgradeData!!!!!!!!!!");
                    //有网络才能获取到服务器版本信息
                    UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isInit) {
                showDefaultLoadingAnima();
            }
        }.execute(new Void[]{});*/
        if (isFrist)
            redirectToInterest();
        if (NetWorkUtils.isNetAvailable()) {
            LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity start requestUpgradeData!!!!!!!!!!");
            //有网络才能获取到服务器版本信息
            UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());
        }else {
            showDefaultLoadingAnima();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_UP:
                if(view.getId() == R.id.startWatch){
                    gotoInterest();
                }
                break;
        }

        switch(view.getId()){
            case R.id.showGuide:
            case R.id.startWatch:
                gestureDetector.onTouchEvent(motionEvent);
                break;
        }

        return true;
    }

    class RequestUpgradeListener implements RequestListener<UpgradeInfo> {
        @Override
        public void onResponse(UpgradeInfo result, boolean isCachedData) {
            mUpdateData = result;
            LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity requestUpgradeData sucess!!!!!!!!!!");
            showDefaultLoadingAnima();
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity requestUpgradeData net err!!!!!!!!!!");
            showDefaultLoadingAnima();
        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity requestUpgradeData data err!!!!!!!!!!");
            showDefaultLoadingAnima();
        }
    }

    /**
     * 黑色背景图褪去
     */
    private void showDefaultLoadingAnima() {
        AlphaAnimation anima = new AlphaAnimation(1f, 0f);
        anima.setDuration(800);
        mSplash.startAnimation(anima);
        anima.setFillAfter(true);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isFrist) {
                    hideSolganAnim();
                }
//                play(); //播放
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 第一次启动时 隐藏Solgan
     */
    private void hideSolganAnim() {
        AlphaAnimation anima = new AlphaAnimation(1f, 0f);
        anima.setDuration(800);
        mSlogan.startAnimation(anima);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSlogan.setVisibility(View.GONE);
                showBottomAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 第一次启动 显示开始观影
     */
    private void showBottomAnim() {
        mBottom.setVisibility(View.VISIBLE);
        AlphaAnimation anima = new AlphaAnimation(0f, 1f);
        anima.setDuration(800);
        mBottom.startAnimation(anima);
    }

    @Override
    protected void onPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            // 记录这个位置
            currentPosition = player.getCurrentPosition();
            LogUtil.e(TAG, "onPause " + currentPosition);
            // 标识 这视频 会被暂停
            PasueFlag = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (PasueFlag) {
            if (player != null) {
                // 跳转到播放记录的位置
                LogUtil.e(TAG, "onResume " + currentPosition);
                player.seekTo(currentPosition);
                try {
                    player.start();
                } catch (Exception e) {
                    LogUtil.e(TAG, " " + e.toString());
                }
                PasueFlag = false;
            } else
                PasueFlag = true;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LogUtil.e(TAG, "onDestroy " + currentPosition);
        currentPosition = 0;
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (mTask != null)
            mHandler.removeCallbacks(mTask);
        if (mInitTask != null && mInitTask.getStatus() != AsyncTask.Status.FINISHED) {
            mInitTask.cancel(true);
        }
        super.onDestroy();
    }

//    private void setVideoSize() {
//        //首先取得video的宽和高
//        vWidth = player.getVideoWidth();
//        vHeight = player.getVideoHeight();
//
//        if (vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight()) {
//            //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
//            float wRatio = (float) vWidth / (float) currDisplay.getWidth();
//            float hRatio = (float) vHeight / (float) currDisplay.getHeight();
//
//            //选择大的一个进行缩放
//            float ratio = Math.max(wRatio, hRatio);
//
//            vWidth = (int) Math.ceil((float) vWidth / ratio);
//            vHeight = (int) Math.ceil((float) vHeight / ratio);
//        }
//        //设置surfaceView的布局参数
//        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(vWidth, vHeight));
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isFrist || mInterestGuide.getVisibility() == View.VISIBLE)
            return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            sX = event.getX();
            sY = event.getY();
            if (isDebug) {
                LogUtil.e(TAG, " scope_SX is " + scope_SX + " and scope_EX is " + scope_EX);
                LogUtil.e(TAG, " scope_SY is " + scope_SY + " and scope_EY is " + scope_EY);
                LogUtil.e(TAG, " sX is " + sX);
                LogUtil.e(TAG, " sY is " + sY);
            }
            if (sX >= scope_SX && sX <= scope_EX && sY >= scope_SY && sY <= scope_EY)
                isSlide = true;
            else
                isSlide = false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            eY = event.getY();
            if (isDebug) {
                LogUtil.e(TAG, " eY is " + eY);
                LogUtil.e(TAG, " min dis is " + FLING_MIN_DISTANCE);
                LogUtil.e(TAG, " slide dis is " + (sY - eY) + " and isSlide is " + isSlide);
            }
            if (isSlide && (sY - eY) > FLING_MIN_DISTANCE) {
                LogUtil.e(TAG, "slide up and eY is " + eY + " and sY is " + sY);
                gotoInterest();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startWatch:
                gotoInterest();
                break;
            case R.id.startWatchBtn:
                reportInterest();
                break;
        }
    }

    // 设置上报list
    private void setmReportLists() {
        if (mReportLists != null)
            mReportLists.clear();
        if (mAdapter.mSelectArr != null && mList != null) {
            for (int i = 0; i < mAdapter.mSelectArr.length; i++) {
                if (mAdapter.mSelectArr[i])
                    mReportLists.add((InterestEntity) mList.get(i));
            }
        }
    }

    // 上报用户兴趣
    String id = "";
    String cid = "";
    String type = "";
    String token = "";
    String netType = "";

    /**
     * 兴趣页进入首页
     */
    private void reportInterest() {
        LogUtil.e(TAG, "go main");
        setmReportLists(); //构造用户兴趣list
        if (mReportLists != null && mReportLists.isEmpty()) {
            LogUtil.e(TAG, "no select item");
            redirectToHome();
            return;
        }
        InterestEntity entity = null;
        token = UserLoginState.getInstance().getUserInfo().getToken();
        netType = NetWorkUtils.getNetInfo();
        for (int i = 0; i < mReportLists.size(); i++) {
            entity = mReportLists.get(i);
            if (i == mReportLists.size() - 1) {
                id = id.concat(entity.getId());
                cid = cid.concat(String.valueOf(entity.getCategory_id()));
                if (entity.getContent_type() == 1) {
                    //专辑
                    type = type.concat("1");
                } else if (entity.getContent_type() == 2) {
                    // 单视频
                    type = type.concat("2");
                }
                break;
            }
            id = id.concat(entity.getId() + ",");
            cid = cid.concat(String.valueOf(entity.getCategory_id()) + ",");
            if (entity.getContent_type() == 1) {
                //专辑
                type = type.concat("1,");
                new StringBuilder().append("");
            } else if (entity.getContent_type() == 2) {
                // 单视频
                type = type.concat("2,");
            }
        }
//        LogUtil.e(DataHttpApi.TAG, "id is " + id + " cid is " + cid + " type is " + type);
        // 上报兴趣推荐
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataReporter.reportInterest(id, cid, type, token, netType, "" + bucket, "" + reId);
            }
        }).start();
        redirectToHome();
    }

    private void getInterestRecommentRequest(String token) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_INTERESTRECOMMEND_TAG);
        HttpApi.getInterestRecommendRequest(token).start(new RequestInterestListener(), ConstantUtils.REQUEST_INTERESTRECOMMEND_TAG);
    }

    String bucket = "";
    String reId = "";

    class RequestInterestListener implements RequestListener<InterestRecommend> {
        @Override
        public void onResponse(InterestRecommend result, boolean isCachedData) {
            if (null != result) {
                LogUtil.e(TAG, "result is " + result);
                bucket = result.getBucket();
                reId = result.getReid();
                mList = result.getItems();
                if (mList != null && mList.size() > 1) {
                    Boolean[] arr = new Boolean[mList.size()];
                    for (int i = 0; i < mList.size(); i++) {
                        arr[i] = false;
                    }
                    mAdapter = new InterestAdapter(mContext, mList);
                    mAdapter.setmSelectArr(arr);
                    mGridView.setAdapter(mAdapter);
                    // 显示兴趣推荐页
//                    showGuideAnim();
                    // 降低透明度
//                    showVideoAnim();
                }
            } else {
                LogUtil.e(TAG, "RequestInterestListener response result is null");
                redirectToHome();
            }
        }

        @Override
        public void netErr(int errorCode) {
            ToastUtil.showShortToast(mContext, R.string.nonet_tip);
            LogUtil.e(TAG, "reequest interset use time is " + (System.currentTimeMillis() - startTime));
            LogUtil.e(TAG, "RequestInterestListener net err");
//            redirectToHome();
        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e(TAG, "RequestInterestListener data err");
//            redirectToHome();
        }

    }

    private void play() {
        if (player != null) {
            if (player.isPlaying()) {
                LogUtil.e(TAG, "视频处于播放状态");
            }
        } else {
            AssetFileDescriptor fd = null;
            // 创建视频播放的对象
            player = new MediaPlayer();
            try {
                fd = mContext.getAssets().openFd(FILENAME);
                // 指定播放的文件
                player.setDataSource(fd.getFileDescriptor(),
                        fd.getStartOffset(), fd.getLength());
                // 指定用于播放视频的SurfaceView的控件
                player.setDisplay(holder);
                player.prepare();
                LogUtil.e(TAG, "play prepare done");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            setVideoSize();
            if (currentPosition == 0) {
                // 显示位移动画
                showLogoMoveAnim();
            }

            if (isFrist) {
                //循环播放
            } else {
                // 播放3秒淡出
                mHandler.postDelayed(mTask = new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        redirectToHome();
                    }
                }, 3000);
                // 视频淡出
                //showVideoHideAnim();
            }
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //然后开始播放视频
                    LogUtil.e(TAG, "play currentPosition is " + currentPosition);
                    player.seekTo(currentPosition);
                    player.start();
                    PasueFlag = false;
                }
            });

            player
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentPosition = player.getCurrentPosition();
                            LogUtil.e(TAG, "onCompletion " + currentPosition);
                            if (isFrist) {
                                resetVideo();
                            } else {
                                // 当MediaPlayer播放完成后触发
                                LogUtil.e(TAG, "onComletion called");
                                if (player != null) {
                                    player.stop();
                                    player.release();
                                    player = null;
                                }
                            }
                        }
                    });

            /**
             * 播放出差错的时候触发的事件
             *
             * 回调函数
             */
            player
                    .setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp,
                                               int what, int extra) {
                            LogUtil.e(TAG, "error and mp is " + mp + " what is " + what + " extra is " + extra);
//                                    if (player != null) {
//                                        player.stop();
//                                        player.release();
//                                        player = null;
//                                    }
                            return false;
                        }
                    });
        }
//        }
    }

    // logo位移动画
    private void showLogoMoveAnim() {
        mLogo.clearAnimation();
        final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, (float) -0.1);
        mShowAction.setDuration(1500);
        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //TODO
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        mLogo.getLayoutParams());
//                params.setMargins();
//                mLogo.clearAnimation();
//                mLogo.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLogo.startAnimation(mShowAction);
        mShowAction.setFillAfter(true);
    }

    //展现兴趣选择页动画
    private void showGuideAnim() {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                1, Animation.RELATIVE_TO_SELF, 0);
        mShowAction.setDuration(300);
        mInterestGuide.startAnimation(mShowAction);
        mInterestGuide.setVisibility(View.VISIBLE);
    }

    private void showInterest(){
        if (isFrist) {
            setting.edit().putBoolean("FIRST", false).commit();
        }
        mLogo.setVisibility(View.GONE);
        mBottom.setVisibility(View.GONE);
        showGuideAnim();
    }


    private void gotoInterest() {
        if (mList != null) {
            showInterest();
        }else {
            redirectToHome();
        }
    }

    //降低透明度
    private void showVideoAnim() {
        AlphaAnimation anima = new AlphaAnimation(1f, 0.75f);
        anima.setDuration(1000);
        surfaceView.startAnimation(anima);
        anima.setFillAfter(true);
    }

    // 视频淡出动画
//    private void showVideoHideAnim() {
//        mLayout.clearAnimation();
//        AlphaAnimation anima = new AlphaAnimation(1f, 0f);
//        anima.setDuration(3000);
//        mLayout.startAnimation(anima);
//        anima.setFillAfter(true);
//        anima.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                stop();
//                redirectToHome();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//    }

    private void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
            // 暂停标识
            PasueFlag = true;
        }
    }

    public void resetVideo() {
        LogUtil.e(TAG, "prepare resetVideo");
        if (player == null)
            LogUtil.e(TAG, "player is null");
        if (player != null) {
            LogUtil.e(TAG, "resetVideo");
            player.seekTo(0);
            player.start();
            PasueFlag = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // 当Surface尺寸等参数改变时触发
        LogUtil.e(TAG, "surfaceChanged called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.e(TAG, "surfaceCreated called");
        play();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.release();
            player = null;
        }
        if (mTask != null) {
            LogUtil.e(TAG, "surfaceDestroyed called and mTask not null");
            mHandler.removeCallbacks(mTask);
        }
        if (mInitTask != null && mInitTask.getStatus() != AsyncTask.Status.FINISHED) {
            mInitTask.cancel(true);
            showDefaultLoadingAnima(); // TODO
            LogUtil.e(TAG, "surfaceDestroyed called and initTask not complete");
        } else
            LogUtil.e(TAG, "surfaceDestroyed called");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initView();
        if (isFrist)
            setListener();
        initData();
    }
    private class Gesturelistener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            float distance =motionEvent.getY() - motionEvent1.getY();
            if(distance>30){
                gotoInterest();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }
    }
}