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
import com.chaojishipin.sarrs.utils.DataUtils;
import com.mylib.download.activity.DownloadJobActivity;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadHelper;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class DownloadJobAdapter extends SparseArrayAdapter<DownloadJob> {
//    public ArrayList<Boolean> mChecked;
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

    public void setEditable(boolean bo){
        isshow_radiobutton = bo;
    }

    public boolean isEditable(){
        return isshow_radiobutton;
    }

    public DownloadJobAdapter(SparseArray<DownloadJob> mList, DownloadJobActivity mContext, int fromWhere) {
        super(mList, mContext);
        DownloadJobActivity = mContext;
        mInflater = mContext.getLayoutInflater();
        mFromWhere = fromWhere;
        mSettingManage = new SettingManage(mContext);
        mSettingSharePreference = mContext.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
        mSettingPreferenceEditor = mSettingSharePreference.edit();
    }

    @Override
    public int getCount() {
        if(mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView) {
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

        if (null != mList) {
            if (position < mList.size() && mList.valueAt(position) instanceof DownloadJob) {
                job = mList.valueAt(position);
            }
            if (null != job) {
                if(job.getCheck()){
                    holder.btnDelete.setBackgroundResource(R.drawable.radiobutton_red_bg);
                }else{
                    holder.btnDelete.setBackgroundResource(R.drawable.radiobutton_white_bg);
                }
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
                        final ViewHolder finalHolder = holder;
                        LogUtil.e("DownloadJobAdapter","image"+entity.getImage());
                        displayImage(entity.getImage(), holder.imageView, R.drawable.sarrs_main_default);
                        holder.progressBar.setProgress(job.getProgress());
                        holder.downloadName.setTextColor(mContext.getResources().getColor(R.color.color_444444));
                        holder.downloadName.setTextSize(16);
                    }
                }
                DataUtils.getInstance().setView(job, holder.progressBar, holder.progressText, holder.downloadLength);
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
                }
            }
        }
        return convertView;
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
    }
}
