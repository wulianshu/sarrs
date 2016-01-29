package com.chaojishipin.sarrs.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.RankListDetailListViewAdapter;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.RankListDetail;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.share.ShareDataConfig;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.UpgradeHelper;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeListView;
import com.ibest.thirdparty.share.model.ShareData;
import com.ibest.thirdparty.share.presenter.ShareManager;
import com.ibest.thirdparty.share.view.ShareDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class ChaoJiShiPinRankListDetailActivity extends ChaoJiShiPinBaseActivity implements PullToRefreshSwipeListView.OnSwipeListener, PullToRefreshSwipeListView.OnMenuItemClickListener, View.OnClickListener,
        AdapterView.OnItemClickListener, onRetryListener {
    public final String pageid = "00S002002_1";
    //ListView的头部
    private View headerview;
    //头部的布局的具体控件
    private EqualRatioImageView equalRatioImageView;
    private TextView tv_rank;
    private TextView play_count;
    private TextView tv_title;
    private TextView tv_description;
    //Listview
    private ListView listView;
    private NetStateView netStateView;
    //头部TitleBar
    private RelativeLayout baseactivity_titlebar;
    private ImageView imageView_back;
    private ImageView imageView_share;
    private TextView tv_titlebar;
    //适配器
    private RankListDetailListViewAdapter rankListDetailListViewAdapter;
    //排行榜
    private RankList rankList;
    //第一条
    RankListDetail firstrankListDetail;
    //分享返回数据
    private ArrayList<String> shareParams;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_topicdetail_layout);
        setTitleBarVisibile(false);
        rankList = (RankList) this.getIntent().getSerializableExtra("rankList");
        if (rankList == null)
        {
            shareParams = ShareDataConfig.jumpFromShare(this);
        }

        headerview = View.inflate(this, R.layout.topic_listview_item, null);
        equalRatioImageView = (EqualRatioImageView) headerview.findViewById(R.id.main_frontview_poster);
        tv_rank = (TextView) headerview.findViewById(R.id.tv_rank);
        play_count = (TextView) headerview.findViewById(R.id.tv_play_count);
        tv_title = (TextView) headerview.findViewById(R.id.main_frontview_poster_title);
        tv_description = (TextView) headerview.findViewById(R.id.main_frontview_poster_comment1);


        listView = (ListView) this.findViewById(R.id.topicdetailactivity_listview);
        imageView_back = (ImageView) this.findViewById(R.id.baseactivity_left_btn);
        imageView_share = (ImageView) this.findViewById(R.id.baseactivity_right_btn);
        tv_titlebar = (TextView) this.findViewById(R.id.baseactivity_title);
        netStateView = (NetStateView) this.findViewById(R.id.mainchannle_fragment_netview);
        baseactivity_titlebar = (RelativeLayout) this.findViewById(R.id.mtitlebar);
        rankListDetailListViewAdapter = new RankListDetailListViewAdapter(this, null);
        netStateView.setOnRetryLisener(this);
        listView.addHeaderView(headerview);
        listView.setAdapter(rankListDetailListViewAdapter);

        listView.setOnItemClickListener(this);
        imageView_share.setImageResource(R.drawable.selector_ranklistdetailtitlebar_share);
        imageView_back.setImageResource(R.drawable.selector_ranklistdetail_titlebar);
        imageView_back.setOnClickListener(this);
        imageView_share.setOnClickListener(this);
        getNetData();
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //新浪授权
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        ShareManager.authorCallback(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // return true;//返回真表示返回键被屏蔽掉

            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取网络数据
     */
    public void getNetData() {
        if (NetWorkUtils.isNetAvailable()) {
            netStateView.setVisibility(View.GONE);
            requestRankListdetailData(getRankId());
        } else {
            baseactivity_titlebar.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            netStateView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    /**
     * 构造 添加、是否存在、取消收藏统一参数
     */
    String id = "";
    String token = UserLoginState.getInstance().getUserInfo().getToken();
    String type = "";
    String cid = "";
    String netType = NetWorkUtils.getNetInfo();

    void buidlParam() {
        cid = ConstantUtils.RANKLIST_CONTENT_TYPE;
        type = "5";
        if (rankList != null) {
            id = getRankId();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chao_ji_shi_pin_topic_detail, menu);
        return true;
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.baseactivity_right_btn:
//              Toast.makeText(ChaoJiShiPinRankListDetailActivity.this, getResources().getString(R.string.share), Toast.LENGTH_SHORT).show();
                share();
                // 上报需先设置参数
                buidlParam();
                // 分享上报
                DataReporter.reportAddShare(id, "", cid, type, token, netType, "", "");
                break;
            case R.id.baseactivity_left_btn:
                back();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ChaoJiShiPinVideoDetailActivity.class);
        PlayData playData = null;
        RankListDetail arankListDetail = null;
        if (i == 0 && firstrankListDetail != null) {
            arankListDetail = firstrankListDetail;
        } else {
            arankListDetail = (RankListDetail) rankListDetailListViewAdapter.getItem(i - 1);
        }
        UploadStat.uploadstat(arankListDetail, "0", "00S002002_1", "00S002002", i + "", getRankId(), "-", "-", "-","-");

        VideoDetailItem videoDetailItem = new VideoDetailItem();
        videoDetailItem.setTitle(arankListDetail.getTitle());
        LogUtil.e("xll", "source rankList " + arankListDetail.getSource());
        videoDetailItem.setSource(arankListDetail.getSource());
        videoDetailItem.setDescription(arankListDetail.getDescription());
        videoDetailItem.setId(arankListDetail.getGaid());
        videoDetailItem.setCategory_id(arankListDetail.getCategory_id());
        videoDetailItem.setPlay_count(arankListDetail.getPlay_count());
        videoDetailItem.setVideoItems(arankListDetail.getVideos());
        videoDetailItem.setFromMainContentType(ConstantUtils.RANKLIST_CONTENT_TYPE);
        videoDetailItem.setDetailImage(arankListDetail.getImage());
        if("0".equals(ChaoJiShiPinMainActivity.isCheck)|| "0".equals(ChaoJiShiPinMainActivity.lasttimeCheck)) {
            //播放
            //Log.e("RankListDetailActivity",arankListDetail.getTitle()+"##"+arankListDetail.getGaid()+"##"+arankListDetail.getSource());
            playData = new PlayData(arankListDetail.getTitle(), arankListDetail.getVideos().get(0).getGvid(), ConstantUtils.PLAYER_FROM_RANKLIST, arankListDetail.getSource());
            intent.putExtra("ref", pageid);
            intent.putExtra("playData", playData);
            intent.putExtra("videoDetailItem", videoDetailItem);
            startActivity(intent);
        }else{
            //提神状态  视频要在webview里面播放并且需要有播放记录  记录时间为0

            Intent webintent = new Intent(this, PlayActivityFroWebView.class);
            webintent.putExtra("url", arankListDetail.getVideos().get(0).getPlay_url());
            webintent.putExtra("title", arankListDetail.getVideos().get(0).getTitle());
            webintent.putExtra("site", arankListDetail.getSource());
            webintent.putExtra("videoDetailItem", videoDetailItem);
            startActivity(webintent);
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
        getNetData();
    }

    /**
     * 根据tid获取专题的详情
     *
     * @param rid
     */
    private void requestRankListdetailData(String rid) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_RANKLIST_DETAIL);
        HttpApi.
                getRankListDetailRequest(rid)
                .start(new RequestRankListDetailListener(), ConstantUtils.REQUEST_RANKLIST_DETAIL);
    }

    private class RequestRankListDetailListener implements RequestListener<RankList> {

        @Override
        public void onResponse(RankList result, boolean isCachedData) {
            //进行展现的相关操作
            title = result.getTitle();
            tv_titlebar.setText(result.getTitle());
            if (null != result && result.getItems().size() > 0) {
                firstrankListDetail = (RankListDetail) result.getItems().get(0);
                play_count.setText(firstrankListDetail.getPlay_count() + "");
                tv_rank.setVisibility(View.VISIBLE);
                tv_rank.setText("1");
                tv_title.setText(firstrankListDetail.getTitle());
                tv_description.setText(firstrankListDetail.getDescription());
                ImageLoader.getInstance().displayImage(firstrankListDetail.getImage(), equalRatioImageView);
                result.getItems().remove(0);
                if (result.getItems().size() > 0) {
                    rankListDetailListViewAdapter.setmDatas(result.getItems());
                    rankListDetailListViewAdapter.notifyDataSetChanged();

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

    private String getRankId()
    {
        if (rankList != null)
            return rankList.getRid();
        if (shareParams != null)
            return shareParams.get(0);
        return "";
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

    /**
     * 分享
     */
    private void share() {
        ShareDataConfig config = new ShareDataConfig(this);
        ShareData shareData = config.configShareData(getRankId(),
                title,
                firstrankListDetail.getImage(),
                ShareDataConfig.RANKING_SHARE,
                null);
        ShareDialog shareDialog = new ShareDialog(this, shareData, null);
        shareDialog.show();
    }

    private void back()
    {
        if (AllActivityManager.getInstance().isExistActivy("ChaoJiShiPinMainActivity")) {
            this.finish();
        }else {
            Intent intent = new Intent(ChaoJiShiPinRankListDetailActivity.this, ChaoJiShiPinMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        UmengPagePath.beginpage(ConstantUtils.AND_RANK_DETAIL,this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        UmengPagePath.endpage(ConstantUtils.AND_RANK_DETAIL,this);
        super.onPause();
    }
}
