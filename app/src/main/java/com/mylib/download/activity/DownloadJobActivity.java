package com.mylib.download.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinBaseActivity;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.DownLoadListActivity;
import com.chaojishipin.sarrs.adapter.VideoInfoAdapter;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.download.adapter.DownloadJobAdapter;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.download.Constants;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuCreator;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuItem;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuLayout;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuListView;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StoragePathsManager;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.utils.NetWorkTypeUtils;
import com.mylib.download.DownloadUtil;

import java.util.ArrayList;
import java.util.List;


public class DownloadJobActivity extends ChaoJiShiPinBaseActivity implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener, DownloadUtil.DownloadEndListener {
    public final static String pageid = "00S0020017_2";
    private LinearLayout mDelLayout;

    public void setmListView(SwipeMenuListView mListView) {
        this.mListView = mListView;
    }

    private SwipeMenuListView mListView;
    public int index;
    private DownloadJobAdapter adapter;
    private TextView title;
    private TextView all_select;
    private PopupWindow mCheckAllPopWindow;
    private RelativeLayout mCheckTabLayout;
    private TextView mCheckTabText;
    private ImageView mLeftButton;
//    private LinearLayout mContinuePlayLayout;
//    private TextView mContinuePlayButton;
//    private ImageView mPlayButtonLine;
    private String mediaId;
    private Dialog mTipDialog;
    private RelativeLayout title2layout;
    private TextView mConfirm_delete;
    private boolean mAllPauseState;//全部暂停按钮的初始状态，是全部暂停还是全部开始
    private String mediaName;
    private boolean isFromBackground = false;//按home键，从后台切换回来
    private RelativeLayout memory_info;
    private SparseArray<DownloadJob> jobs = new SparseArray<>(); //null;//放入updateListView的数据

    public SparseArray<DownloadJob> getJobs() {
        return jobs;
    }

    public SwipeMenuListView getmListView() {
        return mListView;
    }

    //  网络变化广播接收
    private NetworkCheckReceiver mCheckReceiver;
    private ImageView iv_download_back;
    private TextView tv_download_title;
    private TextView tv_download_edit;
    private ImageView iv_download_more;
    private TextView download_more_tip;
    //    private ContainSizeManager mSizeManager;
    private RelativeLayout download_no_item;
    DownloadJobAdapter.ViewHolder holder =  null;

    DownloadJob ajob=null;
    DownloadJob lastjob=null;
    public boolean ismAllPauseState() {
        return mAllPauseState;
    }

    public void setmAllPauseState(boolean mAllPauseState) {
        this.mAllPauseState = mAllPauseState;
    }

