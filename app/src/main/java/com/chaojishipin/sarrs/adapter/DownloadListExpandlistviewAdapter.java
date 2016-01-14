package com.chaojishipin.sarrs.adapter;
import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.util.DownloadEvent;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.widget.NoScrollGridView;
import com.chaojishipin.sarrs.widget.NoScrollListView;
import com.chaojishipin.sarrs.widget.PinnedHeaderExpandableListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by wulianshu on 2015/9/2.
 */
//implements PinnedHeaderExpandableListView.HeaderAdapter
public class DownloadListExpandlistviewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private SparseArray<ArrayList<VideoItem>> listSparseArray;
    private List<String> titlelist;
    private int current_group_open_position = 0;
    private VideoDetailItem videoDetailItem;
    private SparseArray<SparseArray<Boolean>> downloadstatulist;
    private SparseIntArray groupStatusMap = new SparseIntArray();
//  private PinnedHeaderExpandableListView lv_juji;
    public void setDownloadstatulist(SparseArray<SparseArray<Boolean>> downloadstatulist){
        this.downloadstatulist = downloadstatulist;
    }
    public void setTitlelist(List<String> titlelist){
        this.titlelist = titlelist;
    }
    public void setVideoDetailItem(VideoDetailItem videoDetailItem){
         this.videoDetailItem = videoDetailItem;
    }
    public int getCurrent_group_open_position() {
        return current_group_open_position;
    }

    public void setCurrent_group_open_position(int current_group_open_position) {
        this.current_group_open_position = current_group_open_position;
    }

    public DownloadListExpandlistviewAdapter(Context context, SparseArray<ArrayList<VideoItem>> listSparseArray, List<String> titlelist, VideoDetailItem videoDetailItem) {
        this.context = context;
        this.listSparseArray = listSparseArray;
        this.titlelist = titlelist;
        this.videoDetailItem = videoDetailItem;
//        this.lv_juji = lv_juji;
    }

    public void setData(SparseArray<ArrayList<VideoItem>> listSparseArray) {
        this.listSparseArray = listSparseArray;
    }

    @Override
    public int getGroupCount() {
        if(titlelist!=null) {
            return titlelist.size();
        }else{
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listSparseArray.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listSparseArray.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i * i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewholderGroupView viewholderGroupView;
        if (view == null) {
            view = View.inflate(context, R.layout.downloadexpandlvgroupviewlayout, null);
            viewholderGroupView = new ViewholderGroupView();
            viewholderGroupView.imageview = (ImageView) view.findViewById(R.id.video_detail_bottom_more_icon);
            viewholderGroupView.textview = (TextView) view.findViewById(R.id.video_detail_bottom_title);
            view.setTag(viewholderGroupView);
        } else {
            viewholderGroupView = (ViewholderGroupView) view.getTag();
        }
        if (current_group_open_position == -1) {
            viewholderGroupView.imageview.setImageResource(R.drawable.arrow_up_download_activity);
        } else if (current_group_open_position == i) {
            viewholderGroupView.imageview.setImageResource(R.drawable.arrow_down_download_activity);
        } else {
            viewholderGroupView.imageview.setImageResource(R.drawable.arrow_up_download_activity);
        }
        if(titlelist!=null && titlelist.size()==1){
            viewholderGroupView.imageview.setVisibility(View.GONE);
        }
        if(titlelist!=null && titlelist.size()> i){
            viewholderGroupView.textview.setText(titlelist.get(i));
        }


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ViewholderchildView viewholderchildView;
        if (view == null) {
            view = View.inflate(context, R.layout.downloadexpandlvchildviewlayout, null);
            viewholderchildView = new ViewholderchildView();
            viewholderchildView.gridView = (NoScrollGridView) view.findViewById(R.id.video_detail_expand_grid);
            viewholderchildView.listView = (NoScrollListView) view.findViewById(R.id.video_detail_expand_list);
            view.setTag(viewholderchildView);
        } else {
            viewholderchildView = (ViewholderchildView) view.getTag();
        }
        if (listSparseArray.get(i) != null) {
            //动漫 电视剧
            if (videoDetailItem.getCategory_id().equals(ConstantUtils.CARTOON_CATEGORYID) || videoDetailItem.getCategory_id().equals(ConstantUtils.TV_SERISE_CATEGORYID)) {
                viewholderchildView.listView.setVisibility(View.GONE);
                viewholderchildView.gridView.setVisibility(View.VISIBLE);
                DownloadListGVAdapter gridAdapter = new DownloadListGVAdapter(context, listSparseArray.get(i),downloadstatulist.get(i),videoDetailItem);
                viewholderchildView.gridView.setAdapter(gridAdapter);
                viewholderchildView.gridView.setOnItemClickListener(new MyGridViewOnItemClickListener(i, listSparseArray));
           //综艺 记录片
            } else if (videoDetailItem.getCategory_id().equals(ConstantUtils.DOCUMENTARY_CATEGORYID) || videoDetailItem.getCategory_id().equals(ConstantUtils.VARIETY_CATEGORYID)) {
                viewholderchildView.listView.setVisibility(View.VISIBLE);
                viewholderchildView.gridView.setVisibility(View.GONE);
                DownloadListLVAdapter lvAdapter = new DownloadListLVAdapter(context, listSparseArray.get(i),downloadstatulist.get(i),videoDetailItem);
                viewholderchildView.listView.setAdapter(lvAdapter);
                viewholderchildView.listView.setOnItemClickListener(new MyGridViewOnItemClickListener(i, listSparseArray));
            }
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

//    @Override
//    public int getHeaderState(int groupPosition, int childPosition) {
//        final int childCount = getChildrenCount(groupPosition);
//        if (childPosition == childCount - 1) {
//            return PINNED_HEADER_PUSHED_UP;
//        } else if (childPosition == -1
//                && !lv_juji.isGroupExpanded(groupPosition)) {
//            return PINNED_HEADER_GONE;
//        } else {
//            return PINNED_HEADER_VISIBLE;
//        }
//
//    }
//
//    @Override
//    public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
//        String groupData =  this.titlelist.get(groupPosition);
//        ((TextView) header.findViewById(R.id.video_detail_bottom_title)).setText(groupData);
//    }
//
//    @Override
//    public void setGroupClickStatus(int groupPosition, int status) {
//        groupStatusMap.put(groupPosition, status);
//    }
//
//    @Override
//    public int getGroupClickStatus(int groupPosition) {
//        if (groupStatusMap.keyAt(groupPosition)>=0) {
//            return groupStatusMap.get(groupPosition);
//        } else {
//            return 0;
//        }
//    }

    public class ViewholderGroupView {
        ImageView imageview;

        public TextView getTextview() {
            return textview;
        }

        public void setTextview(TextView textview) {
            this.textview = textview;
        }

        public ImageView getImageview() {
            return imageview;
        }

        public void setImageview(ImageView imageview) {
            this.imageview = imageview;
        }

        TextView textview;
    }

    class ViewholderchildView {
        NoScrollGridView gridView;
        NoScrollListView listView;
    }

    class MyGridViewOnItemClickListener implements AdapterView.OnItemClickListener {
        private int index;
        private SparseArray<ArrayList<VideoItem>> listSparseArray;

        public MyGridViewOnItemClickListener(int i, SparseArray<ArrayList<VideoItem>> listSparseArray) {
            this.index = i;
            this.listSparseArray = listSparseArray;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ImageView imageView = null;
            switch(adapterView.getId()){
                case R.id.video_detail_expand_grid:
                    imageView = (ImageView) view.findViewById(R.id.video_detail_anim_bottom_showgrid_item_play);
                    break;
                case R.id.video_detail_expand_list:
                    imageView = (ImageView) view.findViewById(R.id.download_imageview);
                    break;
                default:
                    break;
            }
            DownloadEvent event = new DownloadEvent();
            if(event.downloadFile((Activity)context, videoDetailItem, i)){
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

}
