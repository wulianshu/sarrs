package com.chaojishipin.sarrs.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.fragment.VideoDetailMediaBottomFragment;
import com.chaojishipin.sarrs.fragment.videoplayer.VideoPlayerFragment;
import com.chaojishipin.sarrs.fragment.videoplayer.httpd.M3u8Httpd;
import com.chaojishipin.sarrs.receiver.NetWorkStateReceiver;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.ibest.thirdparty.share.presenter.ShareManager;
import com.ibest.thirdparty.share.view.ShareDialog;

import java.io.IOException;

/**
 * 详情页
 *
 * @author xulinlin
 */

public class ChaoJiShiPinVideoDetailActivity extends ChaoJiShiPinBaseActivity {
    public static final String SAVE_CHECK = "saveCheck";
    public static final String SHARE_CANCEL = "shareCancel";
    // 表示是否第一次进入半屏播放页
    private boolean isFirst = true;
    private String TAG = this.getClass().getSimpleName();
    // 播放器底部布局
    private LinearLayout mMediaBottomView;
    //  本地播放
    private static M3u8Httpd m3u8Httpd =null;
    /**
     * 播放器大小屏幕状态.默认是半屏
     */
    public static boolean isFullScreen = false;
    public static int mNetType;

//    public VideoDetailItem getmVideoDetailItem() {
//        return mVideoDetailItem;
//    }
//
//    public void setmVideoDetailItem(VideoDetailItem mVideoDetailItem) {
//        this.mVideoDetailItem = mVideoDetailItem;
//    }

    private VideoDetailItem mVideoDetailItem;

    public VideoDetailItem getCurrentplayVideoDetailItem() {
        return currentplayVideoDetailItem;
    }

    public void setCurrentplayVideoDetailItem(VideoDetailItem currentplayVideoDetailItem) {
        this.currentplayVideoDetailItem = currentplayVideoDetailItem;
    }

    private VideoDetailItem currentplayVideoDetailItem;
    /**
     * 播放器相关开始
     */
    private Window mWindow;
    /**
     * 播放器布局
     */
    private LinearLayout mMediaView;
    private VideoPlayerFragment mVideoPlayerFragment;
    VideoDetailMediaBottomFragment mVideoBottomFragment;
    /**
     * 网络状态
     */
    boolean mIsWifiTo3GFlag;
    /**
     * 音频焦点相关
     */
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;
    private AudioManager mAudioMgr = null;
    public static boolean isclickfull = false;
    public final static int NET_SETTING_REQUEST_CODE = 0;

    // 横竖屏转换
    private int screenWidth;
    private int screenHeight;

    private boolean sensor_flag = true;
    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;

    private SensorManager sm1;
    private Sensor sensor1;
    private OrientationSensorListener2 listener1;
    //收藏状态:已收藏:true;未收藏:false
    private boolean isSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 解决闪烁问题,方式2，选择支持半透明模式,在有surfaceview的activity中使用。
         */
       // getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //modify 1
        setTitleBarVisibile(false);
        initWindow();
        initView();
        initData();
        if(getMediaType()==MeDiaType.LOCAL){
            setFullScreenLocal();
        }
        setListener();
       //registerFinish();
    }
    FinishReceiver finishReceiver=null;
    void registerFinish(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.chaojishipin.mediaplayer.page.finish");
         finishReceiver = new FinishReceiver();
        registerReceiver(finishReceiver, filter);
    }


    public void initWindow() {
        // 设置当前屏幕不锁屏
        mWindow = getWindow();
        mWindow.setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void initView() {
        // 隐藏标题栏
        //modify 2
        setTitleBarVisibile(false);
        // 隐藏网络连接错误界面
        mMediaBottomView = (LinearLayout) findViewById(R.id.videodetail_media_bottom_fragment_container);
        mMediaView = (LinearLayout) findViewById(R.id.videodetail_medie_fragment_container);
//      isFullScreen = Utils.getScreenOrientation();
         addDetailFragment();
         addVideoPlayerFragment();
         setSmallScreenParam();
    }


    /**
     * 首页传入数据
     */
    private void initData() {
        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Intent intent = getIntent();
        // 从分享地址跳到半屏页
        mVideoDetailItem = (VideoDetailItem) intent.getExtras().get("videoDetailItem");
        currentplayVideoDetailItem = mVideoDetailItem;
        //Build.VERSION.SDK_INT表示当前SDK的版本，Build.VERSION_CODES.ECLAIR_MR1为SDK 7版本 ，
        //因为AudioManager.OnAudioFocusChangeListener在SDK8版本开始才有。
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //失去焦点之后的操作
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //获得焦点之后的操作
                    }
                }
            };
        }
    }
    //modify 7
    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
        LogUtil.e(TAG, "NET WORK ! " + netType);
        this.mNetType = netType;
        if (mVideoPlayerFragment != null) {
            if(this.getMediaType()==MeDiaType.ONLINE){
                mVideoPlayerFragment.setPlayerControllerBarState(netType);
                mVideoBottomFragment.reLoadData();
            }
        }
        if (mVideoBottomFragment != null) {
            mVideoBottomFragment.setNetStateTip(netName, netType, isHasNetWork);
        }
    }
    /**
     *   activity 启动模式为sigletask，调用后台activity
     *  */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
         mVideoDetailItem = (VideoDetailItem) intent.getExtras().get("videoDetailItem");
