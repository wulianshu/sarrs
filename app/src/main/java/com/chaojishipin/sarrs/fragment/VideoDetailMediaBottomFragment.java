package com.chaojishipin.sarrs.fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinRegisterActivity;
import com.chaojishipin.sarrs.activity.DownLoadListActivity;
import com.chaojishipin.sarrs.adapter.VideoDetailBottomExpandListAdapter;
import com.chaojishipin.sarrs.adapter.VideoDetailMiddleGridAdapter;
import com.chaojishipin.sarrs.adapter.VideoDetailMiddleListViewAdapter;
import com.chaojishipin.sarrs.bean.AddComment;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CancelFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.Comment;
import com.chaojishipin.sarrs.bean.CommentEntity;
import com.chaojishipin.sarrs.bean.CommentsInfo;
import com.chaojishipin.sarrs.bean.DownloadEpisodeEntity;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UserCommentInfo;
import com.chaojishipin.sarrs.bean.VideoDetailIndex;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.util.DownloadEvent;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.manager.FavoriteManager;
import com.chaojishipin.sarrs.manager.HistoryRecordManager;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.share.ShareDataConfig;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.FloorView;
import com.chaojishipin.sarrs.widget.NoScrollGridView;
import com.chaojishipin.sarrs.widget.NoScrollListView;
import com.chaojishipin.sarrs.widget.SubComments;
import com.chaojishipin.sarrs.widget.SubFloorFactory;
import com.ibest.thirdparty.share.model.ShareData;
import com.ibest.thirdparty.share.presenter.ShareListener;
import com.ibest.thirdparty.share.view.ShareDialog;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xll on 2015/6/17.
 * 半屏页底部布局
 */
