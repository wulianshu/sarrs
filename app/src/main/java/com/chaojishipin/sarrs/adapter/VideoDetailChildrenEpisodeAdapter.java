package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/6.
 * @des 详情页底部弹出布局  电视剧折叠grid以及综艺折叠list展示 适配器
 */
public  class VideoDetailChildrenEpisodeAdapter extends BaseAdapter {
   // private VideoDetailItem data;
    private int parentId;

    public SparseArray<ArrayList<VideoItem>> getFenyeList() {
        return fenyeList;
    }

    public void setFenyeList(SparseArray<ArrayList<VideoItem>> fenyeList) {
        this.fenyeList = fenyeList;
    }

    // 播放源
    private SparseArray<ArrayList<VideoItem>> fenyeList;
    private Context mContext;


    public VideoDetailChildrenEpisodeAdapter(Context context, int parentId,SparseArray<ArrayList<VideoItem>> datas) {
        this.fenyeList=datas;
        this.mContext=context;
        this.parentId=parentId;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(fenyeList.get(parentId).get(position).getCategory_id());
    }

    @Override
    public int getCount() {
      if(fenyeList!=null&&fenyeList.size()>0){
          LogUtil.e("","");
          return fenyeList.get(parentId).size();
      }else{
          return 0;
      }

    }

    @Override
    public Object getItem(int position) {
        return fenyeList.get(parentId).get(position);
    }

    @Override
    public long getItemId(int position) {
        LogUtil.e("VideoBottomAdapter", "" + position);
        return position;
    }

    class VideodetailExpandItemHolder{
        private TextView mEpisoGridOrder;
        private ImageView mEpisoGridPlay;
        private TextView mEpisoOrder;
        private ImageView mEpisoLogo;
        private ImageView mEpisoPlay;
        private LinearLayout showList;
        private RelativeLayout hideRelativelayout;
    }
    @Override
    public View getView(int cPosition, View convertView, ViewGroup parent) {
        VideodetailExpandItemHolder holder=null;
        if (convertView == null) {
            holder=new VideodetailExpandItemHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.videodetailactivity_fragment_expand_grid_item, parent, false);
            // 电视剧 动漫
            holder.mEpisoGridOrder=(TextView)convertView.findViewById(R.id.video_detail_anim_bottom_showgrid_item_title);
            holder.mEpisoGridPlay=(ImageView)convertView.findViewById(R.id.video_detail_anim_up_showgrid_item_play);
            // 综艺
            holder.showList=(LinearLayout)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_ln);
            holder.mEpisoOrder=(TextView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_title);
            holder.mEpisoLogo=(ImageView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_logo);
            holder.mEpisoPlay=(ImageView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_playimg);
            holder.hideRelativelayout = (RelativeLayout) convertView.findViewById(R.id.rl_gv_item);
            convertView.setTag(holder);
        }else{
            holder=(VideodetailExpandItemHolder)convertView.getTag();
        }

        if(getItemViewType(cPosition)==ConstantUtils.MAIN_DATA_TYPE_1||getItemViewType(cPosition)==ConstantUtils.MAIN_DATA_TYPE_3){
            LogUtil.e("Expand", cPosition + "");
            holder.mEpisoGridOrder.setVisibility(View.VISIBLE);
            if(fenyeList.get(parentId).get(cPosition).isPlay()){
                holder.mEpisoGridPlay.setVisibility(View.VISIBLE);
                holder.mEpisoGridOrder.setTextColor(mContext.getResources().getColor(R.color.color_c5242b));
                holder.mEpisoGridPlay.setImageResource(R.drawable.sarrs_pic_videodetail_play);
            }else{
                holder.mEpisoGridOrder.setTextColor(mContext.getResources().getColor(R.color.color_666666));
                holder.mEpisoGridPlay.setVisibility(View.GONE);
            }
            LogUtil.e("Video size", "" + fenyeList.get(parentId).size());
            LogUtil.e("Video ", "" + fenyeList.get(parentId).get(cPosition).getOrder());
            holder.mEpisoGridOrder.setText(fenyeList.get(parentId).get(cPosition).getOrder());


        }else{
            holder.showList.setVisibility(View.VISIBLE);
            holder.mEpisoOrder.setVisibility(View.VISIBLE);
            holder.mEpisoLogo.setVisibility(View.VISIBLE);
            holder.hideRelativelayout.setVisibility(View.GONE);
            if(fenyeList.get(parentId).get(cPosition).isPlay()){
                holder.mEpisoPlay.setVisibility(View.VISIBLE);
                holder.mEpisoOrder.setTextColor(mContext.getResources().getColor(R.color.color_c5242b));
            }else {
                holder.mEpisoPlay.setVisibility(View.INVISIBLE);
                holder.mEpisoOrder.setTextColor(mContext.getResources().getColor(R.color.color_666666));
            }
            LogUtil.e("Video ", "" + fenyeList.get(parentId).get(cPosition).getOrder());
            holder.mEpisoOrder.setText(fenyeList.get(parentId).get(cPosition).getTitle());
            holder.mEpisoPlay.setImageResource(R.drawable.sarrs_pic_videodetail_play);
            ImageLoader.getInstance().displayImage(fenyeList.get(parentId).get(cPosition).getImage(), holder.mEpisoLogo);
        }
        return convertView;
    }
}
