package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.SearchHistoryAdapter;
import com.chaojishipin.sarrs.adapter.SearchNoResultAdapter;
import com.chaojishipin.sarrs.adapter.SearchResultAdapter;
import com.chaojishipin.sarrs.adapter.SearchSuggestAdapter;
import com.chaojishipin.sarrs.adapter.SearchToplistAdapter;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainActivityData;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchKeyWords;
import com.chaojishipin.sarrs.bean.SearchResultDataList;
import com.chaojishipin.sarrs.bean.SearchResultInfos;
import com.chaojishipin.sarrs.bean.SearchSuggestDataList;
import com.chaojishipin.sarrs.bean.SearchSuggestInfos;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.dao.SearchHistoryDao;
import com.chaojishipin.sarrs.http.parser.XunFeiJsonParser;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.WaveView;
import com.dangdang.original.common.util.Blur;
import com.dangdang.original.common.util.ScreenShot;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, onRetryListener {
    private static final String TAG = SearchActivity.class.getSimpleName();

    public final String pageid = "00S002007";
    private Bitmap bitmap;//高斯模糊图片
    private TextView mVoiceText;//右上角文案显示
    private String mSearchKey;//搜索词
    private int mPageIndex = 1;//分页页数
    private int mPageSize = 20;//分页大小

    private RelativeLayout mSearchactivity_layout_mohu;// 半透明searchActivity界面
    private RelativeLayout mSearchactivity_main_layout;//搜索启动页面
    private RelativeLayout mSearchactivity_result_layout;//搜索结果界面
    private ImageView mEt_search_delete_icon;//清空按钮
    private TextView mSearchactivity_main_layout_tipTest;//搜索界面提示语

    private AutoCompleteTextView mAutoCompleteTextView;
    private PullToRefreshListView mPullToRefreshListView;// 搜索结果listview
    private ListView mSearchactivity_suggest_layout_listView;//suggest和搜索无结果的listview
    private ListView mSearchactivity_history_layout_listView;//搜索历史listview
    private SearchHistoryAdapter mSearchHistoryAdapter;//搜索历史adapter
    private SearchSuggestAdapter mSearchSuggestAdapter;//搜索suggest adapter
    private SearchResultAdapter mSearchResultAdapter;//搜索结果adapter
    private SearchNoResultAdapter mSearchNoResultAdapter;//搜索无结果adapter

    private SearchResultInfos mResult;//搜索结果数据
    private SearchSuggestInfos mSuggestInfo;//sugest结果

    private SearchKeyWords mHistorywords;//搜索历史记录
    private SearchHistoryDao searchHistoryDao;//搜索历史数据库
    private RelativeLayout mSearchactivity_history_layout_head;//搜索历史页head
    private TextView mSearchactivity_history_layout_head_clear;//搜索历史页head清空按钮

    private RelativeLayout mSearchactivity_noresult_layout_head;//搜索无结果页head

    private ImageView mSearchactivity_main_icon_layout_textsearch;// 搜索图标
    private ImageView mSearchactivity_main_icon_layout_back;// 返回图标

    private WaveView mWaveView;// 录音view
    private NetStateView mNetView;// 无网络view

    private SarrsArrayList<SearchResultDataList> resultDataLists; //搜索结果list

    private TextView search_noresult;

    /**
     * 推荐热词
     */
    private ArrayList<String> toplistWords;
    private SearchToplistAdapter mSearchToplistAdapter;//搜索toplist adapter
    private TextView section_text;

    /******
     * 讯飞相关
     ******/

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    int ret = 0; // 函数调用返回值

    /************
     * 讯飞相关
     ***************/
    private String inputtype = "-";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity_layout);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //高斯模糊，需要等contentView都加载完毕，才能执行，所以需要延时。
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
        //在高斯模糊的背景上加个半透明色
//                findViewById(R.id.searchactivity_layout_mohu).setBackgroundResource(R.color.color_cc222222);
        blurBackgound(findViewById(R.id.searchactivity_layout));
