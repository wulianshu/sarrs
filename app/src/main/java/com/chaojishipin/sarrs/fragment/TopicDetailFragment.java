package com.chaojishipin.sarrs.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinMainActivity;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinRegisterActivity;
import com.chaojishipin.sarrs.activity.PlayActivityFroWebView;
import com.chaojishipin.sarrs.adapter.TopicDetailListViewAdapter;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CancelFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.bean.TopicDetail;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.feedback.DataReportListener;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.manager.FavoriteManager;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.share.ShareDataConfig;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeListView;
import com.ibest.thirdparty.share.model.ShareData;
import com.ibest.thirdparty.share.view.ShareDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class TopicDetailFragment extends ChaoJiShiPinBaseFragment implements  PullToRefreshSwipeListView.OnSwipeListener, PullToRefreshSwipeListView.OnMenuItemClickListener,View.OnClickListener,
        AdapterView.OnItemClickListener,onRetryListener {
    public static final String pageid = "00S002003_1";
    private RelativeLayout titlebar;
    private ListView listView;
    private NetStateView mNetView;
    private TopicDetailListViewAdapter topicDetailListViewAdapter;
    Topic topic;
    Topic resulttopic;
    //ListView的头部
    private View headerview;
    private EqualRatioImageView equalRatioImageView;
    private ImageView img_back;
    private ImageView img_love;
    private ImageView img_share;
    private TextView tv_title;
    private TextView tv_sbutitle;
    private TextView item_subtitle;
    private View contentview;
    private String TAG="TopicDetailFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contentview = inflater.inflate(R.layout.fragment_topicdetail_layout, container,false);
        Bundle bundle = getArguments();

        topic = (Topic) bundle.getSerializable("topic");
        topicDetailListViewAdapter = new TopicDetailListViewAdapter(getActivity(), null);
        initView();
        initSaveOnLine();
        getNetData();

        return contentview;
    }
    private void initView() {
        headerview = View.inflate(getActivity(), R.layout.topicdetailheaderview, null);
        equalRatioImageView = (EqualRatioImageView) headerview.findViewById(R.id.main_frontview_poster);
        img_back = (ImageView) headerview.findViewById(R.id.topicdetail_top_back);
        img_love = (ImageView) headerview.findViewById(R.id.topicdetail_top_loving);
        img_share = (ImageView) headerview.findViewById(R.id.topicdetail_top_share);
        tv_title = (TextView) headerview.findViewById(R.id.main_frontview_poster_title);
        tv_sbutitle = (TextView) headerview.findViewById(R.id.main_frontview_poster_comment1);
        titlebar = (RelativeLayout) contentview.findViewById(R.id.mtitlebar);
        titlebar.setVisibility(View.GONE);
        img_back.setOnClickListener(this);
        img_love.setOnClickListener(this);
        img_share.setOnClickListener(this);
        mNetView = (NetStateView) contentview.findViewById(R.id.mainchannle_fragment_netview);
        mNetView.setOnRetryLisener(this);
        listView = (ListView) contentview.findViewById(R.id.topicdetailactivity_listview);
        listView.addHeaderView(headerview);
        listView.setAdapter(topicDetailListViewAdapter);
        listView.setOnItemClickListener(this);
        mNetView.setOnRetryLisener(this);
    }

    /**
     * 获取网络数据
     */
    public void getNetData() {

        if (NetWorkUtils.isNetAvailable()) {
            mNetView.setVisibility(View.GONE);
            requestTopicdetailData(getActivity(), topic.getTid());
        } else {
            listView.setVisibility(View.GONE);
            mNetView.setVisibility(View.VISIBLE);

        }
    }
    @Override
    protected void handleInfo(Message msg) {

    }
    /**
     *   构造 添加、是否存在、取消收藏统一参数
     * */
    String id="";
    String token= "";
    String type="";
    String cid="";
    String source="";
    String netType=NetWorkUtils.getNetInfo();
    void buidlParam(){
        token=UserLoginState.getInstance().getUserInfo().getToken();
        cid=ConstantUtils.TOPIC_CONTENT_TYPE;
        source=topic.getSource();
        type="4";
        if(topic!=null){
            id=topic.getTid();
        }
    }

