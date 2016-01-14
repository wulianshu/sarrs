package com.chaojishipin.sarrs.download.adapter;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.SparseArrayAdapter;
import com.chaojishipin.sarrs.config.SettingManage;
import com.chaojishipin.sarrs.download.activity.DownloadJobActivity;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuLayout;
import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.call.Position;

import java.util.ArrayList;


public class DownloadJobAdapter extends SparseArrayAdapter<DownloadJob> {
    public boolean deleteState;
    public ArrayList<Boolean> mChecked;
    public int deletedNum;
    private DownloadJobActivity DownloadJobActivity;
    private LayoutInflater mInflater;
    private int mFromWhere;
//	private String taskstate;
//    public ContainSizeManager mSizeManager;
    //获取是否允许3g下载的用户选择数据
    private SettingManage mSettingManage;
    private SharedPreferences mSettingSharePreference;
    private Editor mSettingPreferenceEditor;
    private boolean isshow_radiobutton;
    public View downloadingview = null;

    //	private int oldsize=0;
    public void setIsshowRadiobutton(boolean isshow_radiobutton) {
        this.isshow_radiobutton = isshow_radiobutton;
    }


    public DownloadJobAdapter(SparseArray<DownloadJob> mList, DownloadJobActivity mContext, int fromWhere) {
        super(mList, mContext);
        DownloadJobActivity = mContext;
        mInflater = mContext.getLayoutInflater();
        mChecked = new ArrayList<Boolean>();
        mFromWhere = fromWhere;
        for (int i = 0; i < ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getAllDownloads().size(); i++) {
            mChecked.add(false);
        }
        mSettingManage = new SettingManage(mContext);
        mSettingSharePreference = mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
        mSettingPreferenceEditor = mSettingSharePreference.edit();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        DownloadJob job = null;
        //有数据 增加和删除的时候更新UI
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.download_row2, null);
            holder.downloadName = (TextView) convertView.findViewById(R.id.small_poster_title);
            holder.downloadLength = (TextView) convertView.findViewById(R.id.tv_download_length);
            holder.progressText = (TextView) convertView.findViewById(R.id.tv_download_status);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.ProgressBar);
            holder.imageView = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            holder.btnDelete = (ToggleButton) convertView.findViewById(R.id.download_radiobtton);
            holder.ifwatch = (ImageView) convertView.findViewById(R.id.ifwatch);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.position = position;
        }
        if (isshow_radiobutton) {
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
//        if (isShowSwipe) {
//            DownloadJobActivity.getmListView().setIsOpenStatus(false);
//        } else
//            DownloadJobActivity.getmListView().setIsOpenStatus(true);
        if (null != mList) {
            if (position < mList.size() && mList.valueAt(position) instanceof DownloadJob) {
                job = mList.valueAt(position);
            }
            if (null != job) {
                DownloadEntity entity = job.getEntity();
               if(job.getStatus() == DownloadJob.DOWNLOADING){
                   downloadingview = convertView;
               }
                holder.progressText.setText(job.getRate());
                if (null != entity) {
                    if (holder.downloadName.getText() == null
                            || holder.downloadName.getText().length() == 0
                            || !entity.getDisplayName().equals(holder.downloadName.getText())) {
                        holder.downloadName.setText(entity.getDisplayName());
//                      ImageLoader.getInstance().displayImage(entity.getImage(), holder.imageView);
                        final ViewHolder finalHolder = holder;
                        LogUtil.e("DownloadJobAdapter","image"+entity.getImage());
                        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                                .showImageOnLoading(R.drawable.sarrs_main_default)
                                .build();
                        ImageLoader.getInstance().displayImage(entity.getImage(), holder.imageView,options1);
                        holder.progressBar.setMax(100);
                        holder.progressBar.setProgress(job.getProgress());
                        if (mFromWhere == -1) {//是下载中界面
                            holder.downloadName.setTextColor(mContext.getResources().getColor(R.color.color_444444));
                            holder.downloadName.setTextSize(16);
                        } else {
                            holder.downloadName.setTextColor(mContext.getResources().getColor(R.color.color_444444));
                            holder.downloadName.setTextSize(16);
                        }
                    }

                }

                if (job.getStatus() == DownloadJob.DOWNLOADING || job.getStatus() == DownloadJob.COMPLETE)
                {
                    if (DownloadInfo.MP4.equals(entity.getDownloadType())) {

                        holder.downloadLength.setText(DownloadUtils.getDownloadedSize(job.getDownloadedSize()) + "M/" + DownloadUtils.getDownloadedSize(job.getTotalSize()) + "M");

                    } else {
                        holder.downloadLength.setText(mContext.getString(R.string.compulate_size));
                    }
                    holder.downloadLength.setVisibility(View.VISIBLE);
                }else {
                    holder.downloadLength.setVisibility(View.INVISIBLE);
                }
                if (mFromWhere == -1) {//是下载中界面
                    holder.downloadLength.setTextColor(mContext.getResources().getColor(R.color.color_444444));
                    holder.downloadLength.setTextSize(12);
                    holder.ifwatch.setVisibility(View.GONE);
                } else {
                    Log.i("iswatch", "job postion is " + position + " name is " + entity.getDisplayName() + " ifwatch " + entity.getIfWatch());
                    if (!"true".equals(entity.getIfWatch())) {//不是下载中界面,并且没有点击过
                        holder.ifwatch.setVisibility(View.VISIBLE);
                    } else {
                        holder.ifwatch.setVisibility(View.GONE);
                    }
                    holder.downloadLength.setVisibility(View.VISIBLE);
                }

                if (job.getProgress() == 100) {
                    downloadJobCompleted(holder, entity, position);
//					taskstate = "finished";
                    String name = job.getEntity().getMedianame();
                    String albumID = job.getEntity().getMid();
                    String vid = job.getEntity().getId();
                } else {
                    downloadJobUnCompleted(holder, job, position);
                    switch (job.getStatus()) {
                        case DownloadJob.INIT:
                            break;
                        case DownloadJob.DOWNLOADING:
//					     	holder.downloadControl.setBackgroundResource(R.drawable.download_pausebtn_selector);
                            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download));
                            break;
                        case DownloadJob.NO_USER_PAUSE:
                            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                            holder.progressText.setText(mContext.getResources().getString(R.string.download_faile));
                            showExceptionPause(holder, job);
                            break;
                        case DownloadJob.PAUSE:
                            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                            holder.progressText.setText(mContext.getResources().getString(R.string.already_pause_download));
                            showExceptionPause(holder, job);
                            if (job.getExceptionType() == DownloadJob.DOWNLOAD_FAILUER) {
//							taskstate = "failed";
                            } else {

                            }
                            break;
                        case DownloadJob.WAITING:
                            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_style_download_pause));
                            holder.progressText.setText("等待中");
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return convertView;
    }

    private void downloadJobUnCompleted(ViewHolder holder, DownloadJob job, int position) {
        holder.progressText.setVisibility(View.VISIBLE);
//		holder.downloadControl.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(job.getProgress());
//		System.out.println("rate2:"+job.getRate());
//		holder.progressText.setText(job.getRate());
        deleteStateHandler(holder, position);
    }

    //下载完成的页面显示与处理
    private void downloadJobCompleted(ViewHolder holder, DownloadEntity entity, int position) {
        holder.progressBar.setVisibility(View.GONE);
        holder.progressText.setVisibility(View.GONE);
        if (null != entity) {
            if (DownloadHelper.getDownloadedFileSize(entity, entity.getPath()) == 0) {
                holder.downloadLength.setText(mContext.getString(R.string.file_has_been_removed));
            } else {
//				holder.downloadLength.setText(DownloadUtils.getTotalSize(DownloadHelper.getDownloadedFileSize(entity,entity.getPath()))+"MB"+"  完成");
                if (DownloadInfo.M3U8.equals(entity.getDownloadType())) {
                    if (entity.getFileSize() < 1024 * 1024) {
                        entity.setFileSize(DownloadHelper.getDownloadedFileSize(entity, entity.getPath()));
                    }
                    holder.downloadLength.setText(DownloadUtils.getTotalSize(entity.getFileSize()) + "MB");
//                    holder.downloadLength.setText(DownloadUtils.getTotalSize(entity.getFileSize()) + "M" + "/" + entity.getFileSize() + "M");
                } else {
                    holder.downloadLength.setText(DownloadUtils.getTotalSize(DownloadHelper.getDownloadedFileSize(entity, entity.getPath())) + "MB");
                }
            }
        }
        deleteStateHandler(holder, position);
//		holder.downloadControl.setVisibility(View.GONE);
    }

    private class DeleteListener implements OnClickListener {
        public int mPosition;

        public DeleteListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            ToggleButton deleteBtn = (ToggleButton) v;
            if (deleteBtn.isChecked()) {
                deletedNum++;
            } else {
                deletedNum--;
            }
            mChecked.set(mPosition, deleteBtn.isChecked());

            DownloadJobActivity.setTitle();
        }
    }

    // 设置下载选中项
    public void setDeleteItem(int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ToggleButton deleteBtn = (ToggleButton) viewHolder.btnDelete;

        if (null != mList) {
            DownloadJob job = null;
            if (position < mList.size() && mList.valueAt(position) instanceof DownloadJob) {
                job = mList.valueAt(position);
            }

            if (null != job) {
                if (job.getCheck()) {
//					deleteBtn.setBackgroundDrawable(DownloadJobActivity.getResources().getDrawable(R.drawable.pic_localvideo_uncheck));
                    deletedNum--;
                    job.setCheck(false);
                } else {
//					deleteBtn.setBackgroundDrawable(DownloadJobActivity.getResources().getDrawable(R.drawable.pic_localvideo_checked));
                    deletedNum++;
                    job.setCheck(true);
                }
            }

        }

        DownloadJobActivity.setTitle();

    }


    public class DownloadControlListener implements AdapterView.OnItemClickListener {
        private DownloadJob job;

        public DownloadJob getJob() {
            return job;
        }

        public void setJob(DownloadJob job) {
            this.job = job;
        }

        public ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(ViewHolder holder) {
            this.holder = holder;
        }

        private ViewHolder holder;

        private void popIfContinueDownloadDialog() {
            if (NetworkUtil.reportNetType(DownloadJobActivity) == 2 && !job.isDownloadcan3g()) {
                if (job.isCurrentPathExist()) {
//					mP2pHelper.stopDownloadTask(job);
                    checkIfContinueDownloadDialog();
                }
            } else {
                start(job, holder);
            }
        }

        private void checkIfContinueDownloadDialog() {
            Builder customBuilder = new Builder(DownloadJobActivity);
            customBuilder
                    .setTitle(R.string.tip)
                    .setMessage(R.string.wireless_tip)
                    .setPositiveButton(R.string.continue_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
//			                    	job.setUserPauseWhen3G(false);
                                    mSettingManage.setToggleButtonPreference(true, mSettingPreferenceEditor, SettingManage.IS_DOWNLOAD_CAN_3G);
                                    start(job, holder);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(R.string.pause_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
//			                    	job.setUserPauseWhen3G(true);
                                    //start(job,holder);
                                    dialog.dismiss();
                                }
                            })
                    .setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//								job.setUserPauseWhen3G(true);
//								start(job,holder);
                                dialog.dismiss();
                            }
                            return false;
                        }
                    });
            Dialog dialog = customBuilder.create();
            dialog.show();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            job = DownloadJobActivity.getJobs().get(i);
            holder = (ViewHolder) view.getTag();
