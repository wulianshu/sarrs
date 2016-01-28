package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.LiveDataEntity;
import com.chaojishipin.sarrs.bean.LiveProgramEntity;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyemin on 2016/1/25.
 */
public class LiveActivityChannelAdapter extends BaseAdapter {
    private String TAG = "LiveActivityChannelAdapter";
    private ArrayList<LiveDataEntity> mLiveItemList;
    private Context mContext;

    public LiveActivityChannelAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (null != mLiveItemList)
            return mLiveItemList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mLiveItemList)
            return mLiveItemList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LiveDataEntity liveDataItem = mLiveItemList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.liveactivity_channel_layout, null);
            holder = new ViewHolder();
            holder.divider = (View) convertView.findViewById(R.id.live_divider);
            holder.poster = (EqualRatioImageView) convertView.findViewById(R.id.channel_poster);
            holder.suggest = (ImageView) convertView.findViewById(R.id.channel_suggest_tag);
            holder.tag = (TextView) convertView.findViewById(R.id.channel_poster_tag);
            holder.date = (TextView) convertView.findViewById(R.id.channel_date);
            holder.time = (TextView) convertView.findViewById(R.id.channel_time);
            holder.channelIcon = (ImageView) convertView.findViewById(R.id.channel_icon);
            holder.channelName = (TextView) convertView.findViewById(R.id.channel_name);
            holder.channelTitle = (TextView) convertView.findViewById(R.id.channel_title);
            holder.statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.bottom = (LinearLayout) convertView.findViewById(R.id.live_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        if (position == mLiveItemList.size() - 1) {
            holder.bottom.setVisibility(View.VISIBLE);
        } else
            holder.bottom.setVisibility(View.GONE);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(liveDataItem.getPoster(), holder.poster, options);
        if ("1".equalsIgnoreCase(liveDataItem.getRec())) {
            // 推荐
            holder.suggest.setVisibility(View.VISIBLE);
            holder.tag.setVisibility(View.GONE);
        } else {
            holder.tag.setVisibility(View.VISIBLE);
            holder.suggest.setVisibility(View.GONE);
            if (ConstantUtils.LIVE_TELEVISION.equalsIgnoreCase(liveDataItem.getCid()))
                holder.tag.setText(mContext.getResources().getString(R.string.live_television));
            else
                holder.tag.setText(mContext.getResources().getString(R.string.live_other));
        }
        List<LiveProgramEntity> programs = liveDataItem.getPrograms();
        if (null != programs && programs.size() > 0) {
            // 取programs中第一个program显示
            String curBeginTime = (programs.get(0)).getBeginTime();
            String curTitle = (programs.get(0)).getTitle();
            // 设置日期，时间
            String[] arr = curBeginTime.split(" ");
            if (null != arr) {
                if (1 == arr.length) {
                    // 代理层返回日期，时间中间没有隔开
                    holder.date.setText(arr[0]);
                    holder.time.setText(arr[0]);
                }
                if (2 == arr.length) {
                    holder.date.setText(convertDate(arr[0]));
                    String time = arr[1].trim();
                    if (!TextUtils.isEmpty(time) && time.length() > 5)
                        holder.time.setText(time.substring(0, 5));
                    else
                        holder.time.setText(time);
                }
            } else {
                holder.date.setText(curBeginTime);
                holder.time.setText(curBeginTime);
            }
            // 设置title
            if (!TextUtils.isEmpty(curTitle)) {
                if (curTitle.trim().length() > 12) {
                    holder.channelTitle.setText(curTitle.trim().substring(0, 12) + "...");
                } else
                    holder.channelTitle.setText(curTitle);
            }
        }
        ImageLoader.getInstance().displayImage(liveDataItem.getIcon(), holder.channelIcon, options);
        holder.channelName.setText(liveDataItem.getChannelName());
        if (ConstantUtils.LIVE_TELEVISION.equalsIgnoreCase(liveDataItem.getCid())) {
            holder.status.setText(mContext.getResources().getString(R.string.liveing_status));
        } else
            holder.status.setText(mContext.getResources().getString(R.string.live_other_status));
        return convertView;
    }

    public ArrayList<LiveDataEntity> getmLiveItemList() {
        return mLiveItemList;
    }

    public void setmLiveItemList(ArrayList<LiveDataEntity> mLiveItemList) {
        this.mLiveItemList = mLiveItemList;
    }

    private class ViewHolder {
        View divider;
        EqualRatioImageView poster;
        ImageView suggest;
        TextView tag;
        TextView date;
        TextView time;
        TextView channelTitle;
        ImageView channelIcon;
        TextView channelName;
        ImageView statusIcon;
        TextView status;
        LinearLayout bottom;
    }

    /**
     * @param origin
     * @return
     */
    private String convertDate(String origin) {
        String today = Utils.getTodayStr("yyyy-MM-dd");
        String result = origin;
        if (!TextUtils.isEmpty(origin)) {
            if (origin.equalsIgnoreCase(today))
                result = "今天";
            else {
                StringBuffer sb = new StringBuffer();
                String[] arr = origin.split("-");
                if (null != arr && arr.length == 3)
                    result = sb.append(arr[1] + "月" + arr[2] + "日").toString();
            }
        }
        return result;
    }
}