//            }
//        }, 80);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        exitXunFei();

    }

    @Override
    protected View setContentView() {
        return null;
    }

    /**
     * 初始化组件
     */
    private void initView() {
        mNetView = (NetStateView) findViewById(R.id.searchactivity_netview);
        mVoiceText = (TextView) findViewById(R.id.searchactivity_main_layout_voice_text);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.et_search_topbar_edit);
        mEt_search_delete_icon = (ImageView) findViewById(R.id.et_search_delete_icon);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.searchactivity_result_layout_PullToRefreshListView);
        mSearchactivity_suggest_layout_listView = (ListView) findViewById(R.id.searchactivity_suggest_layout_listView);
        mSearchactivity_suggest_layout_listView.setVerticalFadingEdgeEnabled(false);
        mSearchactivity_suggest_layout_listView.setHorizontalFadingEdgeEnabled(false);
        mSearchactivity_suggest_layout_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mSearchactivity_history_layout_listView = (ListView) findViewById(R.id.searchactivity_history_layout_listView);
        mSearchactivity_history_layout_listView.setVerticalFadingEdgeEnabled(false);
        mSearchactivity_history_layout_listView.setHorizontalFadingEdgeEnabled(false);
        mSearchactivity_history_layout_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mSearchactivity_result_layout = (RelativeLayout) findViewById(R.id.searchactivity_result_layout);
        mSearchactivity_layout_mohu = (RelativeLayout) findViewById(R.id.searchactivity_layout_mohu);
        mSearchactivity_main_layout = (RelativeLayout) findViewById(R.id.searchactivity_main_layout);
        mWaveView = (WaveView) findViewById(R.id.waveview);
        mSearchactivity_main_layout_tipTest = (TextView) findViewById(R.id.searchactivity_main_layout_tipTest);
        executeTipTextShow();

        mSearchactivity_history_layout_head = (RelativeLayout) getLayoutInflater().inflate(R.layout.searchactivity_history_layout_head, null);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dip2px(40));
        mSearchactivity_history_layout_head.setLayoutParams(lp);
        mSearchactivity_history_layout_head_clear = (TextView) mSearchactivity_history_layout_head.findViewById(R.id.searchactivity_history_layout_head_clear);
        section_text = (TextView) mSearchactivity_history_layout_head.findViewById(R.id.section_text);
        mSearchactivity_history_layout_listView.addHeaderView(mSearchactivity_history_layout_head);
        section_text.setVisibility(View.GONE);
        mSearchactivity_history_layout_head_clear.setVisibility(View.GONE);

        mSearchactivity_noresult_layout_head = (RelativeLayout) getLayoutInflater().inflate(R.layout.searchactivity_noresult_head, null);
        AbsListView.LayoutParams l = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dip2px(145));
        mSearchactivity_noresult_layout_head.setLayoutParams(l);

        mSearchactivity_main_icon_layout_textsearch = (ImageView) findViewById(R.id.searchactivity_main_icon_layout_textsearch);
        mSearchactivity_main_icon_layout_back = (ImageView) findViewById(R.id.searchactivity_main_icon_layout_back);
        if (NetWorkUtils.isNetAvailable()) {
            hideNetView();
        } else {
            showNetView();
        }

        setListener();

        initXunFeiView();

        mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_textsearch);
        setSearchResultGone();
        initDataBase();