public class VideoDetailMediaBottomFragment extends ChaoJiShiPinBaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    // 播放器底部具体详情
    private TextView mTitleView, mPublisher, mPublisherName, mViewCount, mScore, mMovieScore, mDirectorName, mActorName, mType, mVideoType, mDate, mPublishDate, mArea, mVideoArea, mDes, mEpiso_num;    //详情展开按钮
    private Button mDropDownBtn;
    //展开详情标示
    private boolean isDrop;
    // 点击按钮隐藏布局
    private Context mContext;
    // 折叠layout // 导演布局 // 主演布局 //
    private LinearLayout videodetail_drop_ln, mVideodetail_score_layout, mVideodetail_director_layout, mVideodetail_actors_layout, mVideodetail_subCategoryName_layout, mVideodetail_date_layout, mVideodetail_area_layout;
    private String TAG = "VideoDetailMediaBottomFragment";
    // 发布布局
    private RelativeLayout mVideodetail_pushlish_layout;
    // 剧集折叠按钮
    ImageButton episode_more;
    //底部弹出剧集
    FrameLayout mBottomMenu;
    // menu 关闭按钮
    Button mTriggerBtn;
    // 底部menu是否关闭
    boolean isOpen;
    //中间剧集展示布局
    NoScrollListView mMiddleScrollListView;
    private boolean ispause;
    NoScrollGridView mMiddleScrollGridView;
    // 底部弹出布局
    ListView mBottomScrollListView;
    //首页传递过来数据
    VideoDetailItem mVideoDetailItem;
    // 本也请求数据
    VideoDetailItem selfItem;
    // 折叠Menu是否展开
    boolean isOpenMenu;
    //
    VideoDetailMiddleGridAdapter gridAdapter;
    VideoDetailMiddleListViewAdapter listAdapter;
    VideoDetailBottomExpandListAdapter tagAdapter;
    // 分页发送数据
    SparseArray<ArrayList<VideoItem>> fenyeList = new SparseArray<ArrayList<VideoItem>>();
    // 播放器开始播放回调消息
    VideoPlayerNotifytData mediaNotifyData;
    //操作栏的控件
    private ImageView iv_msg;
    private TextView tv_msg_number;
    private ImageView iv_loving;
    private ImageView iv_share;
    private ImageView iv_download;
    private LinearLayout videodetail_bar2;
    private RelativeLayout videodetail_loading;
    private LinearLayout videodetail_content;
    private RelativeLayout videodetail_media_bottom_fragment;
    private ImageView iv_loving_videodetail_fragment;

    VideoDetailItem detail;
    // 当前播放的视频数据信息
    PlayData currentPlay;

    private boolean isActivityonResumed = false;

    /**
     * retry Layout
     */
    private RelativeLayout videodetail_retry;
    private Button retry_btn;
    // 剧集展示总布局
    private LinearLayout episoLayout;
    // 详情描述
    private LinearLayout videodetail_scroll_layout;

    private int currentPostion = 0;

    // 评论
    private LinearLayout container;
    private List<Comment> datas;
    private List<CommentEntity> commentEntityList;
    private LayoutInflater inflater;

    ArrayList<HistoryRecord> local_list = new ArrayList<HistoryRecord>();
    ArrayList<HistoryRecord> totallist = new ArrayList<HistoryRecord>();
    ChaoJiShiPinVideoDetailActivity activity;
    String reqTag;
    private ListView hot_comment_lv;
    private ListView new_comment_lv;
    private TextView comment_content;
    // 本地剧集
    ArrayList<LocalVideoEpisode> localEpisodes;

    public static VideoDetailMediaBottomFragment newInstance(int index) {
        VideoDetailMediaBottomFragment details = new VideoDetailMediaBottomFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        details.setArguments(args);
        return details;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ispause = false;
        local_list = new HistoryRecordDao(mContext).getAll();
        if (container == null) {
            return null;
        }

        mContext = getActivity();
        this.inflater = inflater;
        View mBottomView = inflater.inflate(R.layout.videodetailactivity_fragment_bottom, container, false);
        //初始化布局
        initView(mBottomView);

        if (!NetWorkUtils.isNetAvailable()) {
            showRetryLayout();
            hideBottomFragment();

        } else {
            showLoading();
        }
        //请求专辑详情描述信息
        if(getActivity() instanceof ChaoJiShiPinVideoDetailActivity){
            activity =(ChaoJiShiPinVideoDetailActivity)getActivity();
        }

        mVideoDetailItem = activity.getData();
                //(VideoDetailItem) getActivity().getIntent().getSerializableExtra("videoDetailItem");
        reqTag=getActivity().getIntent().getStringExtra(ConstantUtils.reqTag);
        // 分享
        jumpFromShare();
        if(isJumpFromThird){
            mVideoDetailItem = null;
        }
        if (mVideoDetailItem != null) {
            //如果存在播放播放记录 修改playdata就好
            initParams();
        }
        // 收藏
        if(NetWorkUtils.isNetAvailable()&&UserLoginState.getInstance().isLogin()){
            initSaveOnLine();
        }

        // 播放记录 登陆且有网络且在线
        if (UserLoginState.getInstance().isLogin() && NetWorkUtils.isNetAvailable() && activity.getMediaType()==ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE) {
            ArrayList<HistoryRecord> netlist =   HistoryRecordManager.getHisToryRecordFromServer();
            if(netlist !=null){
                mergeDateServerandlocalDB(netlist);
            }
//            requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
        } else {
            //本地的播放记录
            totallist = local_list;
            preparePlay();

        }
        // 本地剧集
        localEpisodes=  activity.getAllLocalEpisodes();




        return mBottomView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivityonResumed = true;
    }

    /**
     *   从分享等跳到半屏页逻辑
     * */
    boolean isJumpFromThird;
    void jumpFromShare(){
        // 从分享跳到跳到半屏页
        Uri uri=getActivity().getIntent().getData();
        String dataStr=getActivity().getIntent().getDataString();
        LogUtil.e("xll "," share "+dataStr);
        if(uri!=null){
            mCid = uri.getQueryParameter("cid");
            mTitle=uri.getQueryParameter("title");
            mSource=uri.getQueryParameter("source");
            //mAid : share过来可以是单视频也可以是单视频
            // mAid=uri.getQueryParameter("id");
            if(uri.getScheme()!=null&&uri.getScheme().equalsIgnoreCase("sarrs1")){
                // 专辑
                mAid=uri.getQueryParameter("id");
                LogUtil.e("xll","share zj video "+mGvid);
            }else{
                // 单视频
                mGvid=uri.getQueryParameter("id");
                LogUtil.e("xll","share single video "+mGvid);
            }


            isJumpFromThird=true;
            // requestVideoDetailByMediaAuto();
        }else{
            isJumpFromThird=false;
        }
    }

    /*
    *    展示描述逻辑
    * */
    public void showDropDes() {

        // 点击展开
        if (isDrop) {
            videodetail_drop_ln.setVisibility(View.GONE);
            mDropDownBtn.setBackgroundResource(R.drawable.sarrs_pic_videodetail_arrow_btn_down);
            isDrop = !isDrop;
        } else {
            // 默认展开
            videodetail_drop_ln.setVisibility(View.VISIBLE);
            mDropDownBtn.setBackgroundResource(R.drawable.sarrs_pic_videodetail_arrow_btn_up);
            isDrop = !isDrop;
        }
    }


    public void initView(View view) {
        // scroll
        videodetail_media_bottom_fragment = (RelativeLayout) view.findViewById(R.id.videodetail_media_bottom_fragment);
        //retry界面
        videodetail_retry = (RelativeLayout) view.findViewById(R.id.videodetail_retry);
        retry_btn = (Button) view.findViewById(R.id.retry_btn);

        //loading 界面
        videodetail_scroll_layout = (LinearLayout) view.findViewById(R.id.videodetail_scroll_layout);
        videodetail_loading = (RelativeLayout) view.findViewById(R.id.videodetail_loading);
        videodetail_content = (LinearLayout) view.findViewById(R.id.videodetail_content);

        // 剧集详情
        videodetail_bar2 = (LinearLayout) view.findViewById(R.id.videodetail_bar2);
        episoLayout = (LinearLayout) view.findViewById(R.id.videodetail_layout_episo);
        mDropDownBtn = (Button) view.findViewById(R.id.videodetail_drop_btn);
        mTitleView = (TextView) view.findViewById(R.id.videodetail_title);
        mPublisher = (TextView) view.findViewById(R.id.videodetail_publisher);
        mPublisherName = (TextView) view.findViewById(R.id.videodetail_publisherName);
        mViewCount = (TextView) view.findViewById(R.id.videodetail_viewcount);

        mScore = (TextView) view.findViewById(R.id.videodetail_score);
        mMovieScore = (TextView) view.findViewById(R.id.videodetail_movie_score);
        mDirectorName = (TextView) view.findViewById(R.id.videodetail_dy_name);
        mActorName = (TextView) view.findViewById(R.id.videodetail_zy_name);
        mType = (TextView) view.findViewById(R.id.videodetail_type);
        mVideoType = (TextView) view.findViewById(R.id.videodetail_video_type);
        mDate = (TextView) view.findViewById(R.id.videodetail_date);
        mPublishDate = (TextView) view.findViewById(R.id.videodetail_publish_date);
        mArea = (TextView) view.findViewById(R.id.videodetail_area);
        mVideoArea = (TextView) view.findViewById(R.id.videodetail_area_name);
        mDes = (TextView) view.findViewById(R.id.videodetail_des);

        videodetail_drop_ln = (LinearLayout) view.findViewById(R.id.videodetail_drop_ln);
        mVideodetail_pushlish_layout = (RelativeLayout) view.findViewById(R.id.videodetail_pub_layout);
        mVideodetail_score_layout = (LinearLayout) view.findViewById(R.id.videodetail_score_layout);
        mVideodetail_director_layout = (LinearLayout) view.findViewById(R.id.videodetail_director_layout);
        mVideodetail_actors_layout = (LinearLayout) view.findViewById(R.id.videodetail_actors_layout);
        mVideodetail_subCategoryName_layout = (LinearLayout) view.findViewById(R.id.videodetail_subCategoryName_layout);
        mVideodetail_date_layout = (LinearLayout) view.findViewById(R.id.videodetail_date_layout);
        mVideodetail_area_layout = (LinearLayout) view.findViewById(R.id.videodetail_area_layout);
        mEpiso_num = (TextView) view.findViewById(R.id.video_detail_episo_num);
        videodetail_drop_ln.setVisibility(View.GONE);
        mDropDownBtn.setOnClickListener(this);
        //中间剧集列表以及grid
        mMiddleScrollGridView = (NoScrollGridView) view.findViewById(R.id.videodetail_middle_showgrid);
        mMiddleScrollListView = (NoScrollListView) view.findViewById(R.id.videodetail_middle_showlist);
        mMiddleScrollGridView.setOnItemClickListener(this);
        mMiddleScrollListView.setOnItemClickListener(this);
        // 剧集item
        episode_more = (ImageButton) view.findViewById(R.id.video_detail_bottom_more_icon);
        episode_more.setOnClickListener(this);
        //底部弹出布局
        mBottomMenu = (FrameLayout) view.findViewById(R.id.video_detail_anim_main);
        // menu 关闭按钮
        mTriggerBtn = (Button) view.findViewById(R.id.video_detail_trigger_btn);
        mTriggerBtn.setOnClickListener(this);
        // 底部弹出menu grid以及list
        //操作栏
        iv_msg = (ImageView) view.findViewById(R.id.iv_msg_videodetail_fragment);
        tv_msg_number = (TextView) view.findViewById(R.id.tv_msg_number);
        iv_loving = (ImageView) view.findViewById(R.id.iv_loving_videodetail_fragment);
        iv_share = (ImageView) view.findViewById(R.id.iv_share_videodetail_fragment);
        iv_download = (ImageView) view.findViewById(R.id.iv_download_videodetail_fragment);
        mBottomScrollListView = (ListView) view.findViewById(R.id.video_detail_anim_bottom_showlist);
        iv_msg.setOnClickListener(this);
        iv_loving_videodetail_fragment = (ImageView) view.findViewById(R.id.iv_loving_videodetail_fragment);
        iv_loving_videodetail_fragment.setOnClickListener(this);
        iv_download.setOnClickListener(this);
        iv_loving.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        retry_btn.setOnClickListener(this);
        mBottomScrollListView.setOnItemClickListener(this);
        // 评论
        container = (LinearLayout) view.findViewById(R.id.container);
        hot_comment_lv = (ListView) view.findViewById(R.id.hot_comment_lv);
        new_comment_lv = (ListView) view.findViewById(R.id.new_comment_lv);
        comment_content = (TextView) view.findViewById(R.id.comment_content);
    }

    /**
     * 刷新布局
     */
    void hideRetyLayout() {
        videodetail_retry.setVisibility(View.GONE);
    }

    void showRetryLayout() {
        videodetail_retry.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏评论
     */

    void hideBottomFragment() {
        videodetail_media_bottom_fragment.setVisibility(View.GONE);
    }

    void showBttomFragment() {
        videodetail_media_bottom_fragment.setVisibility(View.VISIBLE);
    }

    /**
     * 进入半屏页初始化收藏状态
     */
    void initSaveOnLine() {
        // 单视频
        token=UserLoginState.getInstance().getUserInfo().getToken();
        if(mVideoDetailItem!=null){
            if (TextUtils.isEmpty(mVideoDetailItem.getId())) {

                id = mVideoDetailItem.getVideoItems().get(0).getGvid();
                type = "2";
            } else {
                // 专辑
                id = mVideoDetailItem.getId();
                type = "1";
            }
        }else{

            //分享进来mVideoDetailItem是空
            if(TextUtils.isEmpty(mAid)){
                id=mGvid;
                type = "2";
            }else{
                id=mAid;
                type = "1";
            }
        }

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id, token, type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0 && result.isExists()) {
                    iv_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
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
     * 上报 收藏
     */

    void doReport() {
        DataReporter.reportAddCollection(id, mSource, mCid, type, token, netType);
    }




    /**
     * 点击收藏按钮
     */
    void checkSave(final boolean isByClick) {
        if(!isNeedSave){
            return;
        }
        buidlParam();
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id, token, type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0 && result.isExists()) {
                    if(isByClick){
                        cancelSaveOnLine();
                    }
                } else {
                    doSaveOnLine();
                    //上报
                    //doReport();
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
     * 构造 添加、是否存在、取消收藏统一参数   (参数专辑类型使用)
     */
    String id = "";
    String token = null;
    String type = "";
    String netType = NetWorkUtils.getNetInfo();

    void buidlParam() {
        token = UserLoginState.getInstance().getUserInfo().getToken();
        if(isJumpFromThird){
            // 从分享等进入 从详情接口取得参数
            if(detail!=null){
                if (TextUtils.isEmpty(detail.getId())) {
                    id = detail.getVideoItems().get(0).getGvid();
                    type = "2";
                    mSource=detail.getSource();
                    mCid=detail.getCategory_id();
                } else {
                    // 专辑
                    id = detail.getId();
                    type = "1";
                    mSource=detail.getSource();
                    mCid=detail.getCategory_id();
                }
            }



        }else{
            // 从应用自身页面进入从之前页面去
            if(mVideoDetailItem!=null){
                if (TextUtils.isEmpty(mVideoDetailItem.getId())) {
                    id = mVideoDetailItem.getVideoItems().get(0).getGvid();
                    type = "2";
                    mSource=mVideoDetailItem.getSource();
                    mCid=mVideoDetailItem.getCategory_id();
                } else {
                    // 专辑
                    id = mVideoDetailItem.getId();
                    mSource=mVideoDetailItem.getSource();
                    mCid=mVideoDetailItem.getCategory_id();
                    type = "1";
                }
            }

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
                    if(activity!=null) {
                        activity.setIsSave(false);
                    }
                    iv_loving.setImageResource(R.drawable.vedio_detail_loving_normal);
                    ToastUtil.showShortToast(getActivity(), getString(R.string.save_cancel));
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
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ADD_FAVORITE);
        HttpApi.addFavorite(id, token, type, mCid, netType, mSource, mBucket+"", mReid+"").start(new RequestListener<AddFavorite>() {
            @Override
            public void onResponse(AddFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0) {
                    activity.setIsSave(true);
                    iv_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
                    ToastUtil.showShortToast(ChaoJiShiPinApplication.getInstatnce(), getString(R.string.save_success));
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_loving_videodetail_fragment:

                if(NetWorkUtils.isNetAvailable()&&isNeedSave){
                    if (UserLoginState.getInstance().isLogin()){
                        checkSave(true);
                    }else{
                        startActivityForResult(new Intent(getActivity(), ChaojishipinRegisterActivity.class),ConstantUtils.SaveJumpTologin.VIDEOTAIL_SAVE_LOGIN);
                    }
                }
                break;
            case R.id.retry_btn:
                requestVideoDetailIndex();
                break;
            case R.id.videodetail_drop_btn:
                showDropDes();
                break;
            case R.id.video_detail_bottom_more_icon:
                showAnimBottomMenu();
                isOpenMenu = true;
                // 弹出底部布局需要更新当前播放剧集
                updateBottomExpandViewWhenPlaying();
                break;
            case R.id.video_detail_trigger_btn:
                hideAnimBottomMenu();
                isOpenMenu = false;
                updateMiddleViewWhenPlaying();
                //mBottomMenu.setLayoutParams(getHideMenuLayoutParams());
                break;
            case R.id.iv_download_videodetail_fragment:
                if(NetWorkUtils.isNetAvailable()&&isNeedSave){
                    download();
                    MobclickAgent.onEvent(getActivity(), ConstantUtils.HALFSCREEN_DOWNLOAD_BTN);
                }

                break;
            case R.id.iv_msg_videodetail_fragment:
                String vid = null;
                if (currentPlay != null) {
                    vid = currentPlay.getmEpisodes().get(currentPlay.getKey()).get(currentPlay.getIndex()).getGvid();
                    Log.d("comment", "gvid is " + vid);
                }
              /*  // 获取评论信息
                requestUserCommentInfo(vid, UserLoginState.getInstance().getUserInfo().getToken(), 0, 70745, -1, ConstantUtils.COMMENT_DEVICE);
                if (UserLoginState.getInstance().isLogin()) {
                    // 添加评论  楼中楼回复暂时无效
                    excuteCommentRequest(UserLoginState.getInstance().getUserInfo().getToken(), 0, vid, "海贼王第三集", null, 0, 0, 111, ConstantUtils.COMMENT_DEVICE);
                } else {
//                    startActivity(new Intent(getActivity(), ChaojishipinRegisterActivity.class));
                }*/
                MobclickAgent.onEvent(getActivity(),ConstantUtils.HALFSCREEN_COMMENT);

                break;
            case R.id.iv_share_videodetail_fragment: {
                if(NetWorkUtils.isNetAvailable()&&isNeedSave){

                    if(isJumpFromThird){
                        share(selfItem, false);
                    }else{
                        if(mVideoDetailItem!=null){
                            share(mVideoDetailItem, false);
                        }
                    }
                }
                // 上报需先设置参数
                buidlParam();
                // 分享上报
                DataReporter.reportAddShare(id, mSource, mCid, type, token, netType, mBucket, "" + mReid);
                MobclickAgent.onEvent(getActivity(), ConstantUtils.HALFSCREEN_SHARE_BTN);
            }
            break;
        }
    }


    /**
     * 展示loading界面
     */
    private void showLoading() {
        mDropDownBtn.setVisibility(View.GONE);
        videodetail_content.setVisibility(View.GONE);
        videodetail_loading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏loading界面
     */
    private void hideLoading() {
        videodetail_loading.setVisibility(View.GONE);
        mDropDownBtn.setVisibility(View.VISIBLE);
        videodetail_content.setVisibility(View.VISIBLE);

    }

    /**
     * 判断是否分页
     */
    public boolean isNextPage(int key, int position) {
        // 全屏点击分页Tag 分页请求
        if (fenyeList.indexOfKey(key) < 0) {
            return true;
        }
        ArrayList<VideoItem> pageList = fenyeList.get(key);
        if (pageList != null) {
            if (position < pageList.size() - 1) {
                return false;
            } else {

                return true;
            }
        }
        return false;


    }


    /**
     * 播放器正在播放时，点击弹出底部Menu需要保持和中间布局选中剧集一致
     */


    void updateBottomExpandViewWhenPlaying() {
        if (mediaNotifyData != null) {

            updateBottomExpandViewWhenStartPlay(mediaNotifyData);

        }


    }

    /**
     * 播放器正在播放时，点击隐藏底部Menu需要保持和中间布局选中剧集一致
     */


    void updateMiddleViewWhenPlaying() {
        if (mediaNotifyData != null) {
            updateMiddleExposideViewWhenStartPlay(mediaNotifyData);
        }
    }


    /**
     * 接收播放器开始播放时消息&底部Menu布局隐藏   展示中间剧集展示view，只有专辑时会执行
     */
    void updateMiddleExposideViewWhenStartPlay(VideoPlayerNotifytData data) {
        if (data != null) {
            LogUtil.e("xll", " update Middle data " + data.getPosition());
            if (isNextPage(data.getKey(), data.getPosition())) {
                if(TextUtils.isEmpty(data.getType())||!data.getType().equals(ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK)){
                    LogUtil.e("xll"," update middle 从其他页面进入播放页，点击底部剧集，隐藏后更新中间剧集");
                    // 半屏更新剧集面板
                    if (mCid.equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_1)) || mVideoDetailItem.getCategory_id().equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_3))) {
                        VideoDetailMiddleGridAdapter adapter = (VideoDetailMiddleGridAdapter) mMiddleScrollGridView.getAdapter();
                        if (adapter != null) {
                            adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList);
                        }

                    } else {
                        VideoDetailMiddleListViewAdapter adapter = (VideoDetailMiddleListViewAdapter) mMiddleScrollListView.getAdapter();
                        if (adapter != null) {
                            LogUtil.e("xll "," adpater pageTag num is "+mPageNum);
                            adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList);
                        }

                    }


                }else{
                    LogUtil.e("xll"," do nothing");


                }


                // requestVideoDetailByMediaAuto();

            } else {
                //无需分页需要更新剧集展示
                if (mCid != null) {
                    if (mCid.equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_1)) || mCid.equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_3))) {

                        VideoDetailMiddleGridAdapter adapter = (VideoDetailMiddleGridAdapter) mMiddleScrollGridView.getAdapter();
                        if (adapter != null) {
                            adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList);
                        }

                        LogUtil.e("udpate", "update key" + data.getKey() + " position " + data.getPosition());

                    } else {
                        VideoDetailMiddleListViewAdapter adapter = (VideoDetailMiddleListViewAdapter) mMiddleScrollListView.getAdapter();
                        if (adapter != null) {
                            adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList);
                        }

                        LogUtil.e("udpate", "update key" + data.getKey() + " position " + data.getPosition());
                    }

                }

            }
        }


    }


    /**
     * 接收播放器开始播放消息更新剧集展示&底部弹出view展开状态时， 只有专辑时会执行
     */
    void updateBottomExpandViewWhenStartPlay(VideoPlayerNotifytData data) {
        if (data.getReqKey()!=data.getKey()) {
            LogUtil.e("Expand ", " key " + data.getKey() + " position " + data.getPosition());
            // 执行分页请求
            if (data.getType() == ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK) {
                // 请求key
                mPn=data.getReqKey();
            } else {
                // 自动联播
                mPn=data.getKey() + 1;
                if(mPn>=mPageNum){
                    mPn=mPageNum-1;
                }
            }
            VideoDetailBottomExpandListAdapter adapter = (VideoDetailBottomExpandListAdapter) mBottomScrollListView.getAdapter();
            adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList, mCid);
            requestVideoDetailByMediaAuto();
        } else {
            VideoDetailBottomExpandListAdapter adapter = (VideoDetailBottomExpandListAdapter) mBottomScrollListView.getAdapter();
            if(data!=null){
                adapter.updateCurrentVideoInPage(data.getKey(), data.getPosition(), 6, fenyeList, mCid);
            }
        }
    }

    /**
     * 接收来自播放器数据   播放器在开始播放时发送消息
     */
    public void onEventMainThread(VideoPlayerNotifytData data) {
        // 判断fragment是否加载到activity中

        if (this.isAdded()) {
            if (data != null) {
                LogUtil.e("xll","receive from top (k,p,r) "+data.getKey()+" "+data.getPosition()+" "+data.getReqKey());
                mediaNotifyData = data;
                // 点击全屏剧集分页tag 时，设置当前分页数mPn，请求详情接口
                mPn=data.getReqKey();
                //点击分页tag不需要更新剧集位置
                LogUtil.e("OnEvent get update key", "" + data.getReqKey());
                LogUtil.e("OnEvent get key", "" + data.getKey());
                LogUtil.e("OnEvent get position", "" + data.getPosition());
                //底部弹出Menu
                // 判断播放器是否在线播放模式
                if (!data.isLocal()) {
                    LogUtil.e(TAG, "msg from BottomFragment Media Mode is Online！");
                    // 自动联播以及拖动进度条-->更新半屏剧集展示
                    //   if (isOpenMenu) {
                    updateBottomExpandViewWhenStartPlay(mediaNotifyData);
                    updateMiddleExposideViewWhenStartPlay(mediaNotifyData);

                   /* } else {
                        //隐藏底部Menu模式下中间布局的分页请求
                        if (mediaNotifyData == null) {
                            LogUtil.e("Mode ", " Bottom Menu data is null");
                            return;
                        }
                    }*/
                } else {
                    // 播放离线文件模式
                    LogUtil.e(TAG, "msg from BottomFragment Media Mode is Local！");
                    hideLoading();
                    hideEpisoLayout(null, true);
                }


            }
        }


    }

    public void onEventMainThread(String data) {
        if (data.equals(ChaoJiShiPinVideoDetailActivity.SAVE_CHECK))
        {
            updateSaveStatus();
        }
    }

    /**
     * 收藏状态更新
     */
    private void updateSaveStatus()
    {
        if (((ChaoJiShiPinVideoDetailActivity)getActivity()).isSave())
        {
            iv_loving.setImageResource(R.drawable.vedio_detail_loving_pressed);
        }else {
            iv_loving.setImageResource(R.drawable.vedio_detail_loving_normal);
        }
        MobclickAgent.onEvent(getActivity(), ConstantUtils.HALFSCREEN_COLLECTION_BTN);

    }

    /**
     * onItemClick
     */
    int mCurrentTagIndex;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.videodetail_middle_showgrid:
                LogUtil.e(TAG, "" + position);
                int pageNum = gridAdapter.getPageNum();
                List<VideoItem> gridList = gridAdapter.getData();
                for (int i = 0; i < gridList.size(); i++) {
                    if (i == (position + (pageNum - 1) * 6)) {
                        gridList.get(i).setIsPlay(true);

                    } else {
                        gridList.get(i).setIsPlay(false);
                    }
                }
                gridAdapter.setData(fenyeList);
                gridAdapter.notifyDataSetChanged();
                LogUtil.e(TAG, "" + position);
                int middlePost = (Math.max((gridAdapter.getPn() - 1), 0) * gridAdapter.getPageSize() + position + (gridAdapter.getPageNum() - 1) * gridAdapter.getSmallPageSize()) % gridAdapter.getPageSize();
                currentPlay = new PlayData(fenyeList, gridAdapter.getPn(), middlePost, ConstantUtils.PLAYER_FROM_DETAIL_ITEM);

                LogUtil.e("POST", "middle grid key " + gridAdapter.getPn());
                LogUtil.e("POST", "middle grid positon " + middlePost);
                if(NetworkUtil.isNetworkAvailable(activity)){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("v1.1.2","handle episo net ok logic");
                            EventBus.getDefault().post(currentPlay);
                        }
                    }, 0);

                }else {

                    if(fenyeList.indexOfKey(gridAdapter.getPn())>=0&&fenyeList.get(gridAdapter.getPn())!=null&&fenyeList.get(gridAdapter.getPn()).size()>0&&fenyeList.get(gridAdapter.getPn()).size()>middlePost){

                        if(fenyeList.get(gridAdapter.getPn()).get(middlePost).isLocal()){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.e("v1.1.2","handle episo error logic post local data");
                                    EventBus.getDefault().post(currentPlay);
                                }
                            }, 0);
                        }else{
                            Toast.makeText(activity,activity.getString(R.string.nonet_tip),Toast.LENGTH_SHORT).show();

                            LogUtil.e("v1.1.2","handle episo error logic");

                        }

                    }else{
                        LogUtil.e("v1.1.2","handle episo error logic");

                        Toast.makeText(activity,activity.getString(R.string.nonet_tip),Toast.LENGTH_SHORT).show();

                    }




                }


                break;
            case R.id.videodetail_middle_showlist:
                int pageNum2 = listAdapter.getPageNum();
                LogUtil.e(TAG, "" + position);
                List<VideoItem> listItems = listAdapter.getData();
                for (int i = 0; i < listItems.size(); i++) {
                    if (i == (position + (pageNum2 - 1) * 6)) {
                        listItems.get(i).setIsPlay(true);
                    } else {
                        listItems.get(i).setIsPlay(false);
                    }

                }

                listAdapter.setData(fenyeList);
                listAdapter.notifyDataSetChanged();
                int middlePost2 = (Math.max((listAdapter.getPn() - 1), 0) * listAdapter.getPageSize() + position + (pageNum2 - 1) * listAdapter.getSmallPageSize()) % listAdapter.getPageSize();
                LogUtil.e(TAG, "" + position);
                currentPlay = new PlayData(fenyeList, listAdapter.getPn(), middlePost2, ConstantUtils.PLAYER_FROM_DETAIL_ITEM);


                if(NetworkUtil.isNetworkAvailable(activity)){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("v1.1.2","handle episo net ok logic");
                            currentPlay.setFrom(ConstantUtils.PLAYER_FROM_DETAIL_ITEM);
                            EventBus.getDefault().post(currentPlay);
                        }
                    }, 0);
                }else {

                    if(fenyeList.indexOfKey(listAdapter.getPn())>=0&&fenyeList.get(listAdapter.getPn())!=null&&fenyeList.get(listAdapter.getPn()).size()>0&&fenyeList.get(listAdapter.getPn()).size()>middlePost2){

                        if(fenyeList.get(listAdapter.getPn()).get(middlePost2).isLocal()){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.e("v1.1.2","handle episo error logic post local data");
                                    EventBus.getDefault().post(currentPlay);
                                }
                            }, 0);
                        }else{
                            Toast.makeText(activity,activity.getString(R.string.nonet_tip),Toast.LENGTH_SHORT).show();

                            LogUtil.e("v1.1.2","handle episo error logic");

                        }

                    }else{
                        LogUtil.e("v1.1.2","handle episo error logic");

                        Toast.makeText(activity,activity.getString(R.string.nonet_tip),Toast.LENGTH_SHORT).show();

                    }



                }




                break;
            case R.id.video_detail_anim_bottom_showlist:

                LogUtil.e("VideoBottomList", "" + id);

                if (view.getTag() instanceof VideoDetailBottomExpandListAdapter.EpisodesTagHolder
                        && parent.getAdapter() instanceof VideoDetailBottomExpandListAdapter) {
                    VideoDetailBottomExpandListAdapter tagAdapter = (VideoDetailBottomExpandListAdapter) parent.getAdapter();

                    mCurrentTagIndex=position;
                    LogUtil.e("xll","current TagIndex "+mCurrentTagIndex);
                    if (tagAdapter.getInStatePosition(VideoDetailBottomExpandListAdapter.STATE_EXPANDED) == position) {
                        tagAdapter.collaspPosition(position);

                    } else if (tagAdapter.getCachedData(position) != null) {
                        LogUtil.e("Cache ", " not null expand");
                        tagAdapter.expandPosition(position, fenyeList);
                        parent.setSelection(position);

                    } else {
                        LogUtil.e("Cache ", " is null expand");

                        tagAdapter.setPositionInLoading(position);
                        //startRequest(sk, position, isSourceDetail);
                        // 请求页索引
                        VideoDetailIndex indexItem = new VideoDetailIndex();
                        // 设置分页请求参数
                        indexItem.setPn(position);

                        requestVideoEpisoListForExpandGridByHandClick(mVideoDetailItem, indexItem, parent, tagAdapter);
                    }
                }

                break;

        }
    }

    public void showAnimBottomMenu() {
        mBottomMenu.clearAnimation();
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                1, Animation.RELATIVE_TO_SELF, 0);
        mShowAction.setDuration(300);
        mBottomMenu.startAnimation(mShowAction);
        mBottomMenu.setVisibility(View.VISIBLE);
    }

    public void hideAnimBottomMenu() {
        mBottomMenu.clearAnimation();
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                1);
        mHiddenAction.setDuration(300);
        mBottomMenu.startAnimation(mHiddenAction);
        mBottomMenu.setVisibility(View.GONE);
    }

    public boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);

    }

    /**
     *    点击按钮折叠详情逻辑
     *    如果字段没有需要补位
     *
     * */
    public void showDetailsInfos(VideoDetailItem item) {
        if (isEmpty(item.getDirector())) {
            if (!isEmpty(item.getActor())) {
                mPublisher.setText(mContext.getResources().getString(R.string.videodetail_actors));
                mPublisherName.setText(item.getActor());
            }
            mVideodetail_actors_layout.setVisibility(View.VISIBLE);
            mVideodetail_director_layout.setVisibility(View.GONE);
        } else {
            mPublisher.setText(mContext.getResources().getString(R.string.videodetail_director));
            mPublisherName.setText(item.getDirector());
            if (isEmpty(item.getActor())) {
                mVideodetail_actors_layout.setVisibility(View.GONE);
            }
            mVideodetail_director_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  展示半屏播放页详情
     * */
    public void showVideoDetail(VideoDetailItem item, VideoPlayerNotifytData data) {

        if (this.isAdded() || isJumpFromThird) {
            if (item != null && item.getCategory_id() != null) {
                if (item.getCategory_id().equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_1)) || item.getCategory_id().equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_3))) {
                    if (item.getVideoItems() != null && item.getVideoItems().size() > 0) {
                        mMiddleScrollGridView.setVisibility(View.VISIBLE);
                        if(fenyeList.get(data.getKey())!=null && fenyeList.get(data.getKey()).get(data.getPosition()) !=null) {
                            fenyeList.get(data.getKey()).get(data.getPosition()).setIsPlay(true);
                        }
                        gridAdapter = new VideoDetailMiddleGridAdapter(getActivity(), fenyeList,mPageNum);
                        gridAdapter.setPn(data.getKey());
                        gridAdapter.setPosition(data.getPosition());
                        mMiddleScrollGridView.setAdapter(gridAdapter);

                    } else {
                        hideEpisoLayout(null, true);
                    }


                } else {
                    if (item.getVideoItems() != null && item.getVideoItems().size() > 0 && fenyeList !=null) {
                        mMiddleScrollListView.setVisibility(View.VISIBLE);
                        if (fenyeList.get(data.getKey())!=null && fenyeList.get(data.getKey()).get(data.getPosition())!=null){
                            fenyeList.get(data.getKey()).get(data.getPosition()).setIsPlay(true);
                        }
                        listAdapter = new VideoDetailMiddleListViewAdapter(getActivity(), fenyeList,mPageNum);
                        listAdapter.setPn(data.getKey());
                        listAdapter.setPosition(data.getPosition());
                        mMiddleScrollListView.setAdapter(listAdapter);
                    } else {
                        hideEpisoLayout(null, true);
                    }

                }
                if (!isEmpty(item.getIs_end())) {
                    if ("0".equals(item.getIs_end())) {
                        if (!isEmpty(item.getEpiso_latest())) {
                            mEpiso_num.setText(getResources().getString(R.string.videodetailfragment_latestjuji) + item.getEpiso_latest() + getResources().getString(R.string.episode));
                        } else {
                            mEpiso_num.setVisibility(View.GONE);
                        }
                    } else if ("1".equals(item.getIs_end())) {
                        if (!isEmpty(item.getEpiso_num())) {
                            mEpiso_num.setText(getResources().getString(R.string.videodetailfragment_quanji) + item.getEpiso_num() + getResources().getString(R.string.episode));
                        } else {
                            mEpiso_num.setVisibility(View.GONE);
                        }
                    }
                }
                if (isEmpty(item.getTitle())) {
                    mTitleView.setVisibility(View.GONE);
                }
                if (isEmpty(item.getDescription())) {
                    mDes.setVisibility(View.GONE);
                }
                List<String> metaList = item.getMataList();
                int size = metaList.size();
                String directorName = null;
                String actorName = null;
                switch (size) {
                    case 0:
                        break;
                    case 1:
                        directorName = metaList.get(0);
                        break;
                    case 2:
                        directorName = metaList.get(0);
                        actorName = metaList.get(1);
                        break;
                    default:
                        directorName = metaList.get(0);
                        actorName = metaList.get(1);
                        break;
                }
                if (isEmpty(item.getPublisher())) {
                    // 显示保证折叠
                    showDetailsInfos(item);
                } else {
                    mPublisher.setText(mContext.getResources().getString(R.string.videodetail_publisher));
                    mPublisherName.setText(item.getPublisher());
                    if (isEmpty(item.getScore())) {
                        mVideodetail_score_layout.setVisibility(View.GONE);
                    }
                    if (isEmpty(directorName)) {
                        mVideodetail_director_layout.setVisibility(View.GONE);
                    }
                    if (isEmpty(actorName)) {
                        mVideodetail_actors_layout.setVisibility(View.GONE);
                    }
                    if (isEmpty(item.getSub_category_name())) {
                        mVideodetail_subCategoryName_layout.setVisibility(View.GONE);
                    }
                    if (isEmpty(item.getPublish_date())) {
                        mVideodetail_date_layout.setVisibility(View.GONE);
                    }
                    if (isEmpty(item.getArea_name())) {
                        mVideodetail_area_layout.setVisibility(View.GONE);
                    }
                }

                mTitleView.setText(item.getTitle());
                if(mVideoDetailItem!=null){
                    selfItem.setFromMainContentType(mVideoDetailItem.getFromMainContentType());
                }
                mViewCount.setText(item.getPlay_count() + " " + getString(R.string.view_count));
                mMovieScore.setText(item.getScore());
                mDirectorName.setText(directorName);
                mActorName.setText(actorName);
                mVideoType.setText(item.getSub_category_name());
                mPublishDate.setText(item.getPublish_date());
                mVideoArea.setText(item.getArea_name());
                mDes.setText(mContext.getResources().getString(R.string.videodetail_des) + item.getDescription());
            }
        }


    }
    /*
    *  覆盖父类方法
    * */

    @Override
    protected void handleInfo(Message msg) {

    }


    /**
     * 接受 index  执行全屏下载
     *
     * @param episodeEntity
     */
    public void onEventMainThread(DownloadEpisodeEntity episodeEntity) {
        Log.d("fullscreen_down", "onEventBackground收到了消息：" + episodeEntity.toString());
        DownloadEvent event = new DownloadEvent();
        if (mVideoDetailItem == null || TextUtils.isEmpty(mVideoDetailItem.getCategory_id())) {
            Log.d("fullscreen_down", " video excetption no  cid！");
            return;
        }
        mVideoDetailItem.setVideoItems(episodeEntity.getmEpisodes().get(episodeEntity.getKey()));
        Log.d("fullscreen_down", "index is " + episodeEntity.getKey());
        // 下载
        event.downloadFile(getActivity(), mVideoDetailItem, episodeEntity.getIndex());
    }

    public void onEventMainThread(VideoItem videoItem) {
        share(videoItem, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 添加评论
     *
     * @param token
     * @param type
     * @param text
     * @param soundUrl
     * @param soundSecond
     * @param toCommentId
     * @param playTime
     * @param device
     */
    private void excuteCommentRequest(String token, int type, String vid, String text, String soundUrl, int soundSecond, long toCommentId, int playTime, String device) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ADDCOMMENTINFO_TAG);
        if (type == 0) {
            soundUrl = "";
            soundSecond = 0;
        }
        HttpApi.addCommentRequest(token, type, vid, text, soundUrl, soundSecond, toCommentId, playTime, device).start(new RequsetAddCommentListener(), ConstantUtils.REQUEST_ADDCOMMENTINFO_TAG);
    }

    /**
     * 获取用户评论信息
     *
     * @param vid
     * @param token
     * @param hot
     * @param cursor
     * @param forward
     * @param device
     */
    public void requestUserCommentInfo(String vid, String token, int hot, long cursor, int forward, String device) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_USERCOMMENTINFO_TAG);
        HttpApi.getUserCommentInfoRequest(vid, token, hot, cursor, forward, device).start(new RequsetUserCommentInfoListener(), ConstantUtils.REQUEST_USERCOMMENTINFO_TAG);
    }

    /**
     * 评论信息回调监听
     */
    private class RequsetUserCommentInfoListener implements RequestListener<UserCommentInfo> {
        @Override
        public void onResponse(UserCommentInfo result, boolean isCachedData) {
            if (null != result) {
                SarrsArrayList<CommentsInfo> commentsList = result.getComments();
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
     * 添加评论回调
     */
    private class RequsetAddCommentListener implements RequestListener<AddComment> {
        @Override
        public void onResponse(AddComment result, boolean isCachedData) {
            if (result != null && result.getState() == 1) {
                UIs.showToast(getString(R.string.comment_sucess));
                Log.d("comment", "commentSucess and addCommentInfo is " + result);
            } else {
                UIs.showToast(getString(R.string.comment_fail));
            }
        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.d("comment", "dataErr");
        }
    }
    /**
     *   半屏页请求参数
     * */
    String mAid;
    String mCid;
    String mGvid;
    String mSource;
    String mTitle;
    String mBucket;// 推荐
    String mReid;  // 搜索
    int mPn;
    String mPorder;//本地播放点击剧集order

    List<VideoItem> mVideoItems;

    void initParams(){

        if (mVideoDetailItem != null) {
            mVideoItems= mVideoDetailItem.getVideoItems();
            mGvid = mVideoDetailItem.getVideoItems().get(0).getGvid();
            mAid= mVideoDetailItem.getId();
            mCid=mVideoDetailItem.getCategory_id();
            mSource=mVideoDetailItem.getSource();
            mTitle=mVideoDetailItem.getTitle();
            mBucket=mVideoDetailItem.getBucket();
            mReid=mVideoDetailItem.getReid();

        }
    }

    /**
     * 执行请求半屏播放页Index 数据请求
     * TODO(以后有历史记录需要修改获取gvid)
     */
    VideoDetailIndex mIndex;
    //是否是单视频
    boolean isSingleVideo;
    public void requestVideoDetailIndex() {
        // 第三方跳入
        if(isJumpFromThird){
            // 单视频不走请求
            if(TextUtils.isEmpty(mAid)){
                LogUtil.e("xll","分享单视频不走详情");
                isSingleVideo=true;
                buildSingleVideoPlayData();
                LogUtil.e("xll","request videoIndex single video! back");
                return;
            }else{
                LogUtil.e("xll","分享专辑走详情");
                // 直接跳过去索引，直接去详情
                requestVideoDetailByMediaAuto();
            }
        }else{
            // 单视频不走请求
            if(TextUtils.isEmpty(mAid)){
                LogUtil.e("xll","非分享单视频不走索引、详情");
                isSingleVideo=true;
                buildSingleVideoPlayData();
                LogUtil.e("xll","request videoIndex single video! back");
                return;
            }else{
                // 正常进入专辑取索引然后去详情
                LogUtil.e("xll","非分享专辑走详情");
                isSingleVideo=false;
                HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG);
                HttpApi.
                        getVideoDetailIndexRequest(mAid, mGvid)
                        .start(new RequestVideoDetailIndexListener(), ConstantUtils.REQUEST_VIDEODETAIL_VIDEO_INDEX_TAG);
            }


        }


    }

    /**
     *   播放单视频
     * */
    void buildSingleVideoPlayData(){
        //
        isNeedSave=true;
        // TODO 单视频加播放记录
        currentPlay=new PlayData();
        if(activity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
            currentPlay.setIndex(0);
            currentPlay.setFrom(ConstantUtils.PLAYER_FROM_DETAIL);
            currentPlay.setKey(0);
            // TODO Porder
            currentPlay.setPorder("");
            currentPlay.setIsLocalVideo(true);
            currentPlay.setSource(mSource);
            currentPlay.setRecordposition(mPlayTime);
            currentPlay.setFrom(ConstantUtils.PLAYER_FROM_DETAIL);
            currentPlay.setmLocalDataLists(mVideoDetailItem.getLocalVideoEpisodes());
        }else{
            // online
            currentPlay.setIndex(0);
            currentPlay.setIsLocalVideo(false);
            currentPlay.setFrom(ConstantUtils.PLAYER_FROM_DETAIL);
            currentPlay.setKey(0);
            currentPlay.setSource(mSource);
            currentPlay.setRecordposition(mPlayTime);
            currentPlay.setmGvid(mGvid);
            ArrayList<VideoItem>items=new ArrayList<>();
            VideoItem item=new VideoItem();
            item.setGvid(mGvid);
            item.setSource(mSource);
            item.setTitle(mTitle);
            item.setCategory_id(mCid);
            item.setFromMainContentType(mCid);
            item.setKey(0);
            item.setIndex(0);
            items.add(item);
            fenyeList.append(0,items);
            currentPlay.setmEpisodes(fenyeList);

        }


        //TODO 添加单视频播放记录
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // event bus 延迟1000毫秒向顶部fragment发送数据
                EventBus.getDefault().post(currentPlay);
                hideRetyLayout();
                hideLoading();
                hideEpisoForSingleVideo();
                hideDetailDescription();
            }
        }, 1000);

    }


    void hideEpisoForSingleVideo(){
        // 单视频展示
        if (episoLayout != null) {
            episoLayout.setVisibility(View.GONE);
        }
        if (videodetail_bar2 != null) {
            videodetail_bar2.setVisibility(View.GONE);
        }
        showDropDes();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        ispause = true;
        super.onPause();
    }

    /**
     * @des 分页请求半屏折叠剧集Grid数据
     */
    private void requestVideoEpisoListForExpandGridByHandClick(VideoDetailItem item, VideoDetailIndex inItem, AdapterView view, VideoDetailBottomExpandListAdapter adpater) {

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
        RequestVideoDetailByHandClickListener requstListner = new RequestVideoDetailByHandClickListener();
        requstListner.setGridData(view, adpater, inItem.getPn());
        HttpApi.getVideoDetailRequest(item.getCategory_id(), item.getId(), inItem.getPn(), 0).start(requstListner, ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
    }

    /**
     * @des 刚进入半屏播放请求推荐剧集以及描述接口
     *      进入半屏页播放器自动联播使用
     */
    private void requestVideoDetailByMediaAuto() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
        RequestVideoDetailListener requstListner = new RequestVideoDetailListener();

        if(TextUtils.isEmpty(mAid)){
            LogUtil.e("xll", "request videodetail single video!");
            return;
        }
        LogUtil.e("xll", " request videodetail (mCid mAid,mPn) " + mCid + " " + mAid + " " + mPn);
        HttpApi.getVideoDetailRequest(mCid,mAid,mPn, 0).start(requstListner, ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
    }

    /**
     * @param result 请求详情返回结果
     *               返回结果是 单视频隐藏剧集
     */
    private void hideEpisoLayout(VideoDetailItem result, boolean isLocalResult) {
        //本地剧集展示
        if (isLocalResult) {
            if (episoLayout != null) {
                episoLayout.setVisibility(View.GONE);
            }
            if (videodetail_bar2 != null) {
                videodetail_bar2.setVisibility(View.GONE);
            }

            hideDetailDescription();
            return;
        }
        // 电影等专辑展示
        if (result != null && !TextUtils.isEmpty(result.getCategory_id())) {
            if (result.getCategory_id().equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_2))) {
                if (result.getVideoItems().size() <= 1) {
                    if (episoLayout != null) {
                        episoLayout.setVisibility(View.GONE);
                    }
                    if (videodetail_bar2 != null) {
                        videodetail_bar2.setVisibility(View.GONE);
                    }

                }
            }
        }

    }

    /**
     * 处理 播放器空回调
     */

    void handleNotifyResult(VideoPlayerNotifytData data, VideoDetailItem result) {

        //播放器有回调----- 本地播放
        if (data.isLocal()) {
            LogUtil.e(TAG, "execute local videodetail logic");

            return;
        }
        //播放器有回调----onLine
        if (data.getType() == ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK) {
            LogUtil.e("xll","send data 点击全屏tag key "+data.getReqKey());
            if (fenyeList.indexOfKey((data.getReqKey())) < 0 && result.getVideoItems() != null && result.getVideoItems().size() > 0) {
                fenyeList.append((data.getReqKey()), (ArrayList<VideoItem>) result.getVideoItems());
                fenyeList.get(data.getKey()).get(data.getPosition()).setIsPlay(true);
                LogUtil.e("xll", "receive from top (k,p,r) " + data.getKey() + " " + data.getPosition() + " " + data.getReqKey());
                currentPlay =new PlayData(fenyeList, data.getKey(), data.getPosition(), data.getReqKey(), ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK);
                currentPlay.setTagIndex(data.getReqKey());
                currentPlay.setTagIndex2(mCurrentTagIndex);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(currentPlay);
                    }
                }, 0);
            }
            // 解决播放页剧集展示问题
            // 自动联播
        } else if (mPageNum>data.getKey()+1&&fenyeList.indexOfKey((data.getKey() + 1)) < 0 && result.getVideoItems() != null && result.getVideoItems().size() > 0) {
            LogUtil.e("xll","send data 自动联播 "+data.getKey());
            fenyeList.append((data.getKey() + 1), (ArrayList<VideoItem>) result.getVideoItems());
            currentPlay = new PlayData(fenyeList, result.getPage_titles(), result.getCategory_id(), ConstantUtils.PLAYER_FROM_DETAIL);
            currentPlay.setKey(data.getKey());
            currentPlay.setIndex(data.getPosition());
            //设置自动联播key
            currentPlay.setTagIndex(data.getKey());
            currentPlay.setTagIndex2(data.getKey() + 1);

            LogUtil.e("xll ", "当前分页 底部接收播放器 key index " + data.getKey() + "  " + data.getPosition());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(currentPlay);
                }
            }, 0);
        }
    }








    /**
     *  当前总分页
     *  */
    int mPageNum;
    // 表示请求出详情可以执行按钮点击
    boolean isNeedSave;

    /**
     * @des   请求详情接口回调
     */
    private class RequestVideoDetailListener implements RequestListener<VideoDetailItem> {
        @Override
        public void onResponse(VideoDetailItem result, boolean isCachedData) {


            selfItem = result;
            if(activity!=null&&selfItem!=null){
                activity.setCurrentplayVideoDetailItem(selfItem);
            }
            hideRetyLayout();
            isNeedSave=true;
            hideLoading();
            showBttomFragment();
            detail = result;
            // 展示详情数据
            if (result == null) {
                return;
            }
            if (TextUtils.isEmpty(result.getPublisher()) && TextUtils.isEmpty(result.getActor()) && TextUtils.isEmpty(result.getDirector()) && TextUtils.isEmpty(result.getDescription())) {
                // 详情返回结果正常但是没有数据
                hideDetailDescription();
                hideEpisoLayout(null, true);
                LogUtil.e("xll ", "详情页 隐藏描述");
            } else {
                // 详情返回数据正常
                LogUtil.e("xll ","详情页 隐藏剧集");
                hideEpisoLayout(result, false);
            }
            // 从其他页面初次进来
            if (mediaNotifyData == null) {
                mediaNotifyData = new VideoPlayerNotifytData();
                //第几页
                mediaNotifyData.setKey(mPn);
                List<VideoItem> onLineItems=result.getVideoItems();

                fenyeList.append(mediaNotifyData.getKey(), activity.mergeList(localEpisodes,(ArrayList<VideoItem>)onLineItems));
                currentPlay = new PlayData(fenyeList, result.getPage_titles(), result.getCategory_id(), ConstantUtils.PLAYER_FROM_DETAIL);
                currentPlay.setKey(mPn);
                if(activity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                    currentPlay.setIsLocalVideo(true);
                    if(mVideoDetailItem!=null){
                        currentPlay.setmLocalDataLists(mVideoDetailItem.getLocalVideoEpisodes());
                    }
                }else{
                    currentPlay.setIsLocalVideo(false);
                }
                ArrayList<VideoItem> mList=(ArrayList<VideoItem>) result.getVideoItems();
                // 首页瀑布流order没有
                if(mVideoDetailItem!=null){
                    // porder is null 首页过来porder是空的
                    if(TextUtils.isEmpty(mVideoDetailItem.getVideoItems().get(0).getOrder())){
                        //综艺播放推荐那集
                        if(result!=null && ConstantUtils.VARIETY_CATEGORYID.equalsIgnoreCase(result.getCategory_id())) {
                            if(mList!=null){
                                for(int i=0;i<mList.size();i++){
                                    if(mList.get(i).getGvid().equalsIgnoreCase(mGvid)){
                                        // 设置当前播放index
                                        currentPlay.setIndex(i);
                                        currentPlay.setPorder(mList.get(i).getOrder());
                                        break;
                                    }
                                }
                            }
                        }else if(result!=null && ConstantUtils.DOCUMENTARY_CATEGORYID.equalsIgnoreCase(result.getCategory_id())){
                            if(mList!=null){
                                for(int i=0;i<mList.size();i++){
                                    if(mList.get(i).getGvid().equalsIgnoreCase(mGvid)){
                                        // 设置当前播放index
                                        currentPlay.setIndex(i);
                                        currentPlay.setPorder(mList.get(i).getOrder());
                                        break;
                                    }
                                }
                            }
//                             currentPlay.setIndex(mList.size()-1);
//                             currentPlay.setPorder(mList.get(mList.size()-1).getOrder());
                        }else if(result!=null && ConstantUtils.CARTOON_CATEGORYID.equalsIgnoreCase(result.getCategory_id())){
                            if(mList!=null){
                                for(int i=0;i<mList.size();i++){
                                    if(mList.get(i).getGvid().equalsIgnoreCase(mGvid)){
                                        // 设置当前播放index
                                        currentPlay.setIndex(i);
                                        currentPlay.setPorder(mList.get(i).getOrder());
                                        break;
                                    }
                                }
                            }
//                             currentPlay.setIndex(mList.size()-1);
//                             currentPlay.setPorder(mList.get(mList.size()-1).getOrder());
                        }else if(result!=null && ConstantUtils.TV_SERISE_CATEGORYID.equalsIgnoreCase(result.getCategory_id())){
                            if(mList!=null){
                                for(int i=0;i<mList.size();i++){
                                    if(mList.get(i).getGvid().equalsIgnoreCase(mGvid)){
                                        // 设置当前播放index
                                        currentPlay.setIndex(i);
                                        currentPlay.setPorder(mList.get(i).getOrder());
                                        break;
                                    }
                                }
                            }
//                             currentPlay.setIndex(mList.size()-1);
//                             currentPlay.setPorder(mList.get(mList.size()-1).getOrder());
                        }else{
                            // 设置(未完结)专辑需要播放器播放最新一集
                            if(result.getIs_end()!=null&&result.getIs_end().equalsIgnoreCase("0")){
                                // 记录片走专辑模式（默认最新一集）
                                if(result.getCategory_id().equalsIgnoreCase(ConstantUtils.DOCUMENTARY_CATEGORYID)){
                                    currentPlay.setIndex(0);
                                }else{
                                    currentPlay.setIndex(mList.size()-1);
                                }
                            }else{
                                //
                                currentPlay.setIndex(0);
                            }
                        }
                        // porder not null
                    }else{
                        //
                        if(result.getIs_end()!=null&&result.getIs_end().equalsIgnoreCase("0")){
                            // 设置未完结专辑需要播放器播放最新一集
                            if(result.getCategory_id().equalsIgnoreCase(ConstantUtils.VARIETY_CATEGORYID)||result.getCategory_id().equalsIgnoreCase(ConstantUtils.DOCUMENTARY_CATEGORYID)){
                                currentPlay.setIndex(0);
                            }else{
                                currentPlay.setIndex(mList.size()-1);
                            }
                        }else{
                            if(mList!=null){
                                for(int i=0;i<mList.size();i++){
                                    if(mList.get(i).getOrder().equalsIgnoreCase(mVideoDetailItem.getVideoItems().get(0).getOrder())){
                                        // 设置当前播放index
                                        currentPlay.setIndex(i);
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }else{
                    // 分享入口进入mVideoDetailItem空
                    if(result.getIs_end()!=null&&result.getIs_end().equalsIgnoreCase("0")){
                        // 设置未完结专辑需要播放器播放最新一集
                        if(result.getCategory_id().equalsIgnoreCase(ConstantUtils.VARIETY_CATEGORYID)||result.getCategory_id().equalsIgnoreCase(ConstantUtils.DOCUMENTARY_CATEGORYID)){
                            currentPlay.setIndex(0);
                        }else{
                            currentPlay.setIndex(mList.size()-1);
                        }

                    }else{
                        currentPlay.setIndex(0);
                    }
                }
                // 分享进来mVideoDetaiItem 是空
                // 给播放器发送数据
                currentPlay.setRecordposition(mPlayTime);
                // 当前播放索引
                if(mVideoDetailItem!=null&&!TextUtils.isEmpty(mVideoDetailItem.getVideoItems().get(0).getOrder())){
                    currentPlay.setPorder(mVideoDetailItem.getVideoItems().get(0).getOrder());
                }
                if(isRecord){
                    //有播放记录设置播放索引
                    if(mList!=null&&mList.size()>0){
                        for(int i=0;i<mList.size();i++){
                            if(mList.get(i).getGvid().equalsIgnoreCase(mGvid)){
                                // 设置当前播放index
                                currentPlay.setIndex(i);
                                break;
                            }
                        }
                    }

                }
                if(activity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                    if(mVideoDetailItem!=null){
                        //本地porder从下载页传入
                        currentPlay.setPorder(mVideoDetailItem.getPorder());
                        LogUtil.e("v1.1.2","from local porder is "+mVideoDetailItem.getPorder());
                    }
                }
                //设置
                mediaNotifyData.setPosition(currentPlay.getIndex());
                LogUtil.e("xll", " send data key" + currentPlay.getKey() + " index " + currentPlay.getIndex());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(currentPlay);
                    }
                }, 0);

                // modify
                LogUtil.e("xll","详情页 初次进入，取详情");

            } else {
                LogUtil.e("xll", "详情页 取详情，不是初次进入");
                //LogUtil.e("xll", " send data key" + currentPlay.getKey() + " index " + currentPlay.getIndex());

                handleNotifyResult(mediaNotifyData, result);
            }
            if(result.getPage_titles()!=null){
                mPageNum=result.getPage_titles().size();
            }
            showVideoDetail(selfItem, mediaNotifyData);
            if (tagAdapter == null) {
                if(result!=null){
                    if(result.getPage_titles()!=null){

                        tagAdapter = new VideoDetailBottomExpandListAdapter(result.getPage_titles(), getActivity(), fenyeList, result.getCategory_id());
                        mBottomScrollListView.setAdapter(tagAdapter);
                    }

                }
            } else {
                tagAdapter.setFenyeList(fenyeList);
                tagAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void dataErr(int errorCode) {
            hideLoading();
            hideBottomFragment();
            showRetryLayout();
            LogUtil.e(TAG, "" + errorCode);

        }

        @Override
        public void netErr(int errorCode) {
            hideLoading();
            hideBottomFragment();
            showRetryLayout();
            LogUtil.e(TAG, "" + errorCode);
        }
    }

    private void hideDetailDescription() {
        if (videodetail_scroll_layout != null) {
            videodetail_scroll_layout.setVisibility(View.GONE);
        }
        if (mDropDownBtn != null) {
            mDropDownBtn.setVisibility(View.GONE);
        }
    }
    /**
     *   发送底部剧集分页点击tag消息
     * */

    void sendBottomTagClick(int position){
        // send data
        currentPlay.setTagIndex(position);
        currentPlay.setFrom(ConstantUtils.PLAYER_FROM_BOTTOM_EPISO_TAG_CLICK);
        EventBus.getDefault().post(currentPlay);
    }


    /**
     * @des 请求半屏页剧集信息回调  自动联播调用
     */
    private class RequestVideoDetailByHandClickListener implements RequestListener<VideoDetailItem> {

        // 展示折叠Grid数据
        private AdapterView parentView;
        private VideoDetailBottomExpandListAdapter adapter;
        private int position;


        public void setGridData(AdapterView parent, VideoDetailBottomExpandListAdapter adpater, int position) {
            this.parentView = parent;
            this.adapter = adpater;
            this.position = position;
        }


        @Override
        public void onResponse(VideoDetailItem result, boolean isCachedData) {
            Log.i("searchPlay", "RequestVideoDetailByHandClickListener---->" + result.toString());
            if (fenyeList != null && fenyeList.indexOfKey(position) < 0) {

                fenyeList.append(position, activity.mergeList(localEpisodes,(ArrayList<VideoItem>)result.getVideoItems()));
                LogUtil.e("v1.1.2","merge next page local episode ");
                adapter.expandPosition(position, fenyeList);
                parentView.setSelected(true);
                parentView.setSelection(position);


            }


        }

        @Override
        public void dataErr(int errorCode) {

            LogUtil.e(TAG, "" + errorCode);

        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e(TAG, "" + errorCode);
        }
    }
    /**
     *   index 回调 非专辑类（maid 是 null）不走index接口
     * */

    private class RequestVideoDetailIndexListener implements RequestListener<VideoDetailIndex> {
        @Override
        public void onResponse(VideoDetailIndex result, boolean isCachedData) {
            if (mVideoDetailItem != null) {
                LogUtil.e(TAG, "" + result.toString());
                // 请求剧集详情
                //综艺节目
                mIndex=result;
                if(ConstantUtils.VARIETY_CATEGORYID.equals(mVideoDetailItem.getCategory_id()) || ConstantUtils.DOCUMENTARY_CATEGORYID.equals(mVideoDetailItem.getCategory_id())){
                    if(mIndex.getIndex() % 10 ==0){
                        mPn = mIndex.getIndex() / 10-1;
                    }else{
                        mPn = mIndex.getIndex() / 10;
                    }
                }else if(ConstantUtils.TV_SERISE_CATEGORYID.equals(mVideoDetailItem.getCategory_id()) || ConstantUtils.CARTOON_CATEGORYID.equals(mVideoDetailItem.getCategory_id())){
//                        mPn = (mIndex.getIndex() / 60);
                    if(mIndex.getIndex() % 60 ==0){
                        mPn = mIndex.getIndex() / 60-1;
                    }else{
                        mPn = mIndex.getIndex() / 60;
                    }
                }
                mIndex.setIndex(result.getIndex());
                mIndex.setPn(mPn);
                requestVideoDetailByMediaAuto();
            }


        }

        @Override
        public void dataErr(int errorCode) {
            hideLoading();
            hideBottomFragment();
            showRetryLayout();
            EventBus.getDefault().post(ChaoJiShiPinVideoDetailActivity.PLAY_DATA_ERR);
            LogUtil.e(TAG, "" + errorCode);
        }

        @Override
        public void netErr(int errorCode) {
            hideLoading();
            hideBottomFragment();
            showRetryLayout();
            EventBus.getDefault().post(ChaoJiShiPinVideoDetailActivity.PLAY_DATA_ERR);
            LogUtil.e(TAG, "" + errorCode);
        }
    }

    /**
     * 设置网络监听
     *
     * @param netName 网络类型
     */
    public void setNetStateTip(String netName, int netType, boolean isHasNetWork) {
        if (isHasNetWork) {
            //判断具体的网络类型

            //WIFI
            if (NetWorkUtils.isWifi()) {
                // requestVideoDetailIndex();
                return;
            }
            //流量
            else {
                if (((ChaoJiShiPinVideoDetailActivity)getActivity()).getMediaType() == ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.RPG_net_tip), Toast.LENGTH_SHORT).show();
                }
            }
            //没网络
        } else {
            //hideLoading();
            // showRetryLayout();
            // hideEpisoLayout(null,true);
            // hideDetailDescription();
           // Toast.makeText(mContext, mContext.getResources().getString(R.string.nonet_tip), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *   下载单视频逻辑
     * */

    void downSingleVideo(VideoDetailItem detailItem){
        //

        DownloadEvent event = new DownloadEvent();
        VideoDetailItem singleDetail=new VideoDetailItem();
        ArrayList<VideoItem> items=new ArrayList<>();
        VideoItem item=new VideoItem();
        item.setFromMainContentType(mCid);
        item.setCategory_id(mCid);
        item.setGvid(mGvid);
        if(detailItem!=null){
            item.setTitle(detailItem.getTitle());
            singleDetail.setTitle(detailItem.getTitle());
            singleDetail.setDescription(detailItem.getDescription());
            singleDetail.setSource(detailItem.getSource());
        }
        items.add(item);
        singleDetail.setVideoItems(items);
        singleDetail.setCategory_id(mCid);
        event.downloadFile(getActivity(), singleDetail, 0);

    }

    /**
     *   下载专辑
     * */
    void downloadVideos(){
        // 专辑如果是1集
        if(selfItem!=null) {
            if(selfItem.getEpiso_num()!=null&& "1".equals(selfItem.getEpiso_num())){
                DownloadEvent event = new DownloadEvent();
                VideoDetailItem singleDetail=new VideoDetailItem();
                ArrayList<VideoItem> items=new ArrayList<>();
                VideoItem item=new VideoItem();
                item.setFromMainContentType(mCid);
                item.setCategory_id(mCid);
                item.setGvid(mGvid);
                item.setId(mAid);
                item.setImg(selfItem.getImg());
                item.setDetailImage(selfItem.getDetailImage());
                if(mVideoDetailItem!=null) {
                    item.setTitle(mVideoDetailItem.getTitle());
                    singleDetail.setTitle(mVideoDetailItem.getTitle());
                    singleDetail.setDescription(mVideoDetailItem.getDescription());
                    singleDetail.setSource(mVideoDetailItem.getSource());
                }
                items.add(item);
                singleDetail.setVideoItems(items);
                singleDetail.setCategory_id(mCid);
                singleDetail.setImg(selfItem.getImg());
                singleDetail.setId(mAid);
                singleDetail.setDetailImage(selfItem.getDetailImage());
                event.downloadFile(getActivity(), singleDetail, 0);

            }else{
                Intent intent = new Intent(getActivity(), DownLoadListActivity.class);
                intent.putExtra("mediaNotifyData", mediaNotifyData);
                if (selfItem != null) {
                    if(mVideoDetailItem!=null) {
                        selfItem.setFromMainContentType(mVideoDetailItem.getFromMainContentType());
                    }
                    intent.putExtra("mVideoDetailItem", selfItem);
                } else {
                    if(mVideoDetailItem!=null) {
                        intent.putExtra("mVideoDetailItem", mVideoDetailItem);
                    }
                }
                startActivity(intent);
            }

        }


    }


    /**
     * 下载
     */
    private void download() {
        if(mVideoDetailItem!=null){
            if(TextUtils.isEmpty(mAid)){
                //如果是单视频
                downSingleVideo(mVideoDetailItem);
            } else {
                // 如果是电影等一个剧集的专辑需要停留本页
                downloadVideos();
                return;
            }
        }else
        {
            // 分享等进入半屏页 mVideoDetailItem是空
            if(TextUtils.isEmpty(mAid)){
                //如果是单视频//TODO 详情接口不支持aid 空，拿gvid取详情
                downSingleVideo(null);

            }else{
                downloadVideos();

            }
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
                                if(!TextUtils.isEmpty(local_list.get(i).getGvid())){
                                    if (local_list.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                                        if (local_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                            totallist.remove(netlist.get(j));
                                            totallist.add(local_list.get(i));
                                        }
                                        break;
                                    }
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


    private void  mergeDateServerandlocalDB(ArrayList<HistoryRecord> netlist){
        if(totallist!=null){
            totallist.clear();
        }
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
                        if(!TextUtils.isEmpty(local_list.get(i).getGvid())){
                            if (local_list.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                                if (local_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                    totallist.remove(netlist.get(j));
                                    totallist.add(local_list.get(i));
                                }else{
                                        totallist.remove(local_list.get(i));
                                        totallist.add(netlist.get(j));
                                }
                                break;
                            }
                        }
                        if(!TextUtils.isEmpty(local_list.get(i).getId())&&!TextUtils.isEmpty(netlist.get(j).getId())){
                            LogUtil.e("v1.1.2","merge record(x,y) " +i+"_"+j);
                            if (local_list.get(i).getId().equalsIgnoreCase(netlist.get(j).getId())) {
                                if (local_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                    totallist.remove(netlist.get(j));
                                    totallist.add(local_list.get(i));
                                    LogUtil.e("v1.1.2","merge records (r,a) remove net "+netlist.get(j).getTitle()+" add local "+local_list.get(i).getTitle());
                                }else{
                                    LogUtil.e("v1.1.2","merge records (r,a) remove local "+local_list.get(i).getTitle()+" add net "+netlist.get(j).getTitle());
                                    totallist.remove(local_list.get(i));
                                    totallist.add(netlist.get(j));
                                }
                                break;
                            }
                        }
                        if (j == netlist.size() - 1) {
                            totallist.add(local_list.get(i));
                        }
                    }
                }
            }
            LogUtil.e("v1.1.2","merge records finish  ");
            preparePlay();
        }
    }
    /**
     * TODO NULL
     */
    int mPlayTime;
    boolean isRecord;
    public void preparePlay() {
        if (totallist.size() > 0) {
            for (int i = 0; i < totallist.size(); i++) {
                //有播放记录
                if (totallist.get(i) != null) {
                    // 专辑类型
                    if (!TextUtils.isEmpty(totallist.get(i).getId())) {
                        // 区分单视频 专辑
                        // 在线 离线
                        // 在线
                        // TODO review 有没有用 ！=null判断为空情况 都改为TextUtil
                        //综艺 推荐优先
                        if(activity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE&&NetworkUtil.isNetworkAvailable(activity)){
                            // 从首页跳转，综艺类型专辑走推荐一集
                            if(!TextUtils.isEmpty(mCid)&&mCid.equalsIgnoreCase(ConstantUtils.VARIETY_CATEGORYID)){

                                if (totallist.get(i).getGvid().equals(mGvid)) {
                                    mAid=totallist.get(i).getId();
                                    mGvid=totallist.get(i).getGvid();
                                    isRecord=true;
                                    mPlayTime = 0;
                                    if(totallist.get(i).getPlay_time() != null){
                                        mPlayTime = Integer.parseInt(totallist.get(i).getPlay_time());
                                    }
                                    requestVideoDetailIndex();
                                    //保存播放的时长
                                    break;
                                }
                            }
                            // 非综艺播放记录优先
                            else if (totallist.get(i).getId().equals(mAid)) {
                                mAid=totallist.get(i).getId();
                                mGvid=totallist.get(i).getGvid();
                                isRecord=true;
                                mPlayTime = 0;
                                if(!TextUtils.isEmpty(totallist.get(i).getPlay_time())){
                                    mPlayTime = Integer.parseInt(totallist.get(i).getPlay_time());
                                }
                                LogUtil.e("xll ","online has records playTime "+mPlayTime);
                                requestVideoDetailIndex();
                                //保存播放的时长
                                break;
                            }
                        }else{
                            // 离线
                            if(!TextUtils.isEmpty(totallist.get(i).getGvid())){
                                if (totallist.get(i).getGvid().equals(mGvid)) {
                                    mAid=totallist.get(i).getId();
                                    mGvid=totallist.get(i).getGvid();
                                    if(totallist.get(i)!=null&&!TextUtils.isEmpty(totallist.get(i).getPlay_time())){
                                        mPlayTime = Integer.parseInt(totallist.get(i).getPlay_time().trim());
                                        LogUtil.e("xll ", "local has records playTime " + mPlayTime);
                                        LogUtil.e("v1.1.2 ", "local has records playTime " + mPlayTime);

                                    }
                                    isRecord=true;
                                    playNoRecordOrLocal();
                                    //保存播放的时长

                                    LogUtil.e("xll "," has record local ");
                                    break;
                                }
                            }

                        }

                    }else{
//                     aid为空  但视频需要判断gvid

                        // 区分单视频 专辑
                        // 在线 离线
                        // 在线
                        if(activity.getMediaType()== ChaoJiShiPinVideoDetailActivity.MeDiaType.ONLINE){
                            if (totallist.get(i).getGvid().equals(mGvid)) {
                                mAid=totallist.get(i).getId();
                                mGvid=totallist.get(i).getGvid();
                                isRecord=true;
                                mPlayTime = 0;
                                if(totallist.get(i).getPlay_time() != null){
                                    mPlayTime = Integer.parseInt(totallist.get(i).getPlay_time());
                                }
                                requestVideoDetailIndex();
                                //保存播放的时长
                                break;
                            }
                        }else{
                            // 离线
                            if(!TextUtils.isEmpty(totallist.get(i).getGvid())){
                                if (totallist.get(i).getGvid().equals(mGvid)) {
                                    mAid=totallist.get(i).getId();
                                    mGvid=totallist.get(i).getGvid();
                                    if(totallist.get(i)!=null&&!TextUtils.isEmpty(totallist.get(i).getPlay_time())){
                                        mPlayTime = Integer.parseInt(totallist.get(i).getPlay_time().trim());
                                        LogUtil.e("xll ", "local has records playTime " + mPlayTime);
                                    }
                                    isRecord=true;
                                    playNoRecordOrLocal();
                                    //保存播放的时长
                                    LogUtil.e("xll "," has record local ");
                                    break;
                                }
                            }

                        }


                    }
                }
                if (i == totallist.size() - 1) {
                    playNoRecordOrLocal();
                }
            }
        } else {
            playNoRecordOrLocal();
        }
    }


    /**
     *   没有播放记录 或者播放本地视频
     * */

    void playNoRecordOrLocal(){
        //无记录播放
        // 本地剧集请求详情
        LogUtil.e("xll ", "wifi " + NetWorkUtils.isWifi());
        if(NetWorkUtils.isNetAvailable()){
            LogUtil.e("xll ","wifi "+NetWorkUtils.isWifi());
            LogUtil.e("xll ","request index wifi "+NetWorkUtils.isWifi());
            requestVideoDetailIndex();
        }else{
            LogUtil.e("v1.1.2","no net play local from not download");
            currentPlay=new PlayData();
            currentPlay.setIsLocalVideo(true);
            int cuIndex=0;
            int cuKey=0;
            String porder=null;
            if(mVideoDetailItem!=null){

                ArrayList<LocalVideoEpisode> locals=activity.getLocalEpisoByFolderId(null,mGvid);
                PlayData temPlaydata=null;
                if(locals!=null){
                     temPlaydata=activity.mergeOnlyLocal(locals);
                }
                   // currentPlay.setPorder(mVideoDetailItem.getPorder());
                    if(!TextUtils.isEmpty(mGvid)&&temPlaydata!=null&&temPlaydata.getmEpisodes()!=null&&temPlaydata.getmEpisodes().size()>0){

                        for(int i=0;i<temPlaydata.getmEpisodes().size();i++){

                            List<VideoItem>temVideos=temPlaydata.getmEpisodes().get(i);

                             for(int j=0;j<temVideos.size();j++){
                                 if(mGvid.equalsIgnoreCase(temVideos.get(j).getGvid())){
                                     cuIndex=j;
                                     porder=temVideos.get(j).getPorder();
                                     cuKey=i;
                                     break;
                                 }
                             }
                        }
                    }


                currentPlay.setPorder(porder);
                //fenyeList=temPlaydata.getmEpisodes();
                if(temPlaydata!=null){
                    currentPlay.setPage_titles(temPlaydata.getPage_titles());
                }

                currentPlay.setmLocalDataLists(locals);

                currentPlay.setKey(cuKey);
                LogUtil.e("v1.1.2", "local play (k,v)= (" + cuKey + "," + cuIndex + ")");
                if(temPlaydata!=null){
                    if(temPlaydata.getmEpisodes()!=null){

                        if(!TextUtils.isEmpty(mCid)){
                            // 电视剧动漫排序
                            if(mCid.equalsIgnoreCase(ConstantUtils.CARTOON_CATEGORYID)||mCid.equalsIgnoreCase(ConstantUtils.TV_SERISE_CATEGORYID))
                            {
                                Collections.sort(temPlaydata.getmEpisodes().get(cuKey), new Comparator<VideoItem>() {
                                    @Override
                                    public int compare(VideoItem t1, VideoItem t2) {
                                        if(!TextUtils.isEmpty(t1.getPorder())&&!TextUtils.isEmpty(t2.getPorder())){
                                            return Integer.parseInt(t1.getPorder())-Integer.parseInt(t2.getPorder());

                                        }
                                        if(!TextUtils.isEmpty(t1.getOrder())&&!TextUtils.isEmpty(t2.getOrder())){
                                            return Integer.parseInt(t1.getOrder())-Integer.parseInt(t2.getOrder());

                                        }
                                        return 0;

                                    }
                                });
                            }
                        }
                        if(!TextUtils.isEmpty(porder)){
                            String morder=null;
                            for(int j=0;j<temPlaydata.getmEpisodes().get(cuKey).size();j++){
                                if(!TextUtils.isEmpty(temPlaydata.getmEpisodes().get(cuKey).get(j).getPorder())){
                                    morder=temPlaydata.getmEpisodes().get(cuKey).get(j).getPorder();
                                }
                                if(!TextUtils.isEmpty(temPlaydata.getmEpisodes().get(cuKey).get(j).getOrder())){
                                    morder=temPlaydata.getmEpisodes().get(cuKey).get(j).getOrder();
                                }
                                if(!TextUtils.isEmpty(morder)&&morder.equalsIgnoreCase(porder)){
                                    // 排序后设置index
                                    cuIndex=j;
                                     break;
                                }


                            }
                            currentPlay.setIndex(cuIndex);
                        }
                        currentPlay.setmEpisodes(temPlaydata.getmEpisodes());
                    }

                }

            }else{
                //TODO 确认默认剧集
                currentPlay.setPorder("0");
            }
            currentPlay.setSource(mSource);
            currentPlay.setCid(mCid);
            currentPlay.setRecordposition(mPlayTime);

            currentPlay.setFrom(ConstantUtils.PLAYER_FROM_DETAIL);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(currentPlay);
                    LogUtil.e("v1.1.2", "no net merge only local");
                }
            }, 0);
        }
    }



    /**
     * 分享
     */
    void share(VideoDetailItem videoItem, boolean horizontal) {
        ShareDataConfig config = new ShareDataConfig(getActivity());
        ShareData shareData=null;
        VideoItem item=null;
        if (videoItem instanceof VideoItem)
        {
            item = (VideoItem)videoItem;
//            shareData = config.configShareData(item.getGvid(), item.getTitle(), item.getImage(), ShareDataConfig.VIDEO_SHARE);
        }else {
            int index=0;
            if(activity.getMediaType()==ChaoJiShiPinVideoDetailActivity.MeDiaType.LOCAL){
                currentPlay= (PlayData)activity.getIntent().getSerializableExtra(Utils.PLAY_DATA);
                // item = videoItem.getVideoItems().get(currentPlay.getPorder());

            }else{
                if(currentPlay!=null){
                    index = currentPlay.getIndex();
                    if(videoItem.getVideoItems()!=null&&videoItem.getVideoItems().size()>0){
                        if(videoItem.getVideoItems().size()>index){
                            item = videoItem.getVideoItems().get(index);
                        }else{
                            item = videoItem.getVideoItems().get(0);
                        }

                    }

                }

            }
        }
        /**
         * 获取VideoDetailItem.getId，判断是否是专辑，单视频
         */
        String shareId = videoItem.getId();
        int shareType = ShareDataConfig.ALBULM_SHARE;
        if (shareId == null || shareId.length() == 0)
        {
            shareId = item.getGvid();
            shareType = ShareDataConfig.VIDEO_SHARE;
        }
        // DataReporter.reportAddShare(id, mSource, mCid, type, token, netType, ""+mBucket, ""+mReid);


        // 分享
        if(isJumpFromThird){
            shareData = config.configShareData(shareId, item.getTitle(), item.getImg(), shareType, mSource);
        }else{
            if(mVideoDetailItem!=null){
                shareData = config.configShareData(shareId, mVideoDetailItem.getTitle(), mVideoDetailItem.getDetailImage(), shareType, mSource);
            }
        }
        if (horizontal) {
            ShareDialog shareDialog = new ShareDialog(getActivity(), shareData, shareListener, ShareDialog.GRAVITY_RIGHT);
            shareDialog.show();
        } else {
            ShareDialog shareDialog = new ShareDialog(getActivity(), shareData, shareListener);
            shareDialog.show();
        }
    }

    ShareListener shareListener = new ShareListener() {
        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onCancel() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    EventBus.getDefault().post(ChaoJiShiPinVideoDetailActivity.SHARE_CANCEL);
                }
            }, 0);
        }
    };

    /**
     *  未登录状态下点击收藏，启动登录页返回已经收藏
     * */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ConstantUtils.SaveJumpTologin.VIDEOTAIL_SAVE_LOGIN){
            checkSave(false);
        }
    }
    private void addComment(Comment cmt) {
        ViewGroup floor = (ViewGroup) inflater.inflate(R.layout.comment_list_item, null);
        TextView floor_date = (TextView) floor.findViewById(R.id.floor_date);
        TextView floor_username = (TextView) floor.findViewById(R.id.floor_username);
        TextView floor_content = (TextView) floor.findViewById(R.id.floor_content);
//        floor_date.setText(DateFormatUtils.formatPretty(cmt.getDate()));
        floor_date.setText("11");
        floor_username.setText(cmt.getUserName());
        floor_content.setText(cmt.getContent());
        FloorView subFloors = (FloorView) floor.findViewById(R.id.sub_floors);
        if (cmt.getParentId() != Comment.NULL_PARENT) {
            SubComments cmts = new SubComments(addSubFloors(cmt.getParentId(), cmt.getFloorNum() - 1));
            subFloors.setComments(cmts);
            subFloors.setFactory(new SubFloorFactory());
            subFloors.setBoundDrawer(this.getResources().getDrawable(R.drawable.bound));
            subFloors.init();
        } else {
            subFloors.setVisibility(View.GONE);
        }
        container.addView(floor);
    }

    private List<Comment> addSubFloors(long parentId, int num) {
        if (num == 0) return null;
        Comment[] cmts;
        cmts = new Comment[num];
        for (Comment cmt : datas) {
            if (cmt.getId() == parentId) cmts[0] = cmt;
            if (cmt.getParentId() == parentId && cmt.getFloorNum() <= num)
                cmts[cmt.getFloorNum() - 1] = cmt;
        }
/*        }*/
        ArrayList<Comment> list = new ArrayList<Comment>();
        for (int i = 0; i < cmts.length; i++) {
            list.add(cmts[i]);
        }
        return list;
    }
    public void  reLoadData(){
        if (mVideoDetailItem != null && !isActivityonResumed) {
            //如果存在播放播放记录 修改playdata就好
            initParams();
            if (UserLoginState.getInstance().isLogin() && NetWorkUtils.isNetAvailable()) {
                ArrayList<HistoryRecord> netlist =  HistoryRecordManager.getHisToryRecordFromServer();
                mergeDateServerandlocalDB(netlist);
//                requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
            } else {
                //本地的播放记录
                totallist = local_list;
                preparePlay();
            }
        }
    }

    /**
     * 动态设置ListView的高度
     * @param listView
     * 注意事项  ListView Item 最外层的View 必须是LinearLayout
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
