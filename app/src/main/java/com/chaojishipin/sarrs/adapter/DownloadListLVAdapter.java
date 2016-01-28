package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadFolderJob;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/9/2.
 */
public class DownloadListLVAdapter extends DownloadListGVAdapter {

    public DownloadListLVAdapter(Context context, ArrayList<VideoItem> list, VideoDetailItem videoDetailItem) {
        super(context, list, videoDetailItem);
    }

    @Override
    public View getView(int i, View view) {
        Viewholder viewholder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.videodetailactivity_fragment_expand_grid_item, null);
            viewholder = new Viewholder();
            viewholder.gv_item = (RelativeLayout) view.findViewById(R.id.rl_gv_item);
            viewholder.lv_item = (LinearLayout) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_ln);
            viewholder.gv_item.setVisibility(View.GONE);
            viewholder.lv_item.setVisibility(View.VISIBLE);
            viewholder.imageView = (ImageView) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_logo);
            viewholder.textView = (TextView) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_title);
            viewholder.imageView_download_icon = (ImageView) view.findViewById(R.id.download_imageview);
            viewholder.imageview_play = (ImageView) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_playimg);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();
        }
        viewholder.imageview_play.setVisibility(View.GONE);
        VideoItem item = list.get(i);
        setVideoDownloadState(item, viewholder.imageView_download_icon);
        viewholder.textView.setText(item.getTitle());
        ImageLoader.getInstance().displayImage(item.getImage(), viewholder.imageView);
        return view;
    }

    class Viewholder {
        LinearLayout lv_item;
        RelativeLayout gv_item;
        ImageView imageView;
        ImageView imageView_download_icon;
        ImageView imageview_play;
        TextView textView;
    }
}