////      PackageManager pm = getPackageManager();
//        boolean permission = PackageManager.PERMISSION_GRANTED == checkCallingPermission("android.permission.RECORD_AUDIO");
//
//
////        getApplicationContext().enforceCallingPermission("android.permission.RECORD_AUDIO", "RECORD_AUDIO permission deny");
////
////        boolean permission = (PackageManager.PERMISSION_GRANTED ==pm.checkPermission("android.permission.RECORD_AUDIO",  this.getPackageName()));
//
//        if (!permission) {
//            mIat.cancel();
//            mWaveView.stopWave();
//            mVoiceText.setVisibility(View.GONE);
//            setSearchResultVisibility();
//            mAutoCompleteTextView.setFocusable(true);
//            mAutoCompleteTextView.requestFocus();
//            mAutoCompleteTextView.setText("");
//            showSoftKeyboard();
//            mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
//        }
    }

    // 显示 netview
    private void showNetView() {
        mSearchactivity_layout_mohu.setVisibility(View.GONE);
        mNetView.setVisibility(View.VISIBLE);
        mNetView.setErrorTitle(R.string.net_record_msg);
    }

    // 隐藏 netview
    private void hideNetView() {
        mSearchactivity_layout_mohu.setVisibility(View.VISIBLE);
        mNetView.setVisibility(View.GONE);
    }

    /**
     * 搜索历史数据库f
     */
    private void initDataBase() {
        searchHistoryDao = new SearchHistoryDao(this);
        mHistorywords = searchHistoryDao.getAll();
    }

    private void setListViewMode(PullToRefreshBase.Mode mode) {
        mPullToRefreshListView.setMode(mode);
        mPullToRefreshListView.getLoadingLayoutProxy().setPullLabel("加载更多");
    }

    private void setListEnd(int total) {
        if (isAllDataDone(total)) {
            mPullToRefreshListView.setNoMoreDataFooter(this, "已加载完毕");
            setListViewMode(PullToRefreshBase.Mode.DISABLED);
        } else {
            mPullToRefreshListView.hiddenNoMoreDataFooter(true);
        }
    }

    private boolean isAllDataDone(int total) {
        if (mPageIndex * mPageSize >= total)
            return true;
        return false;
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    private void setListener() {
        mNetView.setOnRetryLisener(this);
        mEt_search_delete_icon.setOnClickListener(this);
        mSearchactivity_main_layout.setOnClickListener(this);

        mSearchactivity_history_layout_head_clear.setOnClickListener(this);
        mSearchactivity_main_icon_layout_textsearch.setOnClickListener(this);
        mSearchactivity_main_icon_layout_back.setOnClickListener(this);

        mPullToRefreshListView.setOnItemClickListener(this);
        mSearchactivity_suggest_layout_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("search_item_click", "suggest and recomment position is " + position);
                inputtype = "3";
                String key = null;
                Object item = parent.getAdapter().getItem(position);
                if (item != null) {
                    Log.i("search_item_click", "item type is " + item.toString());
                    if (item instanceof SearchSuggestDataList) {
                        key = ((SearchSuggestDataList) item).getTitle();
                        Log.i("search_item_click", "suggest position is " + position + " and key is " + key);
                        if (!StringUtil.isEmpty(key)) {
                            mSearchKey = key;
                            executeSearchRequest();
                        }
                    } else if (item instanceof MainActivityAlbum) {
                        MainActivityAlbum album = ((MainActivityAlbum) item);
                        key = album.getTitle();
                        Log.i("search_item_click", "recomment key is " + key);
                        Intent intent = new Intent(SearchActivity.this, ChaoJiShiPinVideoDetailActivity.class);

                        List<VideoItem> videoItems = album.getVideos();
                        PlayData playData = null;
                        if (videoItems != null && videoItems.size() > 0) {
                            playData = new PlayData(videoItems.get(0).getTitle(), videoItems.get(0).getGvid(), ConstantUtils.PLAYER_FROM_SEARCH, videoItems.get(0).getSource());
                        }
                        intent.putExtra("playData", playData);
                        VideoDetailItem videoDetailItem = new VideoDetailItem();
                        videoDetailItem.setTitle(album.getTitle());
                        videoDetailItem.setDescription(album.getDescription());
                        videoDetailItem.setId(album.getId());
                        videoDetailItem.setBucket(album.getBucket());
                        videoDetailItem.setReid(album.getReId());
                        videoDetailItem.setCategory_id(album.getCategory_id());
                        videoDetailItem.setPlay_count(album.getPlay_count());
                        videoDetailItem.setVideoItems(album.getVideos());
                        videoDetailItem.setFromMainContentType(album.getContentType());
                        videoDetailItem.setDetailImage(album.getImgage());
                        videoDetailItem.setSource(album.getSource());
                        intent.putExtra("videoDetailItem", videoDetailItem);
                        intent.putExtra("ref",pageid);
                        startActivity(intent);
                    }
                }
            }
        });
        mSearchactivity_suggest_layout_listView.setOnTouchListener(new MyLVTouchListener());
        mSearchactivity_history_layout_listView.setOnItemClickListener(new MyOnItemClickListener());
        mSearchactivity_history_layout_listView.setOnTouchListener(new MyLVTouchListener());
        /**
         * 下拉刷新
         * @author daipei
         */
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isAllDataDone(Integer.parseInt(mResult.getTotal()))) {

//                mPageIndex = 1;
//                requestSearchResultData(null);

                    mPageIndex++;
                    requestSearchResultData(mResult);
                } else {
                    setListViewMode(PullToRefreshBase.Mode.DISABLED);
                }

            }
        });
        /**
         * 滑动到最底部，进行分页加载
         * @author daipei
         */