//			taskstate = "";
            switch (job.getStatus()) {
                case DownloadJob.NO_USER_PAUSE:
                case DownloadJob.PAUSE:
                    if (ContainSizeManager.getInstance().getFreeSize() > Utils.SDCARD_MINSIZE) {//sd卡容量大于500m，可以添加
                        popIfContinueDownloadDialog();
//						taskstate = "download";
                    } else {
                        ToastUtil.showShortToast(DownloadJobActivity, R.string.sdcard_nospace);
                    }
                    break;
                case DownloadJob.WAITING:
//				holder.downloadControl.setBackgroundResource(R.drawable.download_controlbtn_selector);
                    job.cancel();
                    holder.progressText.setText("已暂停");
                    break;
                case DownloadJob.DOWNLOADING:
//				holder.downloadControl.setBackgroundResource(R.drawable.download_controlbtn_selector);
                    job.pauseByUser();
                    holder.progressText.setText("已暂停");
//					taskstate = "pausedbyuser";
                    break;
            }
            String name = job.getEntity().getMedianame();
            String albumID = job.getEntity().getMid();
            String vid = job.getEntity().getId();
//			UmengEventPosterer.postDownLoadStateClick(name,albumID,vid,taskstate);

        }
    }

    private void deleteStateHandler(ViewHolder holder, int position) {
        if (deleteState) {

            if (null != mList) {
                DownloadJob job = null;
                if (position < mList.size() && mList.valueAt(position) instanceof DownloadJob) {
                    job = mList.valueAt(position);
                }
                if (null != job) {
                    if (job.getCheck()) {
                        holder.btnDelete.setBackgroundDrawable(DownloadJobActivity.getResources().getDrawable(R.drawable.radiobutton_red_bg));
                    } else {
                        holder.btnDelete.setBackgroundDrawable(DownloadJobActivity.getResources().getDrawable(R.drawable.radiobutton_white_bg));
                    }
                }
            }

        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    public void start(DownloadJob job, ViewHolder holder) {
        if (!job.isCurrentPathExist()) {
            sdcardChangeDialog(mContext, job, holder);
        } else {
            if (null != job && null != job.getEntity()) {
                if (TextUtils.isEmpty(job.getEntity().getPath()) && !TextUtils.isEmpty(job.getmDestination())) {
                    job.getEntity().setPath(job.getmDestination());
                }
                job.start();
                if (null != holder) {
//					holder.downloadControl.setBackgroundResource(R.drawable.download_pausebtn_selector);
                    holder.progressText.setText("0.0KB/s");
                }
            }
        }
    }

    private void showExceptionPause(final ViewHolder holder,
                                    final DownloadJob job) {

        if (job.getExceptionType() == DownloadJob.MOBILE)
            holder.progressText.setText("  " + mContext.getString(R.string.already_pause_download));
        if (job.getExceptionType() == DownloadJob.NET_SHUT_DOWN)
            holder.progressText.setText("  " + mContext.getString(R.string.already_pause_download));
        if (job.getExceptionType() == DownloadJob.NO_SD)
            holder.progressText.setText("  " + mContext.getString(R.string.no_sdcard_added));
        if (job.getExceptionType() == DownloadJob.SD_SPACE_FULL)
            holder.progressText.setText("  " + mContext.getString(R.string.no_space_tip));
        if (job.getExceptionType() == DownloadJob.FILE_NOT_FOUND)
            holder.progressText.setText("  " + mContext.getString(R.string.no_sdcard_added));
        if (job.getExceptionType() == DownloadJob.DOWNLOAD_FAILUER)
            holder.progressText.setText("  " + mContext.getString(R.string.download_faile));
    }

    public class ViewHolder {
        ImageView ifwatch;//是否观看过

        public ImageView getIfwatch() {
            return ifwatch;
        }

        public void setIfwatch(ImageView ifwatch) {
            this.ifwatch = ifwatch;
        }

        TextView downloadName; //文件名,如xxxx第几集
        TextView downloadLength;//大小：15M/88M
        TextView progressText;//网速/下载中/暂停中/等待中/
        ProgressBar progressBar;//进度条
        EqualRatioImageView imageView;
        //		CheckBox  download_checkbox;
        ToggleButton btnDelete;//删除按钮
        int position;

        public TextView getDownloadName() {
            return downloadName;
        }

        public void setDownloadName(TextView downloadName) {
            this.downloadName = downloadName;
        }

        public TextView getDownloadLength() {
            return downloadLength;
        }

        public void setDownloadLength(TextView downloadLength) {
            this.downloadLength = downloadLength;
        }

        public TextView getProgressText() {
            return progressText;
        }

        public void setProgressText(TextView progressText) {
            this.progressText = progressText;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        public EqualRatioImageView getImageView() {
            return imageView;
        }

        public void setImageView(EqualRatioImageView imageView) {
            this.imageView = imageView;
        }

        public ToggleButton getBtnDelete() {
            return btnDelete;
        }

        public void setBtnDelete(ToggleButton btnDelete) {
            this.btnDelete = btnDelete;
        }
//		public CheckBox getDownload_checkbox() {
//			return download_checkbox;
//		}
//
//		public void setDownload_checkbox(CheckBox download_checkbox) {
//			this.download_checkbox = download_checkbox;
//		}
    }

    public void sdcardChangeDialog(Context context, final DownloadJob dJob, final ViewHolder holder) {
        if (null == context) {
            return;
        }
        String newPath = DownloadHelper.getDownloadPath();
        if (!StringUtil.isEmpty(newPath) && newPath.contains("/" + Utils.getDownLoadFolder())) {
            newPath = newPath.substring(0, newPath.indexOf("/" + Utils.getDownLoadFolder()));
        }
        if (newPath.equals(dJob.getmDestination())) {
            newPath = Utils.SAVE_FILE_PATH_DIRECTORY;
            newPath = newPath.substring(0, newPath.indexOf("/" + Utils.getDownLoadFolder()));
        }
        Builder customBuilder = new Builder(context);
        customBuilder
                .setTitle(R.string.tip)
                .setMessage(context.getString(R.string.sd_change_message_before) + dJob.getmDestination()
                        + context.getString(R.string.sd_change_message_middle)
                        + newPath + context.getString(R.string.sd_change_message_after))
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
//		                    	mP2pHelper.removeP2pFileFromDisk(dJob);
//		                    	mP2pHelper.removeP2pTaskFromList(dJob);
                                DownloadJob job = dJob;
//		                    	if(mP2pHelper.isP2pDownLoad(dJob.getEntity())) {
//		                    		String p2pPath = mP2pHelper.getP2PDownloadPath(dJob.getEntity());
//		                    		job.setmDestination(p2pPath);
//			                    	job.getEntity().setPath(p2pPath);
//		                    	} else {
                                job.setmDestination(DownloadHelper.getDownloadPath());
                                job.getEntity().setPath(DownloadHelper.getDownloadPath());
//		                    	}
                                ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getProvider().updateDownloadEntity(job);
                                start(job, holder);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                .setOnKeyListener(new OnKeyListener() {
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

    class CheckBoxClickListener implements OnClickListener {
        ViewHolder viewHolder;
        int position;

        public CheckBoxClickListener(int position, ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (viewHolder.btnDelete.isChecked()) {
                mChecked.set(position, true);
            } else {
                mChecked.set(position, false);
            }
        }
    }
}
