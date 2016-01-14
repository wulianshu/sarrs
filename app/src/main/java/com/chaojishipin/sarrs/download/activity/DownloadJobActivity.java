package com.chaojishipin.sarrs.download.activity;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.download.adapter.DownloadJobAdapter;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.download.download.Constants;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadManager;
import com.chaojishipin.sarrs.download.download.DownloadObserver;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuCreator;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuItem;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuLayout;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuListView;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.utils.NetWorkTypeUtils;

import java.util.ArrayList;
import java.util.List;


public class DownloadJobActivity extends ChaoJiShiPinBaseActivity implements DownloadObserver, OnClickListener,
        OnItemClickListener, OnItemLongClickListener {
    public final static String pageid = "00S0020017_2";
    private LinearLayout mDelLayout;
//  private GestureOverlayView mGesture;
    private Handler mHandler;
    private DownloadManager mDownloadManager;

    public void setmListView(SwipeMenuListView mListView) {
        this.mListView = mListView;
    }

//  private ViewFlipper mViewFlipper;
//  private ListView mListView;

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
    private SparseArray<DownloadJob> jobs = null;//放入updateListView的数据

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
        mHandler = new Handler();
        mDownloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
        download_no_item = (RelativeLayout) findViewById(R.id.download_no_item);
        mListView = (SwipeMenuListView) findViewById(R.id.DownloadListView);
        title2layout = (RelativeLayout) findViewById(R.id.bottomlayout);
//        mListView.setOnItemClickListener(this);
//        mListView.setOnItemLongClickListener(this);
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
//        ChaoJiShiPinApplication.getInstatnce().setActivityStack(this);
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
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0x44, 0x44,
//                        0x44)));
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
//                if(holder!=null && ajob!=null) {
//                    holder.getProgressBar().setProgress(ajob.getProgress());
//                    holder.getProgressText().setText(ajob.getRate());
//                    holder.getDownloadLength().setText(DownloadUtils.getDownloadedSize(ajob.getDownloadedSize()) + "M/" + DownloadUtils.getDownloadedSize(ajob.getTotalSize()) + "M");
//                }
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
//                if(holder!=null && ajob!=null) {
//                    holder.getProgressBar().setProgress(ajob.getProgress());
//                    holder.getProgressText().setText(ajob.getRate());
//                    holder.getDownloadLength().setText(DownloadUtils.getDownloadedSize(ajob.getDownloadedSize()) + "M/" + DownloadUtils.getDownloadedSize(ajob.getTotalSize()) + "M");
//                }
//                mListView.getSwipeMenuLayout().setIsswiping(false);
                checkSwipeStatus();
            }
        });

        mListView.setOnItemClickListener(this);
        ViewGroup root=(ViewGroup) this.getWindow().getDecorView();  //获取本Activity下的获取最外层控件
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

    private void updateAllPauseState() {
        if (ismAllPauseState()) {//存在非暂停状态
        } else {
        }

    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mDownloadManager.deregisterDownloadObserver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (index == -1) {
            title.setText(R.string.downloadingTitle);
            tv_download_title.setText(R.string.downloadingTitle);
        } else {
            title.setText(mediaName);
            tv_download_title.setText(mediaName);
            showAvailableSpace();
        }
        mDownloadManager.registerDownloadObserver(this);
        mDownloadManager.notifyObservers();
        recalculate();
        isFromBackground = false;
        registerCheckNetwork();
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
        if (isFromBackground && null != adapter && index == -1) {//在下载文件夹中
            int num = 0;
            ArrayList<DownloadJob> jobsList = mDownloadManager.getProvider().getQueuedDownloads();
            if (jobsList != null) {
                for (int i = 0; i < jobsList.size(); i++) {
                    if (jobsList.get(i).getCheck()) {
                        num++;
                    }
                }
            }
            adapter.deletedNum = num;
            setTitle();
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateListView();
        }
    };

    private void updateListView() {
        LogUtil.e("wulianshu","updateListView.........");

        if (null == mDownloadManager || null == mListView) {
            return;
        }
        if (index == -1) {//进入下载文件夹
            ArrayList<DownloadJob> jobsList = mDownloadManager.getProvider().getQueuedDownloads();
            jobs = new SparseArray<DownloadJob>();

            setmAllPauseState(false);//每次刷新前，重置
            for (int i = 0; i < jobsList.size(); i++) {
                jobs.append(jobsList.get(i).getEntity().getAddTime(), jobsList.get(i));
                if (jobsList.get(i).getStatus() == DownloadJob.DOWNLOADING) {
                    setmAllPauseState(true);//有不是暂停的状态
//                    if (mListView.getSwipeMenuLayout() != null && mListView.getSwipeMenuLayout().isOpen()) {
//                        if (adapter != null) {
//                            Log.d("isOpen", "open");
//                            adapter.setIsShowSwipe(true);
//                        }
//                    } else if (mListView.getSwipeMenuLayout() != null && !mListView.getSwipeMenuLayout().isOpen()) {
//                        if (adapter != null) {
//                            Log.d("isOpen", "close");
//                            adapter.setIsShowSwipe(false);
//                        }
//                    }
                    Log.d("refresh", "isdowning");
//                    ChaoJiShiPinApplication.getInstatnce().startCheckSDCardFreeSizeService();//启动检查sd卡容量}
                }

            }

//            updateAllPauseState();
        } else {//进入完成文件夹
            SparseArray<DownloadFolderJob> folderJobs = mDownloadManager.getProvider().getFolderJobs();

            if (null != folderJobs && index < folderJobs.size()) {
                jobs = folderJobs.valueAt(index).getDownloadJobs();
            }
        }
        try {
            if (null != mListView.getAdapter() && mListView.getAdapter() instanceof DownloadJobAdapter) {
                adapter = (DownloadJobAdapter) mListView.getAdapter();
            }
            if (adapter != null && jobs != null && jobs.size() == adapter.getCount()) {
                for(int i=0;i<jobs.size();i++){
                      ajob =  jobs.get(jobs.keyAt(i));
//                    Log.e("DownloadJobActivity",jobs.keyAt(i)+"");
                    if(ajob.getStatus() == DownloadJob.DOWNLOADING && adapter.downloadingview != null){

                        if(lastjob != null && !lastjob.getEntity().getGlobaVid().equals(ajob.getEntity().getGlobaVid()) ){
                            LogUtil.e("wulianshu", "adapter ........ notify");
                            adapter.notifyDataSetChanged();
                        }
                        lastjob = ajob;
                        if(mListView.getSwipeMenuLayout()!=null && mListView.getSwipeMenuLayout().getState()== SwipeMenuLayout.STATE_OPEN){
                            mListView.getSwipeMenuLayout().smoothOpenMenu();
                        }
                        holder = (DownloadJobAdapter.ViewHolder) adapter.downloadingview.getTag();
//                      if(mListView.getSwipeMenuLayout().isFling()|| mListView.getSwipeMenuLayout().isClose()) {
                        if(holder.getDownloadName().getText().equals(ajob.getEntity().getDisplayName())) {
//                          LogUtil.e("wulianshu", "界面更新正常。。。。。。。。。。");
                            holder.getProgressBar().setProgressDrawable(getResources().getDrawable(R.drawable.progress_style_download));
                            holder.getProgressBar().setProgress(ajob.getProgress());
                            holder.getProgressText().setText(ajob.getRate());
                            if(ajob.getEntity().getDownloadType().equals(DownloadInfo.M3U8)){
                                holder.getDownloadLength().setText(getResources().getString(R.string.compulate_size));
                            }else{
                                holder.getDownloadLength().setText(DownloadUtils.getDownloadedSize(ajob.getDownloadedSize()) + "M/" + DownloadUtils.getDownloadedSize(ajob.getTotalSize()) + "M");
                            }
                        }
                        return;
                    }
                }
                adapter.notifyDataSetChanged();
                return;
            } else if (adapter != null && jobs != null && jobs.size() != adapter.getCount()) {
                adapter.setList(jobs);
                adapter.notifyDataSetChanged();
                return;
            } else if (jobs != null) {

                adapter = new DownloadJobAdapter(jobs, this, index);
                mListView.setAdapter(adapter);
                //初始化 第一个和第二个按钮
                updatebuttons();
            }
            setupListView(jobs.size());
        } catch (java.lang.ClassCastException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void popIfContinueDownloadDialog(DownloadJob job, DownloadJobAdapter.ViewHolder holder) {
        if (!NetWorkTypeUtils.isWifi(this)) {
            if (job.isCurrentPathExist()) {
                checkIfContinueDownloadDialog(job, holder);
            }
        } else {
            start(job, holder);
        }
    }

    public void start(DownloadJob job, DownloadJobAdapter.ViewHolder holder) {
        if (!job.isCurrentPathExist()) {
        } else {
            if (null != job && null != job.getEntity()) {
                if (TextUtils.isEmpty(job.getEntity().getPath()) && !TextUtils.isEmpty(job.getmDestination())) {
                    job.getEntity().setPath(job.getmDestination());
                }
                job.start();
                if (null != holder) {
//                    holder.getProgressText().setText("0.0KB/s");
                }
            }
        }
    }

    private void checkIfContinueDownloadDialog(final DownloadJob job, final DownloadJobAdapter.ViewHolder holder) {
        if (NetworkUtil.reportNetType(this) == NetworkUtil.TYPE_MOBILE) {
            Builder customBuilder = new Builder(DownloadJobActivity.this);
            customBuilder
                    .setTitle(R.string.tip)
                    .setMessage(R.string.wireless_tip)
                    .setPositiveButton(R.string.continue_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    start(job, holder);
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
        } else {
           // ToastUtil.showShortToast(this, R.string.nonet_tip);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DownloadJobAdapter.ViewHolder holder = (DownloadJobAdapter.ViewHolder) ((SwipeMenuLayout) view).getContentView().getTag();
        //可编辑状态

        if (adapter.deleteState) {
            DownloadJobAdapter.ViewHolder viewholder = (DownloadJobAdapter.ViewHolder) ((SwipeMenuLayout) view).getContentView().getTag();

            if (adapter.mChecked.get(position)) {
                viewholder.getBtnDelete().setChecked(false);
//              viewholder.getBtnDelete().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.radiobutton_white_bg));
                adapter.mChecked.set(position, false);
                jobs.valueAt(position).setCheck(false);
                adapter.deletedNum = adapter.deletedNum - 1;
            } else {
                viewholder.getBtnDelete().setChecked(true);
//              viewholder.getBtnDelete().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.radiobutton_red_bg));
                adapter.mChecked.set(position, true);
                jobs.valueAt(position).setCheck(true);
                adapter.deletedNum = adapter.deletedNum + 1;
            }
            adapter.notifyDataSetChanged();
            updatebuttons();
            //不可编辑状态
        } else {
//          taskstate = "";
            DownloadJob job = adapter.getItem(position);
            switch (job.getStatus()) {
                case DownloadJob.NO_USER_PAUSE:
                    if (ContainSizeManager.getInstance().getFreeSize() > Utils.SDCARD_MINSIZE) {//sd卡容量大于500m，可以添加
                        popIfContinueDownloadDialog(job, holder);
                    } else {
                        ToastUtil.showShortToast(DownloadJobActivity.this, R.string.sdcard_nospace);
                    }
                    break;
                case DownloadJob.PAUSE:
                    if (ContainSizeManager.getInstance().getFreeSize() > Utils.SDCARD_MINSIZE) {//sd卡容量大于500m，可以添加
                        popIfContinueDownloadDialog(job, holder);
                    } else {
                        ToastUtil.showShortToast(DownloadJobActivity.this, R.string.sdcard_nospace);
                    }
                    break;
                case DownloadJob.WAITING:
                    job.cancel();
                    holder.getProgressText().setText("已暂停");
                    break;
                case DownloadJob.DOWNLOADING:
                    job.pauseByUser();
                    holder.getProgressText().setText("已暂停");
                    break;

            }
            updatebuttons();

            // 多剧集播放
            if (index >= 0) {
                Intent intent = new Intent(this, ChaoJiShiPinVideoDetailActivity.class);
                DownloadEntity downloadEntity = adapter.getItem(position).getEntity();
//                jobs.get(position).getEntity();
                DownloadManager mDownloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
                SparseArray<DownloadFolderJob> jobs = mDownloadManager.getDownloadFolderJobs();
                int key = jobs.keyAt(index);
                DownloadFolderJob folderJob = jobs.get(key);
                VideoItem item = VideoInfoAdapter.wrapDownloadEntity(downloadEntity);
                /*   PlayData playData = null;
                if (item != null) {
                    ArrayList<LocalVideoEpisode>localVideoEpisodes= mDownloadManager.getLocalVideoEpisodes(folderJob);
                    playData = new PlayData(localVideoEpisodes, ConstantUtils.PLAYER_FROM_DOWNLOAD, item.getSource(), item.getOrder());
                    // playData = new PlayData(item.getTitle(), item.getGvid(), ConstantUtils.PLAYER_FROM_DOWNLOAD,item.getSource());
                }
                playData.setIsLocalVideo(true);
                intent.putExtra(Utils.PLAY_DATA, playData);*/

                ArrayList<LocalVideoEpisode>localVideoEpisodes= mDownloadManager.getLocalVideoEpisodes(folderJob);
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
                // 设置观看状态
                mDownloadManager.setIfWatch(downloadEntity, "true");
                downloadEntity.setIfWatch("true");
            }
            adapter.notifyDataSetChanged();
        }
    }

    private List<DownloadJob> getCompletedJobList() {
        SparseArray<DownloadFolderJob> folderJobs = mDownloadManager.getProvider().getFolderJobs();
        SparseArray<DownloadJob> jobs = null;
        List<DownloadJob> result = new ArrayList<DownloadJob>();
        if (null != folderJobs && index < folderJobs.size() && index >= 0) {
            jobs = folderJobs.valueAt(index).getDownloadJobs();
            for (int i = 0; i < jobs.size(); i++) {
                if (jobs.valueAt(i).getProgress() == 100) {
                    result.add(jobs.valueAt(i));
                }
            }
        }

        return result;
    }

    @Override
    public void onDownloadChanged(DownloadManager manager) {
        mHandler.post(mUpdateTimeTask);
    }

    @Override
    public void onDownloadEnd(DownloadManager manager, DownloadJob job) {
        if(index!=-1){
            if(mDownloadManager.getProvider().getFolderJobs().valueAt(index).getDownloadJobs()!=null && adapter!=null) {
                jobs = mDownloadManager.getProvider().getFolderJobs().valueAt(index).getDownloadJobs();
                adapter.notifyDataSetChanged();
            }
//            if (adapter.deletedNum > 0) {
//                adapter.deletedNum--;
//                setTitle();
//            }
        }
        else if (index == -1 && adapter !=null) {
            SparseArray<DownloadJob> jobList = adapter.getList();
            int i = jobList.indexOfValue(job);
            if (adapter.mChecked.get(i)) {
                if (adapter.deletedNum > 0) {
                    adapter.deletedNum--;
                    setTitle();
                }
            }
            adapter.mChecked.remove(i);
        }
        showAvailableSpace();
        ArrayList<DownloadJob> jobsList = mDownloadManager.getProvider().getQueuedDownloads();
        if (jobsList.size() == 0 && index == -1)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_layout:
                break;
            //第二个按钮
            case R.id.confirm_delete:
                if (null != adapter) {
//                    if (index == -1) {
//                    可编辑   删除选中的文件
                    if (adapter.deleteState) {
                        if (adapter.deletedNum > 0) {
                            deleteTip();
                        } else {
                            Toast.makeText(DownloadJobActivity.this, "还未选择删除项", Toast.LENGTH_SHORT);
                        }
                        //不可编辑  全部暂停
                    } else {
                        for (DownloadJob job : mDownloadManager.getQueuedDownloads()) {
                            if (null != job && job.getStatus() == DownloadJob.DOWNLOADING) {
                                job.pauseByUser();
                            }
                            if (null != job && job.getStatus() == DownloadJob.WAITING) {
                                job.cancel();
                            }
                            mDownloadManager.notifyObservers();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                updatebuttons();
                break;
            //第一个按钮
            case R.id.all_select:
                //可编辑
                if (adapter.deleteState) {
                    downloadVideoSelected();
                    //不可编辑
                } else {
                    if (ContainSizeManager.getInstance().getFreeSize() > Utils.SDCARD_MINSIZE) {//sd卡容量大于500m，可以添加
                        for (DownloadJob job : mDownloadManager.getQueuedDownloads()) {
                            if (null != job && (job.getStatus() == DownloadJob.NO_USER_PAUSE || job.getStatus() == DownloadJob.PAUSE)) {
                                if (job.getExceptionType() != DownloadJob.NO_SD
                                        && job.getExceptionType() != DownloadJob.FILE_NOT_FOUND) {
                                    job.start();
                                }
                            }
                        }
                    } else {
                        ToastUtil.showShortToast(DownloadJobActivity.this, R.string.sdcard_nospace);
                    }
                }
                adapter.notifyDataSetChanged();
                updatebuttons();
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
                //notifytData.setLastKey(0);
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
            if (adapter.deleteState) {
                mListView.setIsOpenStatus(true);
                download_more_tip.setVisibility(View.VISIBLE);
                iv_download_more.setVisibility(View.VISIBLE);
                mConfirm_delete.setVisibility(View.GONE);
                all_select.setVisibility(View.GONE);

                for (int i = 0; i < adapter.mChecked.size(); i++) {
                    adapter.mChecked.set(i, false);
                }
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
                if (adapter.deleteState) {
                    mListView.setIsOpenStatus(true);
                    all_select.setText(getResources().getString(R.string.allstart));
                    mConfirm_delete.setText(R.string.allpause);
                    tv_download_edit.setText(getResources().getString(R.string.edit));
                    adapter.setIsshowRadiobutton(false);
                    cancelDelete(false);
                    //选择按钮的状态还原 选中个数还原
                    for (int i = 0; i < adapter.mChecked.size(); i++) {
                        adapter.mChecked.set(i, false);
                    }
                    resetCheck();
                    adapter.deletedNum = 0;
                    updatebuttons();
                    //回到原来的状态
                } else {
                    mListView.setIsOpenStatus(false);
                    adapter.setIsshowRadiobutton(true);
                    tv_download_edit.setText(getResources().getString(R.string.complete));
                    mConfirm_delete.setText(R.string.delete_up);
                    all_select.setText(R.string.check_all);
                    cancelDelete(true);
                    updatebuttons();
                }
                //已经下载好的
            } else {
                //可编辑
                if (adapter.deleteState) {
                    mListView.setIsOpenStatus(true);
//                          title2layout.setVisibility(View.GONE);
                    tv_download_edit.setText(getResources().getString(R.string.edit));
                    cancelDelete(false);
                    updatebuttons();
                } else {
                    mListView.setIsOpenStatus(false);
//                          title2layout.setVisibility(View.VISIBLE);
                    adapter.setIsshowRadiobutton(true);
                    adapter.notifyDataSetChanged();
                    cancelDelete(true);
                    updatebuttons();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void onClickBackButton() {
        if (null != adapter && adapter.deleteState) {
            cancelDelete(false);
            if (null != adapter.mChecked && adapter.mChecked.size() > 0) {
                for (int i = 0; i < adapter.mChecked.size(); i++) {
                    adapter.mChecked.set(i, false);
                }
            } else {

            }
            adapter.notifyDataSetChanged();
            return;
        }
        finish();
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
        adapter.deleteState = deleteState;
        title.setVisibility(View.VISIBLE);
        adapter.deletedNum = 0;
        resetCheck();
        if (deleteState) {

        } else {
            toggleConinuePlayButton(mediaId);
        }
    }

    public void setTitle() {
        if(adapter.deleteState) {
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
            mConfirm_delete.setText(R.string.delete_up);
            all_select.setText(R.string.cancel_all);
            mConfirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
            all_select.setTextColor(getResources().getColor(R.color.color_FF1E27));
            for (int i = 0; i < adapter.mChecked.size(); i++) {
                if (i < adapter.getCount()) {
                    adapter.mChecked.set(i, true);
                }
            }
            allCheck();
            adapter.deletedNum = adapter.getCount();
            updatebuttons();
        } else {
//			title.setVisibility(View.VISIBLE);
//			mUserDeletecount.setVisibility(View.GONE);
//			mDeleteIcon.setBackgroundResource(R.drawable.pic_delete_normal1);
            mConfirm_delete.setText(R.string.delete_up);
            mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
            // 用户选择取消全选则用户当前选中项为0
            for (int i = 0; i < adapter.mChecked.size(); i++) {
                adapter.mChecked.set(i, false);
            }
            resetCheck();
            adapter.deletedNum = 0;
            updatebuttons();
        }
//		if (mCheckAllPopWindow.isShowing()) {
//			mCheckAllPopWindow.dismiss();
//		}
        adapter.notifyDataSetChanged();
        mDownloadManager.notifyObservers();
    }

//    private void downloadVideoSelected() {
//        if (adapter.deletedNum != adapter.getCount()) {
//            String content = getString(R.string.delete_up) + "(" + adapter.getCount() + ")";
//            mConfirm_delete.setText(content);
//            mConfirm_delete.setTextColor(getResources().getColor(R.color.confirm_delete_color));
//            allCheck();
//            adapter.deletedNum = adapter.getCount();
//        } else {
//            mConfirm_delete.setText(R.string.delete_up);
//            mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
//            adapter.deletedNum = 0;
//            resetCheck();
//            adapter.deletedNum = 0;
//        }
//        mDownloadManager.notifyObservers();
//    }

    private void deleteTip() {
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
                        if (index == -1) {
                            if (null != adapter) {
                                for (int i = 0; i < adapter.mChecked.size(); i++) {
                                    adapter.mChecked.set(i, false);
                                }
                            }
                        } else {

                        }
                        tv_download_edit.setText(getResources().getString(R.string.edit));
                        return;
                    }
                    DeleteDownloadfile();
                    if (index == -1) {
                        mConfirm_delete.setText(getResources().getString(R.string.allpause));
                        all_select.setText(getResources().getString(R.string.allstart));

                    } else {

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

    private Handler mDeteDownloadFileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MESSAGE_DELETE_DOWNLOAD_FILE:
                    cancelLoadingView();
                    ToastUtil.toastPrompt(DownloadJobActivity.this, R.string.delete_success, 0);

                    showAvailableSpace();
                    cancelDelete(false);
                    if (adapter.getCount() < 1) {
                        onClickBackButton();
                    } else {
                        if (index != -1) {
//                            title2layout.setVisibility(View.GONE);
                        }
                        mDownloadManager.registerDownloadObserver(DownloadJobActivity.this);
                        mDownloadManager.notifyObservers();
                    }
                    adapter = new DownloadJobAdapter(jobs, DownloadJobActivity.this, index);
                    mListView.setAdapter(adapter);

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

    };

    private void DeleteDownloadfile() {
        showLoadingView(DownloadJobActivity.this, false, R.string.deleting);
        executeDelete();
        mDeteDownloadFileHandler.sendEmptyMessage(Constants.MESSAGE_DELETE_DOWNLOAD_FILE);
    }

    private void deleteOneItem(int position) {
        showLoadingView(DownloadJobActivity.this, false, R.string.deleting);
//		LogUtils.d("dd","删除之前---"+downloadList.size()+"");
        mDownloadManager.deregisterDownloadObserver(this);
        SparseArray<DownloadJob> jobs = adapter.getList();
        DownloadJob job = jobs.valueAt(position);
        int key = 0;
        if (index == -1) {
            key = job.getEntity().getAddTime();
        } else {
            key = job.getIndex();
        }

        mDownloadManager.deleteDownload(job);
        jobs.delete(key);
        adapter.deletedNum--;

        if (jobs.size() == 0 && index != -1) {
            SparseArray<DownloadFolderJob> folderJobs = mDownloadManager.getProvider().getFolderJobs();
            int localKey = folderJobs.keyAt(position);
            mDownloadManager.getDownloadFolderJobs().delete(localKey);
        }
        mDownloadManager.startNextTask();
        mDeteDownloadFileHandler.sendEmptyMessage(Constants.MESSAGE_DELETE_DOWNLOAD_FILE);
//        tv_download_edit.setText(getResources().getString(R.string.edit));
//        mListView.setIsOpenStatus(true);
    }

//    class executeDelete extends AsyncTask<Object, Object, Object> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showLoadingView(DownloadJobActivity.this, false, R.string.deleting);
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//            super.onPostExecute(result);
//            cancelLoadingView();
//            ToastUtil.toastPrompt(DownloadJobActivity.this, R.string.delete_success, 0);
//            if (mSizeManager != null) {
//                mSizeManager.ansynHandlerSdcardSize();
//            }
//            cancelDelete(false);
//            if (adapter.getCount() <= 1) {
//                onClickBackButton();
//            } else {
//                mDownloadManager.registerDownloadObserver(DownloadJobActivity.this);
//                mDownloadManager.notifyObservers();
//            }
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            executeDelete();
//            return null;
//        }
//
//    }

    protected void executeDelete() {
        ArrayList<DownloadJob> downloadList = ChaoJiShiPinApplication.getInstatnce()
                .getDownloadManager().getAllDownloads();
//		LogUtils.d("dd","删除之前---"+downloadList.size()+"");
        mDownloadManager.deregisterDownloadObserver(this);
        SparseArray<DownloadJob> jobs = adapter.getList();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int pos = 0; pos < jobs.size(); pos++) {
            if (jobs.valueAt(pos).getCheck()) {
                DownloadJob job = jobs.valueAt(pos);
                if (index == -1) {
                    keys.add(job.getEntity().getAddTime());
                } else {
                    keys.add(job.getIndex());
                }

            }
        }
        for (int i : keys) {
            DownloadJob job = jobs.get(i);
            mDownloadManager.deleteDownload(job);
            jobs.delete(i);
            adapter.deletedNum--;
        }
        if (jobs.size() == 0 && index != -1) {
            SparseArray<DownloadFolderJob> folderJobs = mDownloadManager.getProvider().getFolderJobs();
            int key = folderJobs.keyAt(index);
            mDownloadManager.getDownloadFolderJobs().delete(key);
        }
        ArrayList<DownloadJob> downloadList2 = ChaoJiShiPinApplication.getInstatnce()
                .getDownloadManager().getAllDownloads();
        mDownloadManager.startNextTask();
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

    private void toggleConinuePlayButton(String mid) {
        // 如果播放历史中存在本剧集中的影片
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
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

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
//        for(int i=0;i<adapter.getCount();i++){
//            DownloadJobAdapter.ViewHolder holder = adapter.get
//        }
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

    public void updatebuttons() {
        if (index == -1) {
            //可编辑
            if (adapter.deleteState) {
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
                int downloadtype = downloadType();
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
    public int downloadType() {
        ArrayList<DownloadJob> jobs = mDownloadManager.getProvider().getQueuedDownloads();
        int type = -1;
        boolean falg_stop = false;
        boolean flag_downloading = false;
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getStatus() == DownloadJob.DOWNLOADING || jobs.get(i).getStatus() == DownloadJob.WAITING) {
                flag_downloading = true;
            } else if (jobs.get(i).getStatus() == DownloadJob.PAUSE || jobs.get(i).getStatus() == DownloadJob.NO_USER_PAUSE) {
                falg_stop = true;
            }
        }
        //有暂停也有下载
        if (falg_stop && flag_downloading) {
            return 0;
            //全部暂停
        } else if (falg_stop && !flag_downloading) {
            return -1;
            //全部开始
        } else {
            return 1;
        }
    }
}