//        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//
//            @Override
//            public void onLastItemVisible() {
//                mPageIndex++;
//                requestSearchResultData(mResult);
//            }
//        });
//        点击软键盘右下角按钮的监听事件
        mAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event
                        .getAction() == KeyEvent.ACTION_DOWN)) {
                    inputtype = "2";
                    mSearchKey = mAutoCompleteTextView.getText().toString();
                    requestSearchResultData(null);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        //搜索框文本变化监听
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSuggest(s);
                LogUtil.e(TAG, "mAutoCompleteTextView changed ");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mSearchKey != null && mSearchKey.length() > 0)
                    getSuggest(mSearchKey);
            }
        });
        // list滚动时，软键盘收起
        mPullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideSoftKeyboard(mPullToRefreshListView);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onRetry() {
        if (NetWorkUtils.isNetAvailable()) {
            mVoiceText.setVisibility(View.VISIBLE);
            hideNetView();
            mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_before_start);
            setSearchResultGone();
            executeTipTextShow();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initXunFeiView();
                    mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_textsearch);
                }
            }, 100);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        {
            if (position == 1)
                return;

            LogUtil.e("onItemClick", "position " + (position - 1));
            if (resultDataLists != null && resultDataLists.size() > 0) {

                Intent intent = new Intent(this, ChaoJiShiPinVideoDetailActivity.class);
                //
                SearchResultDataList item = (SearchResultDataList) resultDataLists.get(position - 1);
                //Object object,String acode,String pageid,String ref,String rank,String rid_topcid,String sa,String pn,String input
                int mypos = position -1;
                UploadStat.uploadstat(mResult,"-","00S002007","00S002001",(mypos)+"","-","1",mPageIndex+"",inputtype);
                List<VideoItem> videoItems = item.getVideos();
                LogUtil.e("onItemClick", "videoItem is " + videoItems.get(0));
                VideoItem videoItem = videoItems.get(0);

                PlayData playData = null;
                if (videoItems != null && videoItems.size() > 0) {
                    playData = new PlayData(item.getTitle(), videoItem.getGvid(), ConstantUtils.PLAYER_FROM_SEARCH, item.getSource());
                    LogUtil.e("Redirect ", " from SearchActivity " + item.getSource());
                }
                intent.putExtra("playData", playData);
                VideoDetailItem videoDetailItem = new VideoDetailItem();
                videoDetailItem.setTitle(item.getTitle());
                videoDetailItem.setId(item.getId());
                videoDetailItem.setCategory_id(item.getCategory_id());
                videoDetailItem.setPlay_count(item.getPlay_count());
                videoDetailItem.setVideoItems(item.getVideos());
                videoDetailItem.setBucket(bucket);
                videoDetailItem.setReid(reid);
                // 视频来源
                videoDetailItem.setSource(item.getSource());
                videoDetailItem.setFromMainContentType(item.getContent_type());
                videoDetailItem.setDetailImage(item.getImage());
                intent.putExtra("ref", pageid);
                intent.putExtra("seid",mResult.getReid());
                intent.putExtra("videoDetailItem", videoDetailItem);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search_delete_icon:
                mAutoCompleteTextView.setText("");

                break;
            // 在语言搜索界面触摸屏幕
            case R.id.searchactivity_main_layout:
                if (mIat.isListening()) {
                    //正在识别，取消重新识别声音
                    mIat.cancel();
                    mWaveView.stopWave();
                    mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_stop);
                } else {
                    // 准备识别声音
                    mIatResults.clear();
                    // 设置参数
                    setXunFeiParam();
                    mIat.startListening(recognizerListener);
                    mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_before_start);
                    executeTipTextShow();
                }
//                Toast.makeText(SearchActivity.this,""+mIat.isListening(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.searchactivity_history_layout_head_clear:
//                searchHistoryDao.delAll();
                clearHistory();
                //显示推荐
//                requestRecommendData();
                mSearchKey = null;
                requestToplistData();
                break;
            case R.id.searchactivity_main_icon_layout_back:
                finish();
                break;
            case R.id.searchactivity_main_icon_layout_textsearch:
                if (mSearchactivity_main_layout.isShown()) {
                    //切换至普通搜索
                    mIat.cancel();
                    mWaveView.stopWave();
                    mVoiceText.setVisibility(View.GONE);
                    setSearchResultVisibility();
                    mAutoCompleteTextView.setFocusable(true);
                    mAutoCompleteTextView.requestFocus();
                    mAutoCompleteTextView.setText("");
                    showSoftKeyboard();
                    mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
                } else {
                    //切换至语言搜索
                    mSearchKey = null;
                    hideSoftKeyboard(mSearchactivity_main_icon_layout_textsearch);
                    mVoiceText.setVisibility(View.VISIBLE);
                    mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_before_start);
                    executeTipTextShow();

                    setSearchResultGone();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initXunFeiView();
                            mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_textsearch);
                        }
                    }, 100);
                }

                break;
            default:
                break;
        }
    }

    // history 列表项点击事件监听
    public class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            inputtype = "4";//历史和热门是一起的
            String key = (String) parent.getAdapter().getItem(position);
            if (!StringUtil.isEmpty(key)) {
                mSearchKey = key;
                Log.i("search_item_click", "history position is " + position + " and key is " + key);
                executeSearchRequest();
            }
        }
    }

    private void getSuggest(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            mEt_search_delete_icon.setVisibility(View.VISIBLE);
            mSearchKey = s.toString();
            requestSearchSuggestData();
        } else {
            if (StringUtil.isEmpty(mAutoCompleteTextView.getText().toString())) {
                mEt_search_delete_icon.setVisibility(View.GONE);
                if (mHistorywords != null && null != mHistorywords.getWords() && mHistorywords.getWords().size() > 0) {
                    setHistoryDataToAdapter();
                    LogUtil.e(TAG, "history not null");
                } else {
                    LogUtil.e(TAG, "history is null");
                    requestToplistData();
                }
            }
        }
    }

    /**
     * 初始化讯飞控件
     */
    private void initXunFeiView() {
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, new InitListener() {
            @Override
            public void onInit(int i) {

                LogUtil.e("xll","xunfei init call back "+i);

            }
        });
        startXunFei();
    }

    /**
     * 开启讯飞语音
     */
    private void startXunFei() {
        mAutoCompleteTextView.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setXunFeiParam();
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
//            showTip("听写失败,错误码：" + ret);
            LogUtil.e("wulianshu","讯飞语音权限拒绝了");
        } else {
            LogUtil.e("wulianshu","讯飞语音权限允许使用");
//            showTip(getString(R.string.text_begin));
        }
    }

    /**
     * 讯飞参数设置
     */
    private void setXunFeiParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");// 普通话

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "2000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/iflytek/wavaudio.pcm");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");
    }

    private void exitXunFei() {
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener recognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {
            // 音量值0~30
//            showTip("当前正在说话，音量大小：" + volume);
            if (mIat.isListening()) {
                if (NetWorkUtils.isNetAvailable()) {
                    hideNetView();
                    mWaveView.startWave(volume);
                } else {
                    showNetView();
                    mIat.cancel();
                    mWaveView.stopWave();
                }
            } else {
                mIat.cancel();
                mWaveView.stopWave();
            }
        }

        @Override
        public void onBeginOfSpeech() {
//            showTip("开始说话");
//            ToastUtil.showLongToast(getApplicationContext(), "开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
//            showTip(error.getPlainDescription(true));
            if (error.getErrorCode() >= 20001 && error.getErrorCode() <= 20003) {
                showNetView();
                return;
            }
            mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_stop);
            mIat.cancel();
            mWaveView.stopWave();
            ToastUtil.showShortToast(SearchActivity.this, getResources().getString(R.string.search_no_voice));
        }

        @Override
        public void onEndOfSpeech() {
//            showTip("结束说话");
//            ToastUtil.showLongToast(getApplicationContext(), "结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            // 听写结果回调接口(返回Json格式结果，用户可参见 附录12.1)；
            // 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
            // 关于解析Json的代码可参见MscDemo中JsonParser类；
            // isLast等于true时会话结束。
            LogUtil.d(TAG, results.getResultString());

//            showTip("结果");
            printResult(results);

            if (isLast) {
                //TODO 直接请求 （解决语音搜索失败问题）
                inputtype = "1";
                executeSearchRequest();
                // TODO 最后的结果
//                mHandler.sendEmptyMessageDelayed(ConstantUtils.HANDLER_MESSAGEDELAYED_1000, 1000);//延迟，显示AutoCompleteTextView
            }
        }

