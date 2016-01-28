package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadProvider;
import com.chaojishipin.sarrs.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wulianshu on 2015/9/2.
 */
public class DownloadListGVAdapter extends MyBaseAdapter {

    protected ArrayList<VideoItem> list;
    protected VideoDetailItem videoDetailItem;
    private Set<String> mDoneSet;
    private Set<String> mGoingSet;

    public DownloadListGVAdapter(Context context, ArrayList<VideoItem> list, VideoDetailItem videoDetailItem) {
        super(context);
        this.list = list;
        this.videoDetailItem = videoDetailItem;
        mDoneSet = DownloadProvider.getInstance().getCompleteGvidSet();
        mGoingSet = DownloadProvider.getInstance().getmUnCompleteGvidSet();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view) {

        Viewholder viewholder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.videodetailactivity_fragment_expand_grid_item, null);
            viewholder = new Viewholder();
            viewholder.imageView = (ImageView) view.findViewById(R.id.video_detail_anim_bottom_showgrid_item_play);
            viewholder.textView = (TextView) view.findViewById(R.id.video_detail_anim_bottom_showgrid_item_title);
            viewholder.lv_item = (LinearLayout) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_ln);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();
        }
        viewholder.lv_item.setVisibility(View.GONE);
        viewholder.imageView.setVisibility(View.VISIBLE);
        setVideoDownloadState(list.get(i), viewholder.imageView);
        viewholder.textView.setText(list.get(i).getOrder());
        return view;
    }

    class Viewholder {
        ImageView imageView;
        TextView textView;
        LinearLayout lv_item;
    }

    protected void setVideoDownloadState(VideoItem item, ImageView view){
        if(mDoneSet.contains(item.getGvid())){
            view.setImageResource(R.drawable.download_complete);
            view.setVisibility(View.VISIBLE);
        }else if(mGoingSet.contains(item.getGvid())){
            view.setImageResource(R.drawable.downloadlistactivity_download_icon);
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }
}