//        initView();

        setTitleBarVisibile(false);
        // 隐藏网络连接错误界面
        mMediaBottomView = (LinearLayout) findViewById(R.id.videodetail_media_bottom_fragment_container);
        mMediaView = (LinearLayout) findViewById(R.id.videodetail_medie_fragment_container);
        addDetailFragment();
        addVideoPlayerFragment();
        initData();
        if(this.getMediaType()==MeDiaType.LOCAL){
            // 点击下载管理跳回本地播放
            setFullScreenLocal();
        }else{
            if(isFullScreen){
                LogUtil.e("wulianshu","设置成全屏了======");
                setFullScreen();
            }else {
                setSmallScreenParam();
            }
        }
        LogUtil.e("wulianshu", " onNewIntent 被调用====== ");
    }



    @Override
    public void onStart() {
        super.onStart();
        try {
             if(m3u8Httpd==null&&getMediaType()==MeDiaType.LOCAL){
                 m3u8Httpd = new M3u8Httpd(8084);
             }

            if (m3u8Httpd!=null&&!m3u8Httpd.isAlive()) {
                m3u8Httpd.start();
            }
          /*  //注册传感器
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
            sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (m3u8Httpd!=null&&m3u8Httpd.isAlive()) {
            m3u8Httpd.stop();
        }

    }

    /**
     *  注册销毁广播
     * */

    class FinishReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

           LogUtil.e("xll", "first task media close ");
            if(intent.getAction().equalsIgnoreCase("com.chaojishipin.mediaplayer.page.finish")){
                ChaoJiShiPinVideoDetailActivity.this.finish();
            }



        }
    }




    /**


    /**
     * 设置重力感应监听
     */
    private void setListener() {
        //注册重力感应器  屏幕旋转
//        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        listener = new OrientationSensorListener(handler);
//        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);

        //根据  旋转之后 点击 符合之后 激活sm
//        sm1 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        listener1 = new OrientationSensorListener2();
//        sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 添加详情Fragment
     */
    private void addDetailFragment() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaciton = mFragmentManager.beginTransaction();
//      mVideoBottomFragment = VideoDetailMediaBottomFragment.newInstance(1);
        mVideoBottomFragment = new VideoDetailMediaBottomFragment();
//        fragmentTransaciton.add(R.id.videodetail_media_bottom_fragment_container, mVideoBottomFragment);
        fragmentTransaciton.replace(R.id.videodetail_media_bottom_fragment_container, mVideoBottomFragment);
        fragmentTransaciton.commitAllowingStateLoss();
//        fragmentTransaciton.commit();
    }

    /**
     * 添加用于播放视频的Fragment
     */
    private void addVideoPlayerFragment() {
        LogUtil.e(TAG, "add ONLINE Fragment");
        if (null == mVideoPlayerFragment) {
            mVideoPlayerFragment = new VideoPlayerFragment();
        }
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaciton = mFragmentManager.beginTransaction();
        //如果当播放页面没有被添加则开始添加当前播放页面
        if (!mVideoPlayerFragment.isAdded()) {
            fragmentTransaciton.add(R.id.videodetail_medie_fragment_container, mVideoPlayerFragment);
            fragmentTransaciton.commit();
        }
    }

    /**
     * 重力感应监听者（执行屏幕切换操作）
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        public void onSensorChanged(SensorEvent event) {

            if (sensor_flag == isFullScreen)  //只有两个不相同才开始监听行为
            {
                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y value
                if (magnitude * 4 >= Z * Z) {
                    //屏幕旋转时
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int) Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                if (rotateHandler != null) {
                    rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
                }

            }
        }
    }

    // 激活 重力感应 （监听手机旋转）
    public class OrientationSensorListener2 implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;

            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];

            /**
             * 这一段据说是 android源码里面拿出来的计算 屏幕旋转的 不懂 先留着 万一以后懂了呢
             */
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                //屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
//            Log.d("orientation", "orientation is " + orientation);
            if ((orientation > 240 && orientation < 300) || (orientation > 60 && orientation < 120)) {  //横屏
                if (isFirst) {
                    sensor_flag = true;
                    isFirst = false;
                    return;
                }
                sensor_flag = false;
            } else if ((orientation > 330 && orientation < 360) || (orientation > 0 && orientation < 30)) {  //竖屏
                sensor_flag = true;
            }

            if (isFullScreen != sensor_flag) {  //点击变成横屏  屏幕 也转横屏 激活
//                Log.d("orientation", "激活");
                sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }


    public enum MeDiaType {
        ONLINE,
        LOCAL;

    }


    public MeDiaType getMediaType() {
        if(getIntent().getStringExtra(Utils.Medea_Mode)!=null&&getIntent().getStringExtra(Utils.Medea_Mode).equalsIgnoreCase(ConstantUtils.MediaMode.LOCAL)){
            return MeDiaType.LOCAL;
        } else{
            return MeDiaType.ONLINE;
        }
    }


    @Override
    protected void onResume() {
       // requestAudioFocus();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        abandonAudioFocus();
    }

    void unRegisterFinishReceiver(){
            if (null != finishReceiver) {
                unregisterReceiver(finishReceiver);
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //modify 3
        unRegistInfo();
        //unRegisterFinishReceiver();

        if(m3u8Httpd!=null){
            m3u8Httpd= null;
        }
        isFullScreen = false;
           /* sm.unregisterListener(listener);
            sm1.unregisterListener(listener1);*/

    }

    // modify 4
    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.videodetailactivity_main_layout, null);
    }