//        @Override
//        public void onVolumeChanged(int volume) {
//
//        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 扩展接口
        }
    };

    /**
     * 讯飞回调结果解析
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = XunFeiJsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        Iterator it = mIatResults.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            resultBuffer.append(entry.getValue());
        }
        mSearchKey = resultBuffer.toString();
//        ToastUtil.showShortToast(this, "mSearchKey is " + mSearchKey);

    }

    // 一秒后显示开始录音内容
    private void executeTipTextShow() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIat.isListening())
                    mSearchactivity_main_layout_tipTest.setText(R.string.search_main_tip_start);
            }
        }, 1000);
    }


    // 执行请求结果列表展示
    private void executeSearchRequest() {
        setSearchResultVisibility();
        mAutoCompleteTextView.setText(mSearchKey);
        Log.i("search_item_click", "executeSearchRequest-->" + mSearchKey);
        if (null != mSearchKey && mSearchKey.length() > 0) {
            mAutoCompleteTextView.setSelection(mSearchKey.length());
        }
        requestSearchResultData(null);
    }

    @Override
    protected void handleInfo(Message msg) {
        switch (msg.what) {
            case ConstantUtils.HANDLER_MESSAGEDELAYED_1000:
                executeSearchRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 高斯模糊背景图片
     *
     * @param view
     */
    private void blurBackgound(final View view) {
        try{
            if(bitmap == null){
                bitmap = ((ChaoJiShiPinApplication)getApplication()).getBitmap();
                bitmap = Blur.fastblur(this, bitmap, 6);
            }
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }catch(Throwable e){
            view.setBackgroundColor(Color.DKGRAY);
            e.printStackTrace();
        }
    }

    /**
     * 执行请求搜索结果接口数据
     *
     * @param result 1.正常请求，传入null
     *               2.分页请求，传入mResult
     */
    private void requestSearchResultData(SearchResultInfos result) {
        if (null == result) {
            mPageIndex = 1;
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHSUGGEST_TAG);
        // HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHRESULT_TAG);
        HttpApi.getSearchResultRequest(mSearchKey, mPageIndex, mPageSize, result).start(new RequestSearchResultListener(), ConstantUtils.REQUEST_SEARCHRESULT_TAG);
    }

    /**
     * 执行请求搜索suggest接口数据
     */
    private void requestSearchSuggestData() {
        mPageIndex = 1;
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHRESULT_TAG);
        // HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHSUGGEST_TAG);
        HttpApi.getSearchSuggestRequest(mSearchKey).start(new RequestSearchSuggestListener(), ConstantUtils.REQUEST_SEARCHSUGGEST_TAG);
    }

    /**
     * 搜索结果为空，执行请求推荐接口数据
     */
    private void requestRecommendData() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHRESULT_TAG);
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCHSUGGEST_TAG);
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_MAINACTIVITY_DATA);
        HttpApi.getMainActivityDataRequest(this, "0", ConstantUtils.MAINACTIVITY_REFRESH_AREA).start(new RequestRecommendListener(), ConstantUtils.REQUEST_MAINACTIVITY_DATA);
    }

    private void requestToplistData() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SEARCH_TOPLIST_TAG);
        HttpApi.getToplistRequest().start(new RequestTopicListener(), ConstantUtils.REQUEST_SEARCH_TOPLIST_TAG);
    }

    String bucket = "";
    String reid = "";

    private class RequestSearchResultListener implements RequestListener<SearchResultInfos> {

        @Override
        public void onResponse(SearchResultInfos result, boolean isCachedData) {

            saveToHistory(mSearchKey);
            mResult = result;
            if(mResult!=null && mResult.getItems()!=null && mResult.getItems().size()>0){
                UploadStat.uploadstat(mResult, "-", "00S002007", "00S002001", "-", "-", "0", mPageIndex + "", inputtype);
            }else{
                UploadStat.uploadstat(mResult, "-", "00S002007", "00S002007_1", "-", "-", "0", mPageIndex + "", inputtype);
            }
            if (null != mResult) {
                resultDataLists = mResult.getItems();
                bucket = mResult.getBucket();
                reid = mResult.getReid();
                if (null != resultDataLists && resultDataLists.size() > 1) {
                    setDataToAdapter();
                    /**
                     * 延迟，取消刷新动画，因为这个Listview控件有bug，不延迟的话，无效
                     */
                    Runnable tasks = new Runnable() {
                        public void run() {
                            mPullToRefreshListView.onRefreshComplete();
                            if (mResult != null)
                                setListEnd(Integer.parseInt(mResult.getTotal()));
                        }
                    };
                    new Handler().postDelayed(tasks, 100);
                } else
                    requestRecommendData();
            } else {
                //结果为空，显示推荐
                requestRecommendData();
            }

        }

        @Override
        public void netErr(int errorCode) {
            showNetView();
//            Toast.makeText(SearchActivity.this, "netErr", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void dataErr(int errorCode) {
            //结果为空，显示推荐
            requestRecommendData();
        }
    }

    private class RequestSearchSuggestListener implements RequestListener<SearchSuggestInfos> {
        @Override
        public void onResponse(SearchSuggestInfos result, boolean isCachedData) {
            mSuggestInfo = result;
            setSuggestDataToAdapter();
            mPullToRefreshListView.onRefreshComplete();
        }

        @Override
        public void netErr(int errorCode) {
            showNetView();
//            Toast.makeText(SearchActivity.this, "netErr", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void dataErr(int errorCode) {
            mSuggestInfo = null;
            setSuggestDataToAdapter();
        }
    }

    ;

    /**
     * 推荐接口请求
     */
    private class RequestRecommendListener implements RequestListener<MainActivityData> {

        @Override
        public void onResponse(MainActivityData result, boolean isCachedData) {
            if (null != result) {
                ArrayList<MainActivityAlbum> albums = result.getAlbumList();
                if (null != albums && albums.size() > 0) {

                    setNoResultDataToAdapter(albums);
                }
            }
        }

        @Override
        public void netErr(int errorCode) {
            showNetView();
        }

        @Override
        public void dataErr(int errorCode) {

        }
    }

    private class RequestTopicListener implements RequestListener<SarrsArrayList> {
        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            if (toplistWords != null)
                toplistWords.clear();
            else
                toplistWords = new ArrayList<>();
            toplistWords.addAll(result);

            showToplist();
        }

        @Override
        public void netErr(int errorCode) {
            showToplist();
        }

        @Override
        public void dataErr(int errorCode) {
            showToplist();
        }
    }

    ;

    /**
     * searchResult
     */
    private void setDataToAdapter() {
        if (null != mSearchSuggestAdapter) {
            if (null != mSuggestInfo) {
                mSuggestInfo.getItems().clear();
                mSuggestInfo = null;
            }
        }

        if (null == mSearchResultAdapter) {
            mSearchResultAdapter = new SearchResultAdapter(SearchActivity.this, mResult);
            mPullToRefreshListView.setAdapter(mSearchResultAdapter);
        } else {
            mSearchResultAdapter.setData(mResult);
        }

        if (null != mSearchactivity_suggest_layout_listView && mSearchactivity_suggest_layout_listView.isShown()) {
            mSearchactivity_suggest_layout_listView.setVisibility(View.GONE);
        }

        if (null != mSearchactivity_history_layout_listView) {
            mSearchactivity_history_layout_listView.setVisibility(View.GONE);
        }
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        setListViewMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
    }

    /**
     * Suggest数据适配器
     */
    private void setSuggestDataToAdapter() {
        if (null != mSearchResultAdapter) {
            if (null != mResult) {
                mResult.getItems().clear();
                mResult = null;
            }
            mSearchResultAdapter = null;
        }

        if (null != mSearchSuggestAdapter) {
            mSearchSuggestAdapter = null;
        }
        if (null != mSearchactivity_history_layout_listView) {
            mSearchactivity_history_layout_listView.setVisibility(View.GONE);
        }

        mSearchSuggestAdapter = new SearchSuggestAdapter(SearchActivity.this, mSuggestInfo);
//        mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
        changeHistorySectionText(true, true);
        mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
        mSearchactivity_suggest_layout_listView.setAdapter(mSearchSuggestAdapter);
//        }
//        else{
//            mSearchSuggestAdapter.setData(mSuggestInfo);
//        }
        if (null != mPullToRefreshListView && mPullToRefreshListView.isShown()) {
            mPullToRefreshListView.setVisibility(View.GONE);
        }
        mSearchactivity_suggest_layout_listView.setVisibility(View.VISIBLE);
        mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
    }

    /**
     * History数据适配器
     */
    private void setHistoryDataToAdapter() {
        if (null != mSearchResultAdapter) {
            if ((null != mResult) && (null != mResult.getItems())) {
                mResult.getItems().clear();
            }
            mResult = null;
            mSearchResultAdapter = null;
        }
        if (mSuggestInfo != null && mSuggestInfo.getItems() != null) {
            if (mSuggestInfo.getItems() != null)
                mSuggestInfo.getItems().clear();
            mSuggestInfo = null;
            mSearchSuggestAdapter = null;
        }
        if (null != mSearchHistoryAdapter) {
            mSearchHistoryAdapter = null;
        }
        if (null != mHistorywords && null != mHistorywords.getWords() && mHistorywords.getWords().size() > 0) {
            mSearchHistoryAdapter = new SearchHistoryAdapter(SearchActivity.this, mHistorywords.getWords());
            mSearchactivity_history_layout_listView.setAdapter(null);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
//            mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
//            mSearchactivity_history_layout_listView.addHeaderView(mSearchactivity_history_layout_head);
            mSearchactivity_history_layout_listView.setAdapter(mSearchHistoryAdapter);
            changeHistorySectionText(false, true);
        } else {
            mSearchactivity_history_layout_listView.setAdapter(null);
            changeHistorySectionText(true, true);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
        }

        if (null != mPullToRefreshListView && mPullToRefreshListView.isShown()) {
            mPullToRefreshListView.setVisibility(View.GONE);
        }
        if (null != mSearchactivity_suggest_layout_listView) {
            mSearchactivity_suggest_layout_listView.setVisibility(View.GONE);
        }

        mSearchactivity_history_layout_listView.setVisibility(View.VISIBLE);
        mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
    }

    /**
     * 搜索无结果
     */
    private void setNoResultDataToAdapter(ArrayList<MainActivityAlbum> albums) {
        if (null != mSearchSuggestAdapter) {
            if (null != mSuggestInfo) {
                mSuggestInfo.getItems().clear();
                mSuggestInfo = null;
            }
            mSearchSuggestAdapter = null;
        }
        if (null != mSearchResultAdapter) {
            mResult.getItems().clear();
            mResult = null;
            mSearchResultAdapter = null;
        }
        if (null != mSearchactivity_history_layout_listView) {
            mSearchactivity_history_layout_listView.setVisibility(View.GONE);
        }
        mSearchNoResultAdapter = new SearchNoResultAdapter(SearchActivity.this, albums);
        mSearchactivity_suggest_layout_listView.setAdapter(null);
//        mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
        changeHistorySectionText(true, true);
        mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
        mSearchactivity_suggest_layout_listView.addHeaderView(mSearchactivity_noresult_layout_head);
        mSearchactivity_suggest_layout_listView.setAdapter(mSearchNoResultAdapter);
        search_noresult = (TextView) findViewById(R.id.search_noresult);
        if (mResult != null) {
            if (mResult.getIllegal_flag().equals("1")) {
                search_noresult.setText(getResources().getString(R.string.search_illegal));
            } else {
                search_noresult.setText(getResources().getString(R.string.search_noresult));
            }
        }

        if (null != mPullToRefreshListView && mPullToRefreshListView.isShown()) {
            mPullToRefreshListView.setVisibility(View.GONE);
        }
        mSearchactivity_suggest_layout_listView.setVisibility(View.VISIBLE);
        mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
    }

    private void clearHistory() {
        searchHistoryDao.delAll();
        if (mHistorywords != null)
            mHistorywords.getWords().clear();
        if (null != mSearchHistoryAdapter) {
            mSearchHistoryAdapter = null;
        }
        mSearchactivity_suggest_layout_listView.setVisibility(View.GONE);
    }

    //搜索结果界面显示
    private void setSearchResultVisibility() {
        mSearchactivity_result_layout.setVisibility(View.VISIBLE);
        mSearchactivity_main_layout.setVisibility(View.GONE);

    }

    //搜索结果界面隐藏
    private void setSearchResultGone() {
        clearSeearchView();
        mSearchactivity_result_layout.setVisibility(View.GONE);
        mSearchactivity_main_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 清空所有listview
     */
    private void clearSeearchView() {
        mResult = null;
        mSuggestInfo = null;
        mPullToRefreshListView.setAdapter(null);
        mPullToRefreshListView.setVisibility(View.GONE);
        mSearchactivity_suggest_layout_listView.setAdapter(null);
        mSearchactivity_suggest_layout_listView.setVisibility(View.GONE);
    }

    private void showToplist() {
        if (null != mSearchResultAdapter) {
            if ((null != mResult) && (null != mResult.getItems())) {
                mResult.getItems().clear();
            }
            mResult = null;
            mSearchResultAdapter = null;
        }
        if (mSuggestInfo != null && mSuggestInfo.getItems() != null) {
            if (mSuggestInfo.getItems() != null)
                mSuggestInfo.getItems().clear();
            mSuggestInfo = null;
            mSearchSuggestAdapter = null;
        }
        if (null != mSearchHistoryAdapter) {
            mSearchHistoryAdapter = null;
        }
        if (null != toplistWords && toplistWords.size() > 0) {
            mSearchToplistAdapter = new SearchToplistAdapter(SearchActivity.this, toplistWords);
            mSearchactivity_history_layout_listView.setAdapter(null);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
//            mSearchactivity_suggest_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
//            mSearchactivity_history_layout_listView.addHeaderView(mSearchactivity_history_layout_head);
            mSearchactivity_history_layout_listView.setAdapter(mSearchToplistAdapter);
            changeHistorySectionText(false, false);
        } else {
            mSearchactivity_history_layout_listView.setAdapter(null);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_history_layout_head);
//            mSearchactivity_history_layout_listView.removeHeaderView(mSearchactivity_noresult_layout_head);
            changeHistorySectionText(true, false);
        }

        if (null != mPullToRefreshListView && mPullToRefreshListView.isShown()) {
            mPullToRefreshListView.setVisibility(View.GONE);
        }
        if (null != mSearchactivity_suggest_layout_listView) {
            mSearchactivity_suggest_layout_listView.setVisibility(View.GONE);
        }

        mSearchactivity_history_layout_listView.setVisibility(View.VISIBLE);
        if (mSearchactivity_main_layout.isShown()) {
            mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_textsearch);
        } else {
            mSearchactivity_main_icon_layout_textsearch.setImageResource(R.drawable.selector_search_voicesearch);
        }
    }

    /**
     * 历史记录和搜索热词使用同一个header
     *
     * @param hideSection 是否隐藏搜索header
     * @param isHistory   true:搜索历史, false:搜索热词
     */
    private void changeHistorySectionText(boolean hideSection, boolean isHistory) {
        if (hideSection) {
            section_text.setVisibility(View.GONE);
            mSearchactivity_history_layout_head_clear.setVisibility(View.GONE);
        } else {
            section_text.setVisibility(View.VISIBLE);
            if (isHistory) {
                section_text.setText(getResources().getString(R.string.search_history));
                mSearchactivity_history_layout_head_clear.setVisibility(View.VISIBLE);
            } else {
                section_text.setText(getResources().getString(R.string.search_toplist));
                mSearchactivity_history_layout_head_clear.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示软键盘
     */
    private void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 收起软键盘
     */
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mAutoCompleteTextView.getWindowToken(), 0);
    }

    /**
     * 收起软键盘
     */
    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 保存搜索历史
     *
     * @param word
     */
    private void saveToHistory(String word) {
        if (!TextUtils.isEmpty(word)) {
            mHistorywords.getWords().remove(word);
            if (mHistorywords.getWords().size() == ConstantUtils.SEARCH_RECORD_MAXIMUM_SIZE) {

                mHistorywords.getWords().remove(ConstantUtils.SEARCH_RECORD_MAXIMUM_SIZE - 1);
            }
            mHistorywords.getWords().add(0, word);
            searchHistoryDao.save(word);
        }
    }

    class MyLVTouchListener implements View.OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN: {
                    //收期软键盘
                    hideSoftKeyboard(mPullToRefreshListView);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }

                default:

                    break;
            }
            return false;
        }
    }

    //解决系统改变字体大小的时候导致的界面布局混乱的问题
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void launch(Activity ac){
        Bitmap bm = ScreenShot.shoot(ac);
        ((ChaoJiShiPinApplication)ac.getApplication()).setBitmap(bm);
        Intent intent = new Intent(ac, SearchActivity.class);
        ac.startActivity(intent);
    }
}