/*
*  上报收藏
* */
    void doReport(){
        DataReporter.reportAddCollection(id, source, cid, type, token, netType);
    }

    /**
     *  进入专题页初始化收藏状态
     * */
    void initSaveOnLine(){
        // 单视频
        buidlParam();

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id,token,type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0&&result.isExists()) {
                    img_love.setImageResource(R.drawable.vedio_detail_loving_pressed);
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
     *  cancel save
     * */
    void cancelSaveOnLine(){
        buidlParam();
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_CANCEL_FAVORITE);
        HttpApi.cancelFavorite(id, token, type).start(new RequestListener<CancelFavorite>() {
            @Override
            public void onResponse(CancelFavorite result, boolean isCachedData) {
                if(result!=null&&result.getCode()==0){
                    ToastUtil.showShortToast(getActivity(),getString(R.string.save_cancel));
                    img_love.setImageResource(R.drawable.sarrs_pic_topicdetailactivity_loving_normal);
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
     *  save onLine
     * */
    void doSaveOnLine(){
        buidlParam();
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ADD_FAVORITE);
        HttpApi.addFavorite(id, token, type, cid, netType,source, "", "").start(new RequestListener<AddFavorite>() {
            @Override
            public void onResponse(AddFavorite result, boolean isCachedData) {
                if(result!=null&&result.getCode()==0){
                    img_love.setImageResource(R.drawable.vedio_detail_loving_pressed);
                    ToastUtil.showShortToast(getActivity(), getString(R.string.save_success));
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
     *   点击收藏按钮
     * */
    void checkSave(final boolean isByClick){
        buidlParam();
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
                    // 收藏上报
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topicdetail_top_back:
                if (AllActivityManager.getInstance().isExistActivy("ChaoJiShiPinMainActivity")) {
                    this.getActivity().finish();
                }else {
                    Intent intent = new Intent(getActivity(), ChaoJiShiPinMainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.topicdetail_top_loving:
                if(!UserLoginState.getInstance().isLogin()){
                    startActivityForResult(new Intent(getActivity(), ChaojishipinRegisterActivity.class),ConstantUtils.SaveJumpTologin.SPECIAL_LOGIN);
                }else{
                    if(topic!=null){
                        checkSave(true);
                    }else{
                        LogUtil.e(TAG,"topic is null ");
                    }
                }

               // Toast.makeText(getActivity(), getResources().getString(R.string.collection), Toast.LENGTH_SHORT).show();
                break;
            case R.id.topicdetail_top_share:
//                Toast.makeText(getActivity(), getResources().getString(R.string.share), Toast.LENGTH_SHORT).show();
                share();
                // 上报需先设置参数
                buidlParam();
                // 分享上报
                DataReporter.reportAddShare(id, "", cid, type, token, netType, "", "");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i>0) {
            Intent intent = new Intent(getActivity(), ChaoJiShiPinVideoDetailActivity.class);
            TopicDetail item = (TopicDetail) topicDetailListViewAdapter.getItem(i - 1);
            UploadStat.uploadstat(item,"0","00S002003_1","00S002003",(i-1)+"",topic.getTid(),"-","-","-","-");
            List<VideoItem> videoItems = item.getVideos();
            VideoDetailItem videoDetailItem = new VideoDetailItem();
            videoDetailItem.setTitle(item.getTitle());
            LogUtil.e("xll", "source rankList " + item.getSource());
            videoDetailItem.setSource(item.getSource());
            videoDetailItem.setDescription(item.getDescription());
            videoDetailItem.setId(item.getGaid());
            videoDetailItem.setCategory_id(item.getCategory_id() + "");
            videoDetailItem.setPlay_count(item.getPlay_count());
            videoDetailItem.setVideoItems(item.getVideos());
            videoDetailItem.setFromMainContentType(ConstantUtils.TOPIC_CONTENT_TYPE);
            videoDetailItem.setDetailImage(item.getImage());
            if("0".equals(ChaoJiShiPinMainActivity.isCheck) || "0".equals(ChaoJiShiPinMainActivity.lasttimeCheck)) {
                PlayData playData = null;
                if (videoItems != null && videoItems.size() > 0) {
                    playData = new PlayData(videoItems.get(0).getTitle(), videoItems.get(0).getGvid(), ConstantUtils.PLAYER_FROM_SPECAIL, item.getSource());
                }
                intent.putExtra("playData", playData);
                intent.putExtra("ref", pageid);
                intent.putExtra("videoDetailItem", videoDetailItem);
                startActivity(intent);
            }else{
                Intent webintent = new Intent(getActivity(),PlayActivityFroWebView.class);
                webintent.putExtra("url", item.getVideos().get(0).getPlay_url());
                webintent.putExtra("title", item.getVideos().get(0).getTitle());
                webintent.putExtra("site", item.getSource());
                webintent.putExtra("videoDetailItem", videoDetailItem);
                startActivity(webintent);
            }
        }
    }

    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {

    }

    @Override
    public void onSwipeStart(int position) {

    }

    @Override
    public void onSwipeEnd(int position) {

    }

    @Override
    public void onRetry() {
        requestTopicdetailData(getActivity(), topic.getTid());
    }
    /**
     * 根据tid获取专题的详情
     *
     * @param context
     * @param tid
     */
    private void requestTopicdetailData(Context context, String tid) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_TOPIC_DETAIL);
        HttpApi.
                getTopicDetailRequest(context, tid)
                .start(new RequestTopicDetailListener(), ConstantUtils.REQUEST_TOPIC_DETAIL);
    }

    private class RequestTopicDetailListener implements RequestListener<Topic> {

        @Override
        public void onResponse(Topic result, boolean isCachedData) {
            //进行展现的相关操作
            resulttopic = result;
            ImageLoader.getInstance().displayImage(resulttopic.getImage(), equalRatioImageView);
            tv_title.setText(resulttopic.getTitle());
            tv_sbutitle.setLines(2);
            tv_sbutitle.setText(resulttopic.getDescription());

            if (null != result  ) {
                topicDetailListViewAdapter.setmDatas(result.getItems());
                topicDetailListViewAdapter.notifyDataSetChanged();
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
     * 分享
     */
    void share()
    {
        ShareDataConfig config = new ShareDataConfig(getActivity());
        ShareData shareData = config.configShareData(topic.getTid(),
                resulttopic.getTitle(),
                resulttopic.getImage(),
                ShareDataConfig.TOPIC_SHARE,
                null);
        ShareDialog shareDialog = new ShareDialog(getActivity(), shareData, null);
        shareDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== ConstantUtils.SaveJumpTologin.SPECIAL_LOGIN){
            checkSave(false);
        }



    }
}