    private DownloadUtil mUtil;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_job);
        iv_download_more = (ImageView) findViewById(R.id.download_more);
        tv_download_title = (TextView) this.findViewById(R.id.download_tv_title);
        tv_download_edit = (TextView) this.findViewById(R.id.tv_download_edit);
        iv_download_back = (ImageView) findViewById(R.id.iv_download_back);
        download_more_tip = (TextView) this.findViewById(R.id.download_more_tip);
        memory_info = (RelativeLayout) this.findViewById(R.id.memoryinfo_layout);
        tv_download_edit.setOnClickListener(this);
        iv_download_back.setOnClickListener(this);
        mDelLayout = (LinearLayout) findViewById(R.id.delete_layout);
        mDelLayout.setOnClickListener(this);
        download_no_item = (RelativeLayout) findViewById(R.id.download_no_item);
        mListView = (SwipeMenuListView) findViewById(R.id.DownloadListView);
        title2layout = (RelativeLayout) findViewById(R.id.bottomlayout);
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);

        iv_download_more.setOnClickListener(this);
        mediaName = intent.getStringExtra("mediaName");
        mediaId = intent.getStringExtra("mediaId");
        title = (TextView) findViewById(R.id.download_middle_title);
        all_select = (TextView) findViewById(R.id.all_select);

        mConfirm_delete = (TextView) findViewById(R.id.confirm_delete);
        all_select.setText(getResources().getString(R.string.allstart));
        mConfirm_delete.setText(R.string.allpause);

        mConfirm_delete.setOnClickListener(this);
        all_select.setOnClickListener(this);
        mLeftButton = (ImageView) findViewById(R.id.leftButtonLayout);
        mLeftButton.setOnClickListener(this);
        initCheckTabPopWindow();
        title2layout.setVisibility(View.VISIBLE);

        //获取按钮的状态
        if (index == -1) {
            iv_download_more.setVisibility(View.GONE);
            download_more_tip.setVisibility(View.GONE);
            mConfirm_delete.setVisibility(View.VISIBLE);
            all_select.setVisibility(View.VISIBLE);
            memory_info.setVisibility(View.GONE);
        } else {
            download_more_tip.setVisibility(View.VISIBLE);
            iv_download_more.setVisibility(View.VISIBLE);
            mConfirm_delete.setVisibility(View.GONE);
            all_select.setVisibility(View.GONE);
            memory_info.setVisibility(View.VISIBLE);
            showAvailableSpace();
        }

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.drawable.download_swipe_menu_selector);
                // set item width
                deleteItem.setWidth(Utils.dip2px(75));
                // set a icon
                deleteItem.setIcon(R.drawable.delete_normal);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(12);
                deleteItem.setTitle(getApplicationContext().getString(R.string.delete_up));
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                deleteOneItem(position);
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                Log.d("swipe", "postion is " + position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                checkSwipeStatus();
            }
        });

        mListView.setOnItemClickListener(this);
        getList();
        adapter = new DownloadJobAdapter(jobs, this, index);
        mListView.setAdapter(adapter);

        mUtil = new DownloadUtil(this, mListView);
    }

    private void checkSwipeStatus() {
        SwipeMenuLayout mSwipe = null;
        mSwipe = mListView.getSwipeMenuLayout();
        if (mSwipe != null) {
            if (mSwipe.isOpen()) {
                tv_download_edit.setText(getResources().getString(R.string.complete));
            } else
                tv_download_edit.setText(getResources().getString(R.string.edit));
        } else
            tv_download_edit.setText(getResources().getString(R.string.edit));
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected void onPause() {
        mUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mUtil.onResume();
        if (index == -1) {
            title.setText(R.string.downloadingTitle);
            tv_download_title.setText(R.string.downloadingTitle);
        } else {
            title.setText(mediaName);
            tv_download_title.setText(mediaName);
            showAvailableSpace();
        }
        recalculate();
        isFromBackground = false;
        registerCheckNetwork();
        updatebuttons(-1);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        isFromBackground = true;
        super.onRestart();
    }

    private void showAvailableSpace() {
        if (ContainSizeManager.getInstance() != null) {
            ContainSizeManager.getInstance().setView(this);
            ContainSizeManager.getInstance().ansynHandlerSdcardSize();
        }
    }

    /**
     * 从后台按home切换回来，需要重新计算删除选中数量
     */
    private void recalculate() {
        if (isFromBackground && null != adapter && index == -1 && jobs != null) {//在下载文件夹中
            int num = 0;
            int size = jobs.size();
            for(int i=0; i<size; i++){
                if(jobs.valueAt(i).getCheck())
                    ++num;
            }
            adapter.deletedNum = num;
            setTitle();
        }
    }

    private void getList(){
        //TODO,从数据库取所有未完成的下载
        if(index < 0)
            jobs = DataUtils.getInstance().getDownloadJobArray();
        else{
            SparseArray<DownloadFolderJob> folderJobs = DataUtils.getInstance().getFolderJobs();
            if (null != folderJobs && index < folderJobs.size()) {
                jobs = folderJobs.valueAt(index).getDownloadJobs();
            }
        }
    }

    private void setupListView(int size) {
        if (size > 0) {
            mListView.setVisibility(View.VISIBLE);
            download_no_item.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            download_no_item.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long[] vibratorPatter = new long[]{0, 20};
        return false;
    }

    private void popIfContinueDownloadDialog(DownloadJob job) {
        if (!NetWorkTypeUtils.isWifi(this) && job.getStatus() != DownloadJob.DOWNLOADING) {
            if (job.isCurrentPathExist()) {
                checkIfContinueDownloadDialog(job);
            }
        } else {
            start(job);
        }
    }

    public void start(DownloadJob job) {
        if (!job.isCurrentPathExist()) {
        } else {
            if (null != job && null != job.getEntity()) {
                if (TextUtils.isEmpty(job.getEntity().getPath()) && !TextUtils.isEmpty(job.getmDestination())) {
                    job.getEntity().setPath(job.getmDestination());
                }
                DataUtils.getInstance().download(job);
            }
        }
    }

    private void checkIfContinueDownloadDialog(final DownloadJob job) {
        if (NetworkUtil.reportNetType(this) == NetworkUtil.TYPE_MOBILE) {
            Builder customBuilder = new Builder(DownloadJobActivity.this);
            customBuilder
                    .setTitle(R.string.tip)
                    .setMessage(R.string.wireless_tip)
                    .setPositiveButton(R.string.continue_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    start(job);
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(R.string.pause_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                dialog.dismiss();
                            }
                            return false;
                        }
                    });
            Dialog dialog = customBuilder.create();
            dialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DownloadJobAdapter.ViewHolder holder = (DownloadJobAdapter.ViewHolder) ((SwipeMenuLayout) view).getContentView().getTag();
        //可编辑状态

        if (adapter.isEditable()) {
            DownloadJobAdapter.ViewHolder viewholder = (DownloadJobAdapter.ViewHolder) ((SwipeMenuLayout) view).getContentView().getTag();
            if (jobs.valueAt(position).getCheck()) {
                viewholder.getBtnDelete().setChecked(false);
                viewholder.getBtnDelete().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.radiobutton_white_bg));
                jobs.valueAt(position).setCheck(false);
                adapter.deletedNum -= 1;
            } else {
                viewholder.getBtnDelete().setChecked(true);
                viewholder.getBtnDelete().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.radiobutton_red_bg));
                jobs.valueAt(position).setCheck(true);
                adapter.deletedNum += 1;
            }
            updatebuttons(-1);
            //不可编辑状态
        } else {
            // 多剧集播放
            if (index >= 0) {
                // 设置观看状态
                DownloadEntity downloadEntity = adapter.getItem(position).getEntity();
                DataUtils.getInstance().setIfWatch(downloadEntity, "true");
                downloadEntity.setIfWatch("true");
                View v = view.findViewById(R.id.ifwatch);
                if(v != null)
                    v.setVisibility(View.GONE);

                Intent intent = new Intent(this, ChaoJiShiPinVideoDetailActivity.class);

                SparseArray<DownloadFolderJob> jobs = DataUtils.getInstance().getFolderJobs();
                int key = jobs.keyAt(index);
                DownloadFolderJob folderJob = jobs.get(key);
                VideoItem item = VideoInfoAdapter.wrapDownloadEntity(downloadEntity);

                ArrayList<LocalVideoEpisode>localVideoEpisodes= DataUtils.getInstance().getLocalVideoEpisodes(folderJob);
                intent.putExtra(Utils.Medea_Mode, ConstantUtils.MediaMode.LOCAL);
                intent.putExtra("position",position);
                VideoDetailItem videoDetailItem = new VideoDetailItem();
                videoDetailItem.setTitle(item.getTitle());
                videoDetailItem.setDescription(item.getDescription());
                videoDetailItem.setId(item.getId());
                videoDetailItem.setCategory_id(item.getCategory_id() + "");
                videoDetailItem.setPlay_count(item.getPlay_count());
                videoDetailItem.setLocalVideoEpisodes(localVideoEpisodes);
                // 设置当前本地播放剧集列表点击项索引--
                videoDetailItem.setPorder(item.getOrder());
                List<VideoItem> items=new ArrayList<>();
                item.setIsLocal(true);
                items.add(item);
//              LogUtil.e("xll "," detail item "+item.getTitle());
                videoDetailItem.setVideoItems(items);
                videoDetailItem.setFromMainContentType(item.getFromMainContentType());
                videoDetailItem.setDetailImage(item.getImage());
                intent.putExtra("videoDetailItem", videoDetailItem);
                intent.putExtra("ref",pageid);
                startActivity(intent);
            }else{
                DownloadJob job = adapter.getItem(position);
                popIfContinueDownloadDialog(job);
            }
        }
    }

    @Override
    public void onDownloadEnd(DownloadJob job) {
        if(jobs == null || jobs.size() == 0 || adapter == null)
            return;
        int len = jobs.size();
        for(int i=0; i<len; i++){
            if(jobs.valueAt(i).getEntity().getId().equalsIgnoreCase(job.getEntity().getId())){
                jobs.removeAt(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        if (jobs.size() == 0 && index == -1)
            finish();
        else
            showAvailableSpace();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_layout:
                break;
            //第二个按钮
            case R.id.confirm_delete:
                if(adapter == null)
                    return;
                if (adapter.isEditable()) {
                    if (adapter.deletedNum > 0) {
                        deleteTip(-1);
                    } else {
                        Toast.makeText(DownloadJobActivity.this, "还未选择删除项", Toast.LENGTH_SHORT);
                    }
                } else {
                    DataUtils.getInstance().pauseAllDownload();
                }
                updatebuttons(0);
                break;
            //第一个按钮
            case R.id.all_select:
                //可编辑
                if (adapter.isEditable()) {
                    downloadVideoSelected();
                    adapter.notifyDataSetChanged();
                    //不可编辑
                } else {
                    if (ContainSizeManager.getInstance().getFreeSize() > Utils.SDCARD_MINSIZE) {//sd卡容量大于500m，可以添加
                        int size = jobs.size();
                        for(int i=0; i<size; i++){
                            DownloadJob job = jobs.valueAt(i);
                            DataUtils.getInstance().startDownload(job);
                        }
                    } else {
                        ToastUtil.showShortToast(DownloadJobActivity.this, R.string.sdcard_nospace);
                    }
                }
                updatebuttons(jobs.size());
                break;
            case R.id.iv_download_back:
                this.finish();
                break;
            case R.id.tv_download_edit:
                SwipeMenuLayout mSwipe = null;
                mSwipe = mListView.getSwipeMenuLayout();
                if (mSwipe != null) {
                    if (mSwipe.isOpen()) {
                        mSwipe.smoothCloseMenu();
                        tv_download_edit.setText(getResources().getString(R.string.edit));
                    } else {
                        checkEditSwipeStatus();
                    }
                } else {
                    checkEditSwipeStatus();
                }
                break;
            case R.id.download_more:
                Intent intent = new Intent(this, DownLoadListActivity.class);
                intent.putExtra("from", ConstantUtils.From_More_Down);
                VideoDetailItem videoDetailItem = VideoInfoAdapter.wrapDownloadEntity(jobs.valueAt(0).getEntity());
                VideoPlayerNotifytData notifytData = new VideoPlayerNotifytData();
                notifytData.setIsFirst(false);
                notifytData.setPosition(0);
                notifytData.setKey(0);
                notifytData.setType(ConstantUtils.PLAYER_FROM_FULLSCREEN_EPISO_TAG_CLICK);
                intent.putExtra("mediaNotifyData", notifytData);
                intent.putExtra("mVideoDetailItem", videoDetailItem);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void checkEditSwipeStatus() {
        if (index != -1) {
            // 编辑--》完成
            if (adapter.isEditable()) {
                mListView.setIsOpenStatus(true);
                download_more_tip.setVisibility(View.VISIBLE);
                iv_download_more.setVisibility(View.VISIBLE);
                mConfirm_delete.setVisibility(View.GONE);
                all_select.setVisibility(View.GONE);
                resetCheck();
                adapter.deletedNum = 0;
                tv_download_edit.setText(getResources().getString(R.string.edit));

            } else {
                mListView.setIsOpenStatus(false);
                // 完成--》编辑
                download_more_tip.setVisibility(View.GONE);
                iv_download_more.setVisibility(View.GONE);
                mConfirm_delete.setVisibility(View.VISIBLE);
                all_select.setVisibility(View.VISIBLE);
                tv_download_edit.setText(getResources().getString(R.string.complete));
            }
        }
        if (null != adapter) {
            //未下载成功的
            if (index == -1) {
                //可编辑除状态
                if (adapter.isEditable()) {
                    mListView.setIsOpenStatus(true);
                    all_select.setText(getResources().getString(R.string.allstart));
                    mConfirm_delete.setText(R.string.allpause);
                    tv_download_edit.setText(getResources().getString(R.string.edit));
                    cancelDelete(false);
                    //回到原来的状态
                } else {
                    mListView.setIsOpenStatus(false);
                    tv_download_edit.setText(getResources().getString(R.string.complete));
                    mConfirm_delete.setText(R.string.delete_up);
                    all_select.setText(R.string.check_all);
                    cancelDelete(true);
                }
                //已经下载好的
            } else {
                //可编辑
                if (adapter.isEditable()) {
                    mListView.setIsOpenStatus(true);
                    tv_download_edit.setText(getResources().getString(R.string.edit));
                    cancelDelete(false);
                } else {
                    mListView.setIsOpenStatus(false);
                    adapter.notifyDataSetChanged();
                    cancelDelete(true);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean onClickBackButton() {
        if (null != adapter && adapter.isEditable()) {
            cancelDelete(false);
            resetCheck();
            adapter.notifyDataSetChanged();
            return false;
        }
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onClickBackButton();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 改变是否是删除状态，重置选中状态
     */
    private void cancelDelete(boolean deleteState) {
        adapter.setEditable(deleteState);
        title.setVisibility(View.VISIBLE);
        adapter.deletedNum = 0;
        resetCheck();
        adapter.notifyDataSetChanged();
        updatebuttons(-1);
    }

    public void setTitle() {
        if(adapter.isEditable()) {
            if (adapter.deletedNum == 0) {
                title.setVisibility(View.VISIBLE);
                mConfirm_delete.setText(R.string.delete_up);
                mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
            } else if (adapter.deletedNum > 0) {
                showUserSelecedItem();
            }
        }
    }

    private void showUserSelecedItem() {
        String content = getString(R.string.delete_up);
        mConfirm_delete.setText(content);
        mConfirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
    }

    private void initCheckTabPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View mPopWindowView = inflater.inflate(R.layout.user_checkall_tab, null);
        if (null != mPopWindowView) {
            mCheckAllPopWindow = new PopupWindow(mPopWindowView, 232, 77);
            mCheckAllPopWindow.setFocusable(true);
            mCheckAllPopWindow.setBackgroundDrawable(new BitmapDrawable());
            initPopWindowComponent(mPopWindowView);
        }
    }

    private void initPopWindowComponent(View view) {
        mCheckTabLayout = (RelativeLayout) view.findViewById(R.id.check_tab_layout);
        mCheckTabText = (TextView) view.findViewById(R.id.user_checkall_tv);
        mCheckTabLayout.setOnClickListener(this);
    }

    private void downloadVideoSelected() {
        if (adapter.deletedNum != adapter.getCount()) {
            allCheck();
            adapter.deletedNum = jobs.size();
            updatebuttons(-1);
        } else {
            resetCheck();
            adapter.deletedNum = 0;
            updatebuttons(-1);
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteTip(final int position) {
        try {
            Builder builder = new Builder(this);
            builder.setTitle(R.string.tip);
            builder.setMessage(getString(R.string.delete_file));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (null == adapter.getList() || adapter.getList().size() <= 0) {
                        ToastUtil.toastPrompt(DownloadJobActivity.this, R.string.undefind_delete_file, 0);
                        tv_download_edit.setText(getResources().getString(R.string.edit));
                        return;
                    }
                    deleteDownloadFile(position);
                    if (index == -1) {
                        mConfirm_delete.setText(getResources().getString(R.string.allpause));
                        all_select.setText(getResources().getString(R.string.allstart));
                    }
                    tv_download_edit.setText(getResources().getString(R.string.edit));
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDownloadFile(final int index) {
        showLoadingView(DownloadJobActivity.this, false, R.string.deleting);
        new Thread(){
            public void run(){
                ArrayList<Integer> list = executeDelete(index);
                Message msg = mHandler.obtainMessage(Constants.MESSAGE_DELETE_DOWNLOAD_FILE);
                msg.obj = list;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void deleteOneItem(int position) {
        showLoadingView(DownloadJobActivity.this, false, R.string.deleting);
        deleteDownloadFile(position);
    }

    protected ArrayList<Integer> executeDelete(int index) {
        ArrayList<Integer> list = new ArrayList<>();
        if(index >= 0) {
            DownloadJob j = jobs.valueAt(index);
            list.add(jobs.keyAt(index));
            DataUtils.getInstance().deleteDownloadFile(j);
            if(j.getCheck()) {
                --adapter.deletedNum;
            }
        }else{
            int len = jobs.size();
            for(int i=0; i<len; i++){
                DownloadJob j = jobs.valueAt(i);
                if(j.getCheck()) {
                    list.add(jobs.keyAt(i));
                    DataUtils.getInstance().deleteDownloadFile(j);
                    --adapter.deletedNum;
                }
            }
        }
        return list;
    }

    public void showLoadingView(Context context, boolean isCouldCancel, int strId) {
        mTipDialog = new Dialog(context, R.style.waiting);
        mTipDialog.setContentView(R.layout.dialog_waiting);
        TextView textView = (TextView) mTipDialog.findViewById(R.id.waiting_text);
        textView.setText(strId);
        mTipDialog.setCanceledOnTouchOutside(isCouldCancel);
        mTipDialog.setCancelable(isCouldCancel);
        if (!mTipDialog.isShowing()) {
            try {
                mTipDialog.show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void cancelLoadingView() {
        if (null != mTipDialog && mTipDialog.isShowing()) {
            mTipDialog.cancel();
        }
    }

    /**
     * qinguoli
     * 注册监听手机网络广播 接收
     */
    protected void registerCheckNetwork() {
        mCheckReceiver = new NetworkCheckReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(PlayerUtils.CONNECTIVTY_CHANGE);
        this.registerReceiver(mCheckReceiver, intentfilter);
    }

    /**
     * qinguoli
     * 取消对网络变化的监听*
     */
    protected void unregisterCheckNetwork() {
        if (mCheckReceiver != null) {
            unregisterReceiver(mCheckReceiver);
        }
    }

    class NetworkCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                //读取保存的之前网络状态
                SharedPreferences priorNetState = getSharedPreferences("priornetstate",
                        Activity.MODE_PRIVATE);
                int priorNetType = priorNetState.getInt("netstate", 2);
                int nowNetType = NetworkUtil.checkNet(getApplicationContext(), intent, priorNetType);
                SharedPreferences.Editor editor = priorNetState.edit();
                editor.putInt("netstate", nowNetType);
                editor.commit();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterCheckNetwork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetCheck();
        mUtil.destroy();
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_DELETE_DOWNLOAD_FILE:
                cancelLoadingView();
                ToastUtil.toastPrompt(DownloadJobActivity.this, R.string.delete_success, 0);
                showAvailableSpace();
                if(msg.obj instanceof ArrayList){
                    for(Integer i : (ArrayList<Integer>)msg.obj){
                        jobs.remove(i);
                    }
                }
                if(onClickBackButton())
                    return;
                if (index == -1) {
                    iv_download_more.setVisibility(View.GONE);
                    download_more_tip.setVisibility(View.GONE);
                    mConfirm_delete.setVisibility(View.VISIBLE);
                    all_select.setVisibility(View.VISIBLE);
                    memory_info.setVisibility(View.GONE);
                } else {
                    download_more_tip.setVisibility(View.VISIBLE);
                    iv_download_more.setVisibility(View.VISIBLE);
                    mConfirm_delete.setVisibility(View.GONE);
                    all_select.setVisibility(View.GONE);
                    memory_info.setVisibility(View.VISIBLE);
                }
                tv_download_edit.setText(getResources().getString(R.string.edit));
                mListView.setIsOpenStatus(true);
                break;

            default:
                break;
        }
    }

    /**
     * 重置job的选择状态为false
     */
    private void resetCheck() {
        if (jobs != null && jobs.size() > 0) {
            for (int i = 0; i < jobs.size(); i++) {
                jobs.valueAt(i).setCheck(false);
            }
        }
    }

    /**
     * 全选状态
     */
    private void allCheck() {
        if (jobs != null && jobs.size() > 0) {
            for (int i = 0; i < jobs.size(); i++) {
                jobs.valueAt(i).setCheck(true);
            }
        }
    }

    public void updatebuttons(int num) {
        if (index == -1) {
            //可编辑
            if (adapter.isEditable()) {
                //已经全选
                if (adapter.deletedNum == adapter.getCount()) {
                    all_select.setText(getResources().getString(R.string.deselect_all));
                    all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    all_select.setClickable(true);
                    mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    mConfirm_delete.setClickable(true);
                }
                // 有选 但是没有全选
                else if (adapter.deletedNum < adapter.getCount() && adapter.deletedNum > 0) {
                    all_select.setText(getResources().getString(R.string.check_all));
                    all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    all_select.setClickable(true);
                    mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    mConfirm_delete.setClickable(true);
                    //没一个 选中
                } else if (adapter.deletedNum == 0) {
                    all_select.setText(getResources().getString(R.string.check_all));
                    all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    all_select.setClickable(true);
                    mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_999999));
                    mConfirm_delete.setClickable(false);
                }
                //不可编辑
            } else {
                //全部开始  全部暂停
                int downloadtype = downloadType(num);
                //全部暂停
                if (downloadtype == -1) {
                    all_select.setText(getResources().getString(R.string.allstart));
                    all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    all_select.setClickable(true);
                    mConfirm_delete.setText(getResources().getString(R.string.allpause));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_999999));
                    mConfirm_delete.setClickable(false);
                    //又开始也有暂停
                } else if (downloadtype == 0) {
                    all_select.setText(getResources().getString(R.string.allstart));
                    all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    all_select.setClickable(true);
                    mConfirm_delete.setText(getResources().getString(R.string.allpause));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    mConfirm_delete.setClickable(true);
                    //全部开始
                } else {
                    all_select.setText(getResources().getString(R.string.allstart));
                    all_select.setTextColor(getResources().getColor(R.color.color_999999));
                    all_select.setClickable(false);
                    mConfirm_delete.setText(getResources().getString(R.string.allpause));
                    mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                    mConfirm_delete.setClickable(true);
                }
            }
        } else {
            //全选
            if (adapter.deletedNum == adapter.getCount()) {
                all_select.setText(getResources().getString(R.string.cancel_all));
                all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                all_select.setClickable(true);
                mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                mConfirm_delete.setClickable(true);
            }
            if (adapter.deletedNum == 0) {
                all_select.setText(getResources().getString(R.string.check_all));
                all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                all_select.setClickable(true);
                mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                mConfirm_delete.setTextColor(getResources().getColor(R.color.color_999999));
                mConfirm_delete.setClickable(false);
            }
            if (adapter.deletedNum > 0 && adapter.deletedNum < adapter.getCount()) {
                all_select.setText(getResources().getString(R.string.check_all));
                all_select.setTextColor(getResources().getColor(R.color.color_ff1E27));
                all_select.setClickable(true);
                mConfirm_delete.setText(getResources().getString(R.string.delete_up));
                mConfirm_delete.setTextColor(getResources().getColor(R.color.color_ff1E27));
                mConfirm_delete.setClickable(true);
            }
        }
    }

    //-1 全部暂停 1 全部开始  0 有开始也有暂停
    private int downloadType(int number) {
        int num;
        if(number >= 0)
            num = number;
        else
            num = DataUtils.getInstance().getDownloadingJobNum();
        if(num == jobs.size())
            return 1;       //全部开始
        else if(num > 0)
            return 0;       //有暂停也有下载
        return -1;          //全部暂停
    }
}
