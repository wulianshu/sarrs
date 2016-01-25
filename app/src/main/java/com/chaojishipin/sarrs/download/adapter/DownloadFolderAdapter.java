package com.chaojishipin.sarrs.download.adapter;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.SparseArrayAdapter;
import com.chaojishipin.sarrs.config.SettingManage;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.fragment.DownloadFragment;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


public class DownloadFolderAdapter extends
        SparseArrayAdapter<DownloadFolderJob> {

    public static String TAG = "DownloadFolderAdapter";

    private boolean deleteState;
    public ArrayList<Boolean> mChecked;
    public int deletedNum;
    //	public DownloadActivity mContext;
    public Activity mContext;
    public DownloadFragment fragment;
    //获取是否允许3g下载的用户选择数据
    private SettingManage mSettingManage;
    private SharedPreferences mSettingSharePreference;
    private Editor mSettingPreferenceEditor;
    //	private P2pHelper mP2pHelper;

    public DownloadFolderAdapter(SparseArray<DownloadFolderJob> list,
                                 Activity context) {
        super(list, context);
        mContext = context;
//		mP2pHelper = P2pHelper.getInstance();
        initChecked();
        mSettingManage = new SettingManage(mContext);
        if (null != mContext) {
            mSettingSharePreference = mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
            mSettingPreferenceEditor = mSettingSharePreference.edit();
        }
    }

    private void initChecked() {
        mChecked = new ArrayList<Boolean>();
        for (int i = 0; i < ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getAllDownloads().size(); i++) {
            mChecked.add(false);
        }
    }

    @Override
    public View getView(int position, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.download_item, null);
            holder.downloadPoster = (EqualRatioImageView) convertView.findViewById(R.id.download_poster);
            holder.downloadName = (TextView) convertView.findViewById(R.id.downloadName);
            holder.downloadLength = (TextView) convertView.findViewById(R.id.downloadLength);
            holder.btnDelete = (ToggleButton) convertView.findViewById(R.id.edit_delete);
            holder.videocounts = (TextView) convertView.findViewById(R.id.download_num);
            holder.ifwatch = (ImageView) convertView.findViewById(R.id.ifwatch);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.i("getView", "position is " + position);

//	    DownloadFolderJob folderJob = mList.valueAt(position);
        DownloadFolderJob folderJob = null;
        if (mList.valueAt(position) instanceof DownloadFolderJob) {
            folderJob = mList.valueAt(position);
//            holder.divider.setVisibility(View.VISIBLE);
        } else {
//            holder.divider.setVisibility(View.GONE);
            return convertView;
        }

        int size = folderJob.getDownloadJobs().size();
        if (size > 1) {
            DownloadJob job = folderJob.getDownloadJobs().valueAt(0);
//            ImageLoader.getInstance().displayImage(job.getEntity().getImage(), holder.downloadPoster);

            DisplayImageOptions options1= new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                    .showImageForEmptyUri(R.drawable.sarrs_main_default)
                    .showImageOnLoading(R.drawable.sarrs_main_default)
                    .build();
            ImageLoader.getInstance().displayImage(job.getEntity().getImage(), holder.downloadPoster,options1);

            DownloadJob jobObj = null;
            boolean isAllWatch = false;
            for (int i = 0; i < size; i++) {
                jobObj = folderJob.getDownloadJobs().valueAt(i);
                Log.i("iswatch", "folder i is " + i + " name is " + jobObj.getEntity().getDisplayName() + " ifwatch " + jobObj.getEntity().getIfWatch());
                if ("true".equals(jobObj.getEntity().getIfWatch()))
                    isAllWatch = true;
                else {
                    isAllWatch = false;
                    break;
                }
                Log.i("iswatch", " 1" + isAllWatch);
            }
            Log.i("iswatch", " 2" + isAllWatch);
            if (isAllWatch)
                holder.ifwatch.setVisibility(View.GONE);
            else
                holder.ifwatch.setVisibility(View.VISIBLE);

//            holder.folderBg.setVisibility(View.VISIBLE);
            holder.downloadName.setText(job.getEntity().getFolderName());
            holder.downloadLength.setText(getTotalSize(folderJob.getDownloadJobs()));
//            holder.progressBar.setVisibility(View.GONE);
//            holder.progressText.setVisibility(View.GONE);
            deleteStateHandler(holder, position);
//            holder.downloadControl.setVisibility(View.GONE);
//            holder.totalvideos.setVisibility(View.VISIBLE);
            holder.videocounts.setVisibility(View.VISIBLE);
            holder.videocounts.setText(folderJob.getDownloadJobs().size() + "个视频");
        } else {
            DownloadJob job = folderJob.getDownloadJobs().valueAt(0);
//            holder.folderBg.setVisibility(View.GONE);
            holder.downloadName.setText(job.getEntity().getDisplayName());
            holder.downloadLength.setText(DownloadUtils.getDownloadedSize(job.getDownloadedSize()) + "MB/" + DownloadUtils.getTotalSize(job.getTotalSize()) + "MB");
            if (job.getProgress() == 100) {
                Log.i("iswatch", "check ifwatch " + job.getEntity().getIfWatch());
                if (!"true".equals(job.getEntity().getIfWatch())) {
                    holder.ifwatch.setVisibility(View.VISIBLE);
                } else {
                    holder.ifwatch.setVisibility(View.GONE);
                }
                downloadJobCompleted(holder, job.getEntity(), position);
            }
//            else {
//                downloadJobUnCompleted(holder, job, position);
//                switch (job.getStatus()) {
//                    case DownloadJob.INIT:
//                        break;
//                    case DownloadJob.DOWNLOADING:
//                        holder.downloadControl.setBackgroundResource(R.drawable.download_pausebtn_selector);
//                        break;
//                    case DownloadJob.PAUSE:
//                    case DownloadJob.NO_USER_PAUSE:
//                        holder.progressText.setText("  暂停中...");
//                        showExceptionPause(holder, job);
//                        holder.downloadControl.setBackgroundResource(R.drawable.download_controlbtn_selector);
//                        break;
//                    case DownloadJob.WAITING:
//                        holder.progressText.setText("  等待中...");
//                        holder.downloadControl.setBackgroundResource(R.drawable.download_waitbtn_selector);
//                        if (job.getProgress() == 0)
//                            holder.downloadLength.setText("0MB/" + DownloadUtils.getTotalSize(job.getTotalSize()) + "MB");
//                        break;
//                    default:
//                        break;
//                }
//                holder.downloadControl.setOnClickListener(new DownloadControlListener(job, holder));
//            }
        }

//		holder.btnDelete.setOnClickListener(new DeleteListener(position));
        return convertView;
    }

    private class DownloadControlListener implements OnClickListener {
        private final DownloadJob job;
        private final ViewHolder holder;

        private DownloadControlListener(DownloadJob job, ViewHolder holder) {
            this.job = job;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            switch (job.getStatus()) {
                case DownloadJob.NO_USER_PAUSE:
                case DownloadJob.PAUSE:
                    popIfContinueDownloadDialog();
                    break;
                case DownloadJob.WAITING:
//                    holder.downloadControl.setBackgroundResource(R.drawable.download_controlbtn_selector);
                    job.cancel();
//                    holder.progressText.setText("  暂停中...");
                    break;
                case DownloadJob.DOWNLOADING:
//                    holder.downloadControl.setBackgroundResource(R.drawable.download_controlbtn_selector);
                    job.pauseByUser();
//                    holder.progressText.setText("  暂停中...");
                    break;
            }
        }

        private void popIfContinueDownloadDialog() {
            if (NetworkUtil.reportNetType(mContext) == 2 && !job.isDownloadcan3g()) {
                if (job.isCurrentPathExist()) {
//					mP2pHelper.stopDownloadTask(job);
                    checkIfContinueDownloadDialog();
                }
            } else {
                start(job, holder);
            }
        }

        private void checkIfContinueDownloadDialog() {
            Builder customBuilder = new Builder(mContext);
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
//			                        job.setUserPauseWhen3G(true);
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
//                    holder.downloadControl.setBackgroundResource(R.drawable.download_pausebtn_selector);
//                    holder.progressText.setText("0.0KB/s");
                }
            }
        }
    }


    //下载未完成的页面显示与处理
    private void downloadJobUnCompleted(ViewHolder holder, DownloadJob job, int position) {
//        holder.progressText.setVisibility(View.VISIBLE);
//        holder.downloadControl.setVisibility(View.VISIBLE);
//        holder.progressBar.setVisibility(View.VISIBLE);
//        holder.progressBar.setMax(100);
//        holder.progressBar.setProgress(job.getProgress());
//        holder.progressText.setText(job.getRate());
        deleteStateHandler(holder, position);
    }

    private void deleteStateHandler(ViewHolder holder, int position) {
        if (deleteState) {
            holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
            if (position >= mChecked.size()) {
                for (int i = mChecked.size() - 1; i < position; i++) {
                    mChecked.add(false);
                }
            }
            if (position < mChecked.size()) {
                if (mChecked.get(position)) {
                    holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_red_bg));
                } else {
                    holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
                }
            }
            holder.btnDelete.setVisibility(View.VISIBLE);