// modify 5

    @Override
    protected void handleInfo(Message msg) {
        switch (msg.what) {
            case 888:
                int orientation = msg.arg1;
                if (orientation > 30 && orientation < 240) {

                } else if ((orientation > 240 && orientation < 300) || (orientation > 60 && orientation < 120)) {
                    System.out.println("切换成横屏");
                    ChaoJiShiPinVideoDetailActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    sensor_flag = false;
                    isFullScreen = true;
                } else if ((orientation > 330 && orientation < 360) || (orientation > 0 && orientation < 30)) {
                }
                break;
            default:
                break;
        }
    }

    public SensorManager getSm() {
        return sm;
    }

    public OrientationSensorListener getListener() {
        return listener;
    }

    /*
    *   保存挂起状态
    * */

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /*
    *   读取状态  *  */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*
    *  线程访问以及含有connection连接时用此保存 getLastNonConfigurationInstance()读取
    * */

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();

    }


    /**
     * 设置播放器半屏高度、宽度
     */
    public void setSmallScreenParam() {
        // 设置当前屏幕播放的状态为小屏播放状态
        isFullScreen = false;
        statusBarShow(true);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        screenWidth =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH,"0"));
        screenHeight =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT,"0"));
        //分享进入半屏页初次使用屏幕宽度高度还未获取到需重新获取
        if(screenWidth==0||screenHeight==0){
            SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, Utils.getScreenWidth(this) + "");
            SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT, Utils.getScreenHeight(this) + "");
            LogUtil.e("xll", "init screen Width " + Utils.getScreenWidth(this));
            LogUtil.e("xll", "init screen Height " + Utils.getScreenHeight(this));
            screenWidth =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH,"0"));;
            screenHeight =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT,"0"));
        }
        params.width =  screenWidth;
        params.height = screenWidth * 9 / 16;
        LogUtil.e("xll"," detail height "+params.height);
        LogUtil.e("xll","detail screen w h "+screenWidth+" "+screenHeight);
        mMediaView.setLayoutParams(params);
    }
  /*
  *   设置全屏播放宽度和高度
  *
  * */

    /**
     * 设置当前屏幕播放的状态为全屏播放状态
     * 需要获取当前屏幕的
     *
     */
    public void setFullScreenParams() {
        statusBarShow(false);
        isFullScreen = true;
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int screenWidth =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH,"0"));;
        int screenHeight =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT,"0"));
        //分享进入半屏页初次使用屏幕宽度高度还未获取到需重新获取
        if(screenWidth==0||screenHeight==0){
            SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, Utils.getScreenWidth(this) + "");
            SPUtil.getInstance().putString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT, Utils.getScreenHeight(this) + "");
            LogUtil.e("xll", "init screen Width " + Utils.getScreenWidth(this));
            LogUtil.e("xll", "init screen Height " + Utils.getScreenHeight(this));
            screenWidth =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH,"0"));;
            screenHeight =Integer.parseInt(SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT,"0"));

        }


        //实际测算宽度
        int realwidth =mDisplayMetrics.widthPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.width = realwidth;
        params.height = screenWidth;
        LogUtil.e("xll"," detail fullscreen matrix height "+mDisplayMetrics.heightPixels);
        LogUtil.e("xll", "detail fullscreen matrix width  " + mDisplayMetrics.widthPixels);
        LogUtil.e("xll"," detail fullscreen height "+params.height);
        LogUtil.e("xll", "detail fullscreen width  " + params.width);
                mMediaView.setLayoutParams(params);
    }

    /**
     * 设置当前屏幕播放的状态为全屏播放状态
     * 需要获取当前屏幕的
     *
     */
    public void setFullScreenLocal() {
        isFullScreen = true;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setFullScreenParams();
    }


    public VideoDetailItem getData(){

        return (VideoDetailItem) getIntent().getSerializableExtra("videoDetailItem");
    }


    /**
     * 视频播放切换横竖屏
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
//            //切换成横屏
//            setFullScreenParams();
//            statusBarShow(false);
//        } else {
//            //切换成竖屏
//            setSmallScreenParam();
//            statusBarShow(true);
//        }
//        dismissShareDialog();
    }
    public void setFullScreen() {
        LogUtil.e(TAG + ">>>>>>>>>>>>>>", "设置前---当前屏幕为竖屏");
        //mMediaBottomView.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setFullScreenParams();
        isFullScreen = true;
        if (mVideoPlayerFragment != null) {
            mVideoPlayerFragment.setmVideoPlayerControllerUIByScreen(isFullScreen);
        }
        LogUtil.e(TAG + ">>>>>>>>>>>>>>", "设置后--当前屏幕为横屏");
    }


    public void setSmallScreen() {
        LogUtil.e(TAG + ">>>>>>>>>>>>>>", "设置前--当前屏幕为横屏");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSmallScreenParam();
        isFullScreen = false;
        if (mVideoPlayerFragment != null) {
            mVideoPlayerFragment.setmVideoPlayerControllerUIByScreen(isFullScreen);
        }
        LogUtil.e(TAG + ">>>>>>>>>>>>>>", "设置后--当前屏幕为竖屏");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mVideoPlayerFragment.isLockScreen()){
                return false;
            }
            if (MeDiaType.ONLINE == getMediaType()) {
                if (mVideoPlayerFragment.getmVideoPlayerController().getmLockScreenBtn().isSelected()){
                    return false;
                }else{
                    if(isFullScreen){
                        setSmallScreenParam();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        statusBarShow(true);
                        // 隐藏剧集列表
                        mVideoPlayerFragment.getmVideoPlayerController().setSlideTriggerVisible(false);
                        mVideoPlayerFragment.getmVideoPlayerController().hideAnimSelect(10);
                        // 隐藏剧集按钮
                        mVideoPlayerFragment.getmVideoPlayerController().setSelectVisibile(false);
                        //隐藏下一集按钮
                        mVideoPlayerFragment.getmVideoPlayerController().setPlayNextVisible(false);
                    }else{
                        back();
                    }
                }
                //切换成竖屏

                return false;
            } else {
                back();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back()
    {

            // 分享过来
            if(getIntent().getData()!=null){
                LogUtil.e("xll", "jump from share");
                Intent intent = new Intent(this, ChaoJiShiPinMainActivity.class);
                // 将历史中的activity拉回到栈顶
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  //注意本行的FLAG设置
                startActivity(intent);


            }else{
                LogUtil.e("xll","jump from other(search/download/main/record/save)");
                this.finish();
            }


    }


    public Window getmWindow() {
        return mWindow;
    }

    /**
     * 动态展现状态栏 使用这种方式能够使系统状态栏浮在最顶层显示而不占用Activity空间
     *
     * @param enable 竖屏 true
     */
    public void statusBarShow(boolean enable) {
        Window window = getWindow();
        if (enable) {
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

//    @Override
//    public void observeNetWork(String netName, int netType, boolean isHasNetWork) {
//        LogUtil.e(TAG, "NET WORK ! " + netType);
//        this.mNetType = netType;
//        if (mVideoPlayerFragment != null) {
//            mVideoPlayerFragment.setPlayerControllerBarState(netType);
//        }
//        if (mVideoBottomFragment != null) {
//            mVideoBottomFragment.setNetStateTip(netName, netType, isHasNetWork);
//        }
//        super.observeNetWork(netName, netType, isHasNetWork);
//    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        AudioManager mAudioMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioMgr != null) {
            int ret = mAudioMgr.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            }
        }
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (mAudioMgr != null) {
            mAudioMgr.abandonAudioFocus(mAudioFocusChangeListener);
            mAudioMgr = null;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.NET_SETTING_REQUEST_CODE) {
            if (NetWorkUtils.isNetAvailable()) {
                mVideoBottomFragment.requestVideoDetailIndex();
            }
        }

        //新浪授权
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        ShareManager.authorCallback(requestCode, resultCode, data);
    }

    /**
     * 关闭分享弹窗框
     */
    private void dismissShareDialog()
    {
        ShareDialog.dismiss();
    }

    public boolean isSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

}