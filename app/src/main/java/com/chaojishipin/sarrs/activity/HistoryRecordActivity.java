package com.chaojishipin.sarrs.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.HistoryRecordListViewAdapter;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener;
import com.chaojishipin.sarrs.manager.HistoryRecordManager;
import com.chaojishipin.sarrs.thirdparty.LoginUtils;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeMenuListView;
import com.chaojishipin.sarrs.widget.SarrsMainMenuView;
import com.chaojishipin.sarrs.widget.TitleActionBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HistoryRecordActivity extends ChaoJiShiPinBaseActivity implements TitleActionBar.onActionBarClickListener, View.OnClickListener, AdapterView.OnItemClickListener, SarrsMainMenuView.onSlideMenuItemClick {
    private static final String TAG = "HistoryRecordActivity";
    public final String pageid = "00S0020015";
    private PullToRefreshSwipeMenuListView xListView;
    private HistoryRecordListViewAdapter adapter;
    private ImageView iv_download_back;
    private ArrayList<HistoryRecord> totallist;
    private ArrayList<HistoryRecord> records_list;
    private TextView tv_download_edit;
    private RelativeLayout relativeLayout;
    private TextView tv_select_all;
    private TextView tv_delete;
    private RelativeLayout no_data_bg;
    private TextView mNo_data_tip;
    private Button mLogin;
    HistoryRecordDao historyRecordDao = new HistoryRecordDao(this);
    private boolean isfirst = true;
    //左上按钮状态:true edit, false:complete
    private boolean editable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SarrsMainMenuView.listviewItemHeight = 84;
        SarrsMainMenuView.mode = ConstantUtils.SarrsMenuInitMode.MODE_DELETE;
        setTitleBarVisibile(false);
        initView();
        initPullToReshMode();
        setListener();
        adapter = new HistoryRecordListViewAdapter(this, null);
        xListView.setAdapter(adapter);
        xListView.setSwipeable(true);
        xListView.setSwipeMenuStatusListener(swipeMenuStatusListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegistInfo();
    }

    private void initView() {
        iv_download_back = (ImageView) this.findViewById(R.id.iv_download_back);
        xListView = (PullToRefreshSwipeMenuListView) findViewById(R.id.history_record_listview);
        tv_download_edit = (TextView) findViewById(R.id.tv_download_edit);
        relativeLayout = (RelativeLayout) findViewById(R.id.bottomlayout);
        tv_select_all = (TextView) findViewById(R.id.all_select);
        no_data_bg = (RelativeLayout) findViewById(R.id.no_data_bg);
        tv_delete = (TextView) findViewById(R.id.confirm_delete);
        relativeLayout.setVisibility(View.GONE);
        mNo_data_tip = (TextView) findViewById(R.id.tip2);
        mLogin = (Button) findViewById(R.id.instant_login);
    }

    /**
     * 初始化 pull to refresh mode
     */
    void initPullToReshMode() {
        xListView.setMode(PullToRefreshSwipeMenuListView.Mode.PULL_FROM_START);


        xListView.getLoadingLayoutProxy(true, false).setPullLabel(this.getString(R.string.pull_to_refresh_pull_label));
        xListView.getLoadingLayoutProxy(true, false).setRefreshingLabel(this.getString(R.string.pull_to_refresh_refreshing_label));
        xListView.getLoadingLayoutProxy(true, false).setReleaseLabel(this.getString(R.string.pull_to_refresh_release_label));
    }

    private void setListener() {
        tv_download_edit.setOnClickListener(this);
        iv_download_back.setOnClickListener(this);
        tv_select_all.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        xListView.setOnItemClickListener(this);
        xListView.setOnMenuItemClick(this);
        xListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isfirst) {
            getData();
            isfirst = false;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }, 800);
        }
    }

    public void getData() {

        records_list = historyRecordDao.getAll();
        if (UserLoginState.getInstance().isLogin() && NetWorkUtils.isNetAvailable()) {
//         if (NetWorkUtils.isNetAvailable()) {
            //获取服务器的播放记录列表   并且合并数据
           requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
//            ArrayList<HistoryRecord> netlist = new HistoryRecordManager().getHisToryRecordFromServer();
//            showData(netlist);
        } else {
            totallist = new ArrayList<HistoryRecord>();
            totallist = records_list;
            iniShowTimeList();
            adapter.setmDatas(totallist);
            adapter.notifyDataSetChanged();
            xListView.getRefreshableView().setSelection(1);
            reflashView();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    xListView.onRefreshComplete();
                }
            }, 1000);
            reflashView();
        }

    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.activity_history_record_layout, null);
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onTitleLeftClick(View v) {
    }


    @Override
    public void onTitleDoubleTap() {

    }

    @Override
    public void onTitleRightClick(View v) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.instant_login:
                startActivity(new Intent(this, ChaojishipinRegisterActivity.class));
                break;
            case R.id.iv_download_back:
                this.finish();
                break;
            case R.id.tv_download_edit:
                if (!editable) {
                    xListView.closeMenu();
                    return;
                }
                adapter.setEditable(!adapter.isEditable());
                if (adapter.isEditable()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    tv_download_edit.setText(getString(R.string.complete));
                    xListView.setSwipeable(false);
                    xListView.setMode(PullToRefreshSwipeMenuListView.Mode.DISABLED);
                    tv_select_all.setText(getResources().getString(R.string.check_all));
                    tv_delete.setClickable(false);
                    tv_delete.setTextColor(getResources().getColor(R.color.all_select));
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    tv_download_edit.setText(getString(R.string.edit));
                    xListView.setSwipeable(true);
                    adapter.selectcount = 0;
                    initPullToReshMode();
                    if(totallist!=null) {
                        for (int i = 0; i < totallist.size(); i++) {
                            totallist.get(i).setIsCheck(false);
                        }
                    }
                }
                synchronized (adapter) {
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.all_select:
                //全选
                if (adapter.selectcount < totallist.size()) {

                    adapter.selectcount = totallist.size();
                    for (int i = 0; i < totallist.size(); i++) {
                        totallist.get(i).setIsCheck(true);
                    }
                    tv_delete.setClickable(true);
                    tv_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
                    tv_select_all.setText(getResources().getString(R.string.deselect_all));
                    //取消全选
                } else {
                    adapter.selectcount = 0;
                    for (int i = 0; i < totallist.size(); i++) {
                        totallist.get(i).setIsCheck(false);
                    }
                    tv_select_all.setText(getResources().getString(R.string.check_all));
                    tv_delete.setClickable(false);
                    tv_delete.setTextColor(getResources().getColor(R.color.all_select));
                }
                adapter.notifyDataSetChanged();
                break;

            case R.id.confirm_delete:
                if(UserLoginState.getInstance().isLogin() && !NetWorkUtils.isNetAvailable()){
                    ToastUtil.showShortToast(this, getResources().getString(R.string.no_net_no_delete));
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
                builder.setTitle(getResources().getString(R.string.tip)); //设置标题
                builder.setMessage(getResources().getString(R.string.confir_delete)); //设置内容
                builder.setIcon(R.drawable.app_icon);//设置图标，图片id即可
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //关闭dialog
                        ArrayList<HistoryRecord> deletelist = new ArrayList<HistoryRecord>();
                        ArrayList<UploadRecord> uploadlist = new ArrayList<UploadRecord>();
                        List<Boolean> deleteisshowtimelist = new ArrayList<Boolean>();
                        for (int i = 0; i < totallist.size(); i++) {
                            if (totallist.get(i).isCheck()) {
                                deletelist.add(totallist.get(i));
                                if (i < adapter.isshowtimelist.size() - 1 && (!Boolean.valueOf("" + adapter.isshowtimelist.get(i + 1)))) {
                                    adapter.isshowtimelist.set(i + 1, true);
                                }
                                deleteisshowtimelist.add((Boolean) adapter.isshowtimelist.get(i));
                            }
                        }
                        totallist.removeAll(deletelist);
                        HistoryRecordManager.setHisToryRecordFromServer(totallist);
                        iniShowTimeList();
                        adapter.setEditable(false);
                        adapter.notifyDataSetChanged();

                        //删除本地
                        for (HistoryRecord arecord : deletelist) {
                            UploadRecord aupload = new UploadRecord();
                            aupload.setUpdateTime(Long.parseLong(arecord.getTimestamp()));
                            aupload.setVid(arecord.getGvid());
                            aupload.setSource(arecord.getSource());
                            // TODO 吴联暑再检查下 类似问题
                            if (!TextUtils.isEmpty(arecord.getPlay_time())) {
                                aupload.setPlayTime(Integer.parseInt(arecord.getPlay_time()));
                            }

                            aupload.setAction(1);
                            if (!TextUtils.isEmpty(arecord.getCategory_id())) {
                                aupload.setCid(Integer.parseInt(arecord.getCategory_id()));
                            }

                            aupload.setDurationTime(aupload.getDurationTime());
                            aupload.setPid(aupload.getPid());
                            uploadlist.add(aupload);
                            historyRecordDao.delete(arecord.getId());
                        }

                        //删除服务器
                        if (UserLoginState.getInstance().isLogin()) {
                            String json = JsonUtil.toJSONString(uploadlist);
                            uploadHistoryRecord(UserLoginState.getInstance().getUserInfo().getToken(), json);
                        }
                        relativeLayout.setVisibility(View.GONE);
                        adapter.selectcount = 0;
                        reflashView();
                        xListView.setSwipeable(true);
                        tv_download_edit.setText(getString(R.string.edit));

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                      Toast.makeText(MainActivity.this, "取消" + which, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
                break;
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i > 0 && totallist != null && totallist.size() > 0) {
            if (adapter.isEditable()) {
                totallist.get(i - 1).setIsCheck(!totallist.get(i - 1).isCheck());
                if (totallist.get(i - 1).isCheck()) {
                    adapter.selectcount++;
                } else {
                    adapter.selectcount--;
                }

                if (adapter.selectcount == totallist.size()) {
                    tv_select_all.setText(getResources().getString(R.string.deselect_all));
                } else {
                    tv_select_all.setText(getResources().getString(R.string.check_all));
                }
                if (adapter.selectcount > 0) {
                    tv_delete.setClickable(true);
                    tv_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
                } else {
                    tv_delete.setClickable(false);
                    tv_delete.setTextColor(getResources().getColor(R.color.all_select));
                }
                HistoryRecordListViewAdapter.ViewHolder holder = (HistoryRecordListViewAdapter.ViewHolder) view.getTag();
                if (totallist.get(i - 1).isCheck()) {
                    holder.toggleButton.setBackgroundResource(R.drawable.radiobutton_red_bg);
                } else {
                    holder.toggleButton.setBackgroundResource(R.drawable.radiobutton_white_bg);
                }
//                    adapter.notifyDataSetChanged();
            } else {
                HistoryRecord item = totallist.get(i - 1);
                Intent intent = new Intent(this, ChaoJiShiPinVideoDetailActivity.class);
                List<VideoItem> videoitems = new ArrayList<VideoItem>();
                VideoItem aitem = new VideoItem();
                aitem.setGvid(item.getGvid());
                //TODO 播放记录source字段是空  add by xll
                aitem.setSource(item.getSource());
                videoitems.add(aitem);
                VideoDetailItem videoDetailItem = new VideoDetailItem();
                videoDetailItem.setTitle(item.getTitle());
                videoDetailItem.setId(item.getId());
                videoDetailItem.setCategory_id(item.getCategory_id());
                videoDetailItem.setVideoItems(videoitems);
                // 视频来源
                videoDetailItem.setSource(item.getSource());
                videoDetailItem.setFromMainContentType(item.getContent_type());
                videoDetailItem.setDetailImage(item.getImage());
                if("0".equals(ChaoJiShiPinMainActivity.isCheck)) {
                    intent.putExtra("ref", pageid);
                    intent.putExtra("videoDetailItem", videoDetailItem);
                    startActivity(intent);
                }else{
                    Intent webintent = new Intent(this,PlayActivityFroWebView.class);
                    webintent.putExtra("url", item.getUrl());
                    webintent.putExtra("title", videoDetailItem.getVideoItems().get(0).getTitle());
                    webintent.putExtra("site", videoDetailItem.getSource());
                    webintent.putExtra("videoDetailItem", videoDetailItem);
                    startActivity(webintent);
                }
            }
        }

    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
    }

    @Override
    public void onItemClick(int position, View view, int parentId, ListAdapter myAdapter) {


        if(UserLoginState.getInstance().isLogin() && !NetWorkUtils.isNetAvailable()){
            ToastUtil.showShortToast(this, getResources().getString(R.string.no_net_no_delete));
            return;
        }
        HistoryRecord arecord = totallist.get(parentId - 1);
        List<UploadRecord> uploadlist = new ArrayList<UploadRecord>();
        UploadRecord aupload = new UploadRecord();
        aupload.setUpdateTime(Long.parseLong(arecord.getTimestamp()));
        aupload.setVid(arecord.getGvid());
        aupload.setSource(arecord.getSource());
        aupload.setPlayTime(Integer.parseInt(arecord.getPlay_time()));
        aupload.setAction(1);
        aupload.setCid(Integer.parseInt(arecord.getCategory_id()));
        aupload.setDurationTime(aupload.getDurationTime());
        aupload.setPid(aupload.getPid());
        uploadlist.add(aupload);
        if (arecord.getId() != null) {
            historyRecordDao.delete(arecord.getId());
        } else {
            historyRecordDao.deleteByGvid(arecord.getGvid());
        }
        //删除服务器
        if (UserLoginState.getInstance().isLogin()) {
            String json = JsonUtil.toJSONString(uploadlist);
            uploadHistoryRecord(UserLoginState.getInstance().getUserInfo().getToken(), json);
        }
        totallist.remove(parentId - 1);
        HistoryRecordManager.setHisToryRecordFromServer(totallist);
        iniShowTimeList();
        adapter.notifyDataSetChanged();
        reflashView();

    }

    private class RequestHistoryRecordListener implements RequestListener<SarrsArrayList> {
        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            //进行展现的相关操作
            HistoryRecordManager.setHisToryRecordFromServer(result);
            totallist = new ArrayList<HistoryRecord>();
            ArrayList<HistoryRecord> netlist = result;
            xListView.onRefreshComplete();
            if (records_list.size() == 0) {
                totallist = netlist;
            } else if (null == netlist || netlist.size() == 0) {
                totallist = records_list;
            } else if (records_list.size() > 0 && netlist.size() > 0) {
                totallist.addAll(netlist);
                for (int i = 0; i < records_list.size(); i++) {
                    for (int j = 0; j < netlist.size(); j++) {
                        //相同视频需要合并取时间最近的一个记录
                        if (records_list.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                            if (records_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) >= 0) {
                                totallist.remove(netlist.get(j));
                                totallist.add(records_list.get(i));
                            }
                            break;
                        }
                        if (j == netlist.size() - 1) {
                            totallist.add(records_list.get(i));
                        }
                    }
                }
            }
            iniShowTimeList();
            adapter.setmDatas(totallist);
            adapter.notifyDataSetChanged();
            xListView.getRefreshableView().setSelection(1);
            reflashView();
        }



        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }



    private void  showData(ArrayList<HistoryRecord> netlist){
        //进行展现的相关操作
        totallist = new ArrayList<HistoryRecord>();
        xListView.onRefreshComplete();
        if (records_list.size() == 0) {
            totallist = netlist;
        } else if (null == netlist || netlist.size() == 0) {
            totallist = records_list;
        } else if (records_list.size() > 0 && netlist.size() > 0) {
            totallist.addAll(netlist);
            for (int i = 0; i < records_list.size(); i++) {
                for (int j = 0; j < netlist.size(); j++) {
                    //相同视频需要合并取时间最近的一个记录
                    if (records_list.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                        if (records_list.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) >= 0) {
                            totallist.remove(netlist.get(j));
                            totallist.add(records_list.get(i));
                        }
                        break;
                    }
                    if (j == netlist.size() - 1) {
                        totallist.add(records_list.get(i));
                    }
                }
            }
        }
        iniShowTimeList();
        adapter.setmDatas(totallist);
        adapter.notifyDataSetChanged();
        xListView.getRefreshableView().setSelection(1);
        reflashView();

    }
    public ArrayList<HistoryRecord> orderByTimeDesc(ArrayList<HistoryRecord> list_historyrecord) {
        for (int i = 0; i < list_historyrecord.size(); i++) {
            for (int j = i; j < list_historyrecord.size(); j++) {
                if (list_historyrecord.get(i).getTimestamp().compareTo(list_historyrecord.get(j).getTimestamp()) < 0) {
                    HistoryRecord temp = list_historyrecord.get(i);
                    list_historyrecord.set(i, list_historyrecord.get(j));
                    list_historyrecord.set(j, temp);
                }
            }
        }
        return list_historyrecord;
    }

    public void iniShowTimeList() {
        adapter.isshowtimelist = null;
        adapter.isshowtimelist = new ArrayList<Boolean>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        totallist = orderByTimeDesc(totallist);
        for (int i = 0; i < totallist.size(); i++) {
            if (i == 0) {
                adapter.isshowtimelist.add(true);
            } else {
                String stime1 = format.format(new Date(Long.parseLong(totallist.get(i - 1).getTimestamp())));
                String stime2 = format.format(new Date(Long.parseLong(totallist.get(i).getTimestamp())));
                if (stime1.equals(stime2)) {
                    adapter.isshowtimelist.add(false);
                } else {
                    adapter.isshowtimelist.add(true);
                }
            }
        }
    }

    private void uploadHistoryRecord(String token, String json) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.UPLOAD_HISTORY_RECORD);
        HttpApi.
                uploadHistoryRecord(token, json, new UpoloadHistoryRecordListener());
    }

    public void reflashView() {
        if (totallist.size() == 0) {
            no_data_bg.setVisibility(View.VISIBLE);
            tv_download_edit.setVisibility(View.GONE);
            xListView.setVisibility(View.GONE);
            if (UserLoginState.getInstance().isLogin()) {
                mLogin.setVisibility(View.GONE);
                mNo_data_tip.setText(getResources().getString(R.string.no_historyrecord_tip));
            } else {
                mLogin.setVisibility(View.VISIBLE);
                mNo_data_tip.setText(getResources().getString(R.string.no_login_tip));
            }
        } else {
            no_data_bg.setVisibility(View.GONE);
            tv_download_edit.setVisibility(View.VISIBLE);
            xListView.setVisibility(View.VISIBLE);
        }
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(REQUESTCODEFORPLAY == requestCode){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getData();
//                }
//            },500);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private void setEditable(boolean editable) {
        this.editable = editable;
        if (editable) {
            tv_download_edit.setText(getString(R.string.edit));
        } else {
            tv_download_edit.setText(getString(R.string.complete));
        }
    }

    PullToRefreshSwipeMenuListView.SwipeMenuStatusListener swipeMenuStatusListener = new PullToRefreshSwipeMenuListView.SwipeMenuStatusListener() {
        @Override
        public void onMenuSmoothOpen() {
            setEditable(false);
        }

        @Override
        public void onMenuSmoothClose() {
            setEditable(true);
        }
    };
}