//            holder.downloadControl.setVisibility(View.GONE);
//			holder.rlDownLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//			holder.rlDownLayout.setBackgroundResource(R.drawable.video_listview_bg);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
//            holder.downloadControl.setVisibility(View.VISIBLE);
//			holder.rlDownLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//			holder.rlDownLayout.setBackgroundResource(R.drawable.download_folder_selector);
        }
    }

    //下载完成的页面显示与处理
    private void downloadJobCompleted(final ViewHolder holder, DownloadEntity entity, int position) {
//        holder.progressBar.setVisibility(View.GONE);
//        holder.progressText.setVisibility(View.GONE);
        holder.videocounts.setText("1个视频");
//        Log.i("downloadJobCompleted", "position is " + position + "and image url is " + entity.getImage());
//        ImageLoader.getInstance().displayImage(entity.getImage(), holder.downloadPoster);
        ImageLoader.getInstance().displayImage(entity.getImage(),holder.downloadPoster);
//        ImageCacheManager.loadImage(entity.getImage(), new com.android.volley.toolbox.ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
//                if (response.getBitmap() != null) {
//                    holder.downloadPoster.setImageBitmap(response.getBitmap());
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                holder.downloadPoster.setImageResource(R.drawable.search_default_poster);
//            }
//        });
//        ImageLoader.getInstance().displayImage(entity.getImage(), holder.downloadPoster, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String s, View view) {
//            }
//
//            @Override
//            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                holder.downloadPoster.setImageResource(R.drawable.search_default_poster);
//            }
//
//            @Override
//            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                ImageLoader.getInstance().displayImage(s, holder.downloadPoster);
//            }
//
//            @Override
//            public void onLoadingCancelled(String s, View view) {
//
//            }
//        });
        if (DownloadHelper.getDownloadedFileSize(entity, entity.getPath()) == 0) {
            holder.videocounts.setVisibility(View.GONE);
            holder.ifwatch.setVisibility(View.GONE);
            holder.downloadLength.setText(mContext.getString(R.string.file_has_been_removed));
        } else {
            holder.videocounts.setVisibility(View.VISIBLE);
            if (DownloadInfo.M3U8.equals(entity.getDownloadType())) {
                if (entity.getFileSize() < 1024 * 1024) {
                    entity.setFileSize(DownloadHelper.getDownloadedFileSize(entity, entity.getPath()));
                }
                holder.downloadLength.setText(DownloadUtils.getTotalSize(entity.getFileSize()) + "MB");
            } else {
                holder.downloadLength.setText(DownloadUtils.getTotalSize(DownloadHelper.getDownloadedFileSize(entity, entity.getPath())) + "MB");
            }
        }
        deleteStateHandler(holder, position);
//        holder.downloadControl.setVisibility(View.GONE);
    }

    private void showExceptionPause(final ViewHolder holder,
                                    final DownloadJob job) {
//        if (job.getExceptionType() == DownloadJob.NET_SHUT_DOWN)
//            holder.progressText.setText("  " + mContext.getString(R.string.net_shutdown));
//        if (job.getExceptionType() == DownloadJob.NO_SD)
//            holder.progressText.setText("  " + mContext.getString(R.string.no_sdcard_added));
//        if (job.getExceptionType() == DownloadJob.SD_SPACE_FULL)
//            holder.progressText.setText("  " + mContext.getString(R.string.no_space_tip));
//        if (job.getExceptionType() == DownloadJob.FILE_NOT_FOUND)
//            holder.progressText.setText("  " + mContext.getString(R.string.no_sdcard_added));
    }

    class DeleteListener implements OnClickListener {

        public int mPosition;

        public DeleteListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            ToggleButton deleteBtn = (ToggleButton) v;
            DownloadFolderJob folderJob = mList.valueAt(mPosition);
            int size = folderJob.getDownloadJobs().size();
            if (deleteBtn.isChecked()) {
                deletedNum += size;
            } else {
                deletedNum -= size;
            }
            mChecked.set(mPosition, deleteBtn.isChecked());
            if (deletedNum == 0) {
                fragment.restoreDeleteView();
            } else if (deletedNum > 0) {
                showUserSelecedItem();
            }
        }
    }

    // 设置编辑删除项
    public void setDeleteItem(int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ToggleButton deleteBtn = (ToggleButton) viewHolder.btnDelete;

        DownloadFolderJob folderJob = mList.valueAt(position);
        int size = folderJob.getDownloadJobs().size();
        if (mChecked.get(position)) {
            // 选中-->取消
            deleteBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
            deletedNum -= size;
            mChecked.set(position, false);
        } else {
            // 取消-->选中
            deleteBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_red_bg));
            deletedNum += size;
            mChecked.set(position, true);
        }
        if (deletedNum == 0) {
            fragment.restoreDeleteView();
        } else if (deletedNum > 0) {
            showUserSelecedItem();
        }
        fragment.checkUserSelectStatus();
    }

    static class ViewHolder {
        EqualRatioImageView downloadPoster;
        TextView downloadName; //文件名,如xxxx第几集
        TextView downloadLength;//大小：15M/88M
        ToggleButton btnDelete;//视频左侧删除按钮
        TextView videocounts; // 视频数量
        ImageView ifwatch;//是否观看过
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

    public boolean isDeleteState() {
        return deleteState;
    }

    public void setDeleteState(boolean deleteState) {
        this.deleteState = deleteState;
    }

    public void showUserSelecedItem() {
        fragment.deleteView(deletedNum);
    }

    private String getTotalSize(SparseArray<DownloadJob> jobs) {
        long size = 0;
        for (int i = 0; i < jobs.size(); i++) {
            DownloadEntity entity = jobs.valueAt(i).getEntity();
            if (DownloadInfo.M3U8.equals(entity.getDownloadType())) {
                if (entity.getFileSize() < 1024 * 1024) {
                    entity.setFileSize(DownloadHelper.getDownloadedFileSize(entity, entity.getPath()));
                }
                size += entity.getFileSize();
            } else {
                size += DownloadHelper.getDownloadedFileSize(entity, entity.getPath());
            }
        }
        return (DownloadUtils.getTotalSize(size) + "MB");
    }

}
