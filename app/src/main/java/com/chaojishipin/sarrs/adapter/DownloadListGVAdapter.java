package com.chaojishipin.sarrs.adapter;

import android.content.Context;
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
import com.chaojishipin.sarrs.download.download.DownloadManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulianshu on 2015/9/2.
 */
public class DownloadListGVAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VideoItem> list;
    private SparseArray<Boolean> downloadstatus;
    private VideoDetailItem videoDetailItem;

    public DownloadListGVAdapter(Context context, ArrayList<VideoItem> list, SparseArray<Boolean> downloadstatus, VideoDetailItem videoDetailItem) {
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
            viewholder.imageView = (ImageView) view.findViewById(R.id.video_detail_anim_bottom_showgrid_item_play);
            viewholder.textView = (TextView) view.findViewById(R.id.video_detail_anim_bottom_showgrid_item_title);
            viewholder.lv_item = (LinearLayout) view.findViewById(R.id.video_detail_anim_bottom_showlist_item_ln);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();
        }
        viewholder.lv_item.setVisibility(View.GONE);
        DownloadManager mDownloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
//                 SparseArray<DownloadFolderJob> no_complete = mDownloadManager.getProvider().getuncompleteFolderjob();
//
        if (downloadstatus != null && downloadstatus.get(i) != null && downloadstatus.get(i)) {
            SparseArray<DownloadFolderJob> jobs = mDownloadManager.getProvider().getFolderJobs();
            if (isDownloadComplete(jobs, list.get(i))) {
                viewholder.imageView.setImageResource(R.drawable.download_complete);
            } else {
                viewholder.imageView.setImageResource(R.drawable.downloadlistactivity_download_icon);
            }
            viewholder.imageView.setVisibility(View.VISIBLE);
        } else {
            viewholder.imageView.setVisibility(View.GONE);
        }
        viewholder.textView.setText(list.get(i).getOrder());
        return view;
    }

    class Viewholder {
        ImageView imageView;
        TextView textView;
        LinearLayout lv_item;
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

    public boolean unCompleteDownloadlist(SparseArray<DownloadFolderJob> jobs, VideoItem item) {
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
