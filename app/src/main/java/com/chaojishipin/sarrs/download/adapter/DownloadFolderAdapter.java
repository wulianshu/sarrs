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
import com.chaojishipin.sarrs.utils.DataUtils;
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
        mSettingManage = new SettingManage(mContext);
        if (null != mContext) {
            mSettingSharePreference = mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
            mSettingPreferenceEditor = mSettingSharePreference.edit();
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
            displayImage(job.getEntity().getImage(), holder.downloadPoster, R.drawable.sarrs_main_default);

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

            holder.downloadName.setText(job.getEntity().getFolderName());
            holder.downloadLength.setText(getTotalSize(folderJob.getDownloadJobs()));
            deleteStateHandler(holder, folderJob);
            holder.videocounts.setVisibility(View.VISIBLE);
            holder.videocounts.setText(folderJob.getDownloadJobs().size() + "个视频");
        } else {
            DownloadJob job = folderJob.getDownloadJobs().valueAt(0);
            holder.downloadName.setText(job.getEntity().getDisplayName());
            holder.downloadLength.setText(DownloadUtils.getDownloadedSize(job.getDownloadedSize()) + "MB/" + DownloadUtils.getTotalSize(job.getTotalSize()) + "MB");
            if (job.getStatus() == DownloadJob.COMPLETE || job.getProgress() == 100) {
                Log.i("iswatch", "check ifwatch " + job.getEntity().getIfWatch());
                if (!"true".equals(job.getEntity().getIfWatch())) {
                    holder.ifwatch.setVisibility(View.VISIBLE);
                } else {
                    holder.ifwatch.setVisibility(View.GONE);
                }
                downloadJobCompleted(holder, job.getEntity(), folderJob);
            }
        }

        return convertView;
    }

    public void start(DownloadJob job, ViewHolder holder) {
        if (!job.isCurrentPathExist()) {
            sdcardChangeDialog(mContext, job, holder);
        } else {
            if (null != job && null != job.getEntity()) {
                if (TextUtils.isEmpty(job.getEntity().getPath()) && !TextUtils.isEmpty(job.getmDestination())) {
                    job.getEntity().setPath(job.getmDestination());
                }
                DataUtils.getInstance().startDownload(job);
            }
        }
    }

    private void deleteStateHandler(ViewHolder holder, DownloadFolderJob job) {
        if (deleteState) {
            holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
            if (job.isCheck()) {
                holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_red_bg));
            } else {
                holder.btnDelete.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
            }
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    //下载完成的页面显示与处理
    private void downloadJobCompleted(final ViewHolder holder, DownloadEntity entity, DownloadFolderJob job) {
        holder.videocounts.setText("1个视频");
        ImageLoader.getInstance().displayImage(entity.getImage(),holder.downloadPoster);
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
        deleteStateHandler(holder, job);
    }

    // 设置编辑删除项
    public void setDeleteItem(int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ToggleButton deleteBtn = (ToggleButton) viewHolder.btnDelete;

        DownloadFolderJob folderJob = mList.valueAt(position);
        int size = folderJob.getDownloadJobs().size();
        if (folderJob.isCheck()) {
            // 选中-->取消
            deleteBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_white_bg));
            deletedNum -= size;
            folderJob.setCheck(false);
        } else {
            // 取消-->选中
            deleteBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.radiobutton_red_bg));
            deletedNum += size;
            folderJob.setCheck(true);
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
                                DownloadJob job = dJob;
                                job.setmDestination(DownloadHelper.getDownloadPath());
                                job.getEntity().setPath(DownloadHelper.getDownloadPath());
                                DataUtils.getInstance().updateDownloadEntity(job);
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
