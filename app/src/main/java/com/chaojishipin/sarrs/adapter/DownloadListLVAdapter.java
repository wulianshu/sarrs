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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/9/2.
 */
public class DownloadListLVAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VideoItem> list;
    private SparseArray<Boolean> downloadstatus;
    private VideoDetailItem videoDetailItem;

    public DownloadListLVAdapter(Context context, ArrayList<VideoItem> list, SparseArray<Boolean> downloadstatus, VideoDetailItem videoDetailItem) {
        this.context = context;
        this.list = list;
        this.downloadstatus = downloadstatus;
        this.videoDetailItem = videoDetailItem;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        Viewholder viewholder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.videodetailactivity_fragment_expand_grid_item, null);
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

        if (downloadstatus != null && downloadstatus.get(i) != null && downloadstatus.get(i)) {
            ChaoJiShiPinApplication application = ChaoJiShiPinApplication.getInstatnce();
            SparseArray<DownloadFolderJob> jobs = application.getDownloadManager().getDownloadFolderJobs();
            if (isDownloadComplete(jobs, list.get(i))) {
                viewholder.imageView_download_icon.setImageResource(R.drawable.download_complete);
            } else {
                viewholder.imageView_download_icon.setImageResource(R.drawable.downloadlistactivity_download_icon);
            }

            viewholder.imageView_download_icon.setVisibility(View.VISIBLE);
        } else {
            viewholder.imageView_download_icon.setVisibility(View.GONE);
        }
        viewholder.textView.setText(list.get(i).getTitle());
        ImageLoader.getInstance().displayImage(list.get(i).getImage(), viewholder.imageView);
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

    public boolean isDownloadComplete(SparseArray<DownloadFolderJob> jobs, VideoItem item) {
        for (int i = 0; i < jobs.size(); i++) {
            DownloadFolderJob job = jobs.valueAt(i);
            if (job.getMediaId().equals(videoDetailItem.getId())) {
                SparseArray<DownloadJob> downloadJobs = job.getDownloadJobs();
                for (int j = 0; j < downloadJobs.size(); j++) {
                    if (downloadJobs.valueAt(j).getEntity().getGlobaVid().equals(item.getGvid())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
