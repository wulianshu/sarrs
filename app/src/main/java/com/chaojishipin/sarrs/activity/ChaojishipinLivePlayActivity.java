package com.chaojishipin.sarrs.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.LiveDataEntity;
import com.chaojishipin.sarrs.bean.LivePlayData;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayLiveController;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.TrafficStatsUtil;
import com.chaojishipin.sarrs.utils.Utils;

/**
 * Created by wangyemin on 2016/1/26.
 */
public class ChaojishipinLivePlayActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener {

    private ImageView mPlayBack;

    private final static String TAG = "ChaojishipinLivePlayActivity";

    private int mSysAPILevel = 0;

    public boolean isFromBackground = false;//从后台切换回来，解决暂停时，从后台切换回来播放从头开始的bug

    private boolean isClickBack = false;

    private Window mWindow;

    private PlayLiveController mLiveController;

    private LivePlayData mPlayData;

    public final static int NET_SETTING_REQUEST_CODE = 0;

    public static int mNetType;

    public final static String pageid = "00S002009_1";

    private LiveDataEntity livedataentity;

    private long begintime = 0l;
    public String getPeid() {
        return Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()) + begintime;
    }

    public enum NetWork {
        WIFI,//1
        GSM, //0
        OFFLINE //-1
    }

    NetWork mNet;

    public NetWork getNetWork() {
        return mNet;
    }

    public void setNetWork(NetWork net) {
        this.mNet = net;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ref = getIntent().getExtras().getString("ref");
        liveDataEntity = (LiveDataEntity) getIntent().getSerializableExtra("livedataentity");
        setContentView(R.layout.activity_liveplayer);
        begintime = System.currentTimeMillis();
        doNotLockScreen();
        initData();
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    private String ref ;

    public LiveDataEntity getLiveDataEntity() {
        return liveDataEntity;
    }

    public void setLiveDataEntity(LiveDataEntity liveDataEntity) {
        this.liveDataEntity = liveDataEntity;
    }

    private LiveDataEntity liveDataEntity;
    /**
     * 设置当前屏幕不锁屏
     */
    private void doNotLockScreen() {
        mWindow = getWindow();
        // 设置当前屏幕不锁屏
        mWindow.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initData() {
        mPlayBack = (ImageView) findViewById(R.id.mediacontroller_top_back);
        mPlayBack.setOnClickListener(this);
        mSysAPILevel = Utils.getAPILevel();
        initTrafficStats(mSysAPILevel);
        Intent intent = getIntent();
        mLiveController = new PlayLiveController(this);
        livedataentity = (LiveDataEntity) intent.getSerializableExtra(Utils.LIVEDATAENTITY);
        if (null != intent && null != intent.getSerializableExtra(Utils.LIVE_PLAY_DATA)) {
            mPlayData = (LivePlayData) intent.getSerializableExtra(Utils.LIVE_PLAY_DATA);
            // 将播放数据提供给播放控制台使用
            mLiveController.setmPlayData(mPlayData);
        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
        LogUtil.e(TAG, "NET WORK ! " + netType);
        this.mNetType = netType;
        if (netType == -1) {
            setNetWork(NetWork.OFFLINE);
        } else if (netType == 0) {
            setNetWork(NetWork.GSM);
        } else if (netType == 1) {
            setNetWork(NetWork.WIFI);
        }
        if (mLiveController != null) {
            mLiveController.setPlayerControllerBarState(netType);
        }
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }


    /**
     * 动态展现状态栏 使用这种方式能够使系统状态栏浮在最顶层显示而不占用Activity空间
     *
     * @param enable
     */
    public void statusBarShow(boolean enable) {
        Window window = getWindow();
        if (enable) {
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
        } else {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        UmengPagePath.endpage(ConstantUtils.AND_FULL_PLAY, this);
        // 停止播放器页面刷新
        if (null != mLiveController) {
            mLiveController.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengPagePath.beginpage(ConstantUtils.AND_FULL_PLAY, this);
        if (!Utils.getScreenLockStatus() && null != mLiveController) {
            mLiveController.onResume();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "!!!!!onStop!!!!!!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isFromBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveController.uploadstat(System.currentTimeMillis() - begintime);
        isFromBackground = false;
    }

    /**
     * 初始化网速
     *
     * @param sdkVersion
     */
    private void initTrafficStats(int sdkVersion) {
        if (sdkVersion >= 8) {
            TrafficStatsUtil.getPreRxByte();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && 0 == event.getRepeatCount()) {
            isClickBack = true;
            return true;
        }
        return false;
    }

    public int getmSysAPILevel() {
        return mSysAPILevel;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isClickBack) {
            LogUtil.e(TAG, "!!!!!!!!!!!dispatchKeyEvent!!!!!!!!");
            exitEvent();
            isClickBack = false;
        }
        return super.dispatchKeyEvent(event);
    }

    public Window getmWindow() {
        return mWindow;
    }

    private void exitEvent() {
        if (null != mLiveController) {
            mLiveController.exitNormal();
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mediacontroller_top_back:
                exitEvent();
                break;
            default:
                break;
        }
    }
}
