package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xll on 2015/08/24.
 * @des 详情页底部弹出布局  电视剧折叠grid以及综艺折叠list展示 适配器
 */
public  class PlayerGridEpisodeAdapter extends BaseAdapter {
   // private VideoDetailItem data;
    private int parentId;
    SparseArray<VideoDetailItem> parentData;

    public SparseArray<ArrayList<VideoItem>> getFenyeList() {
        return fenyeList;
    }

    public void setFenyeList(SparseArray<ArrayList<VideoItem>> fenyeList) {
        this.fenyeList = fenyeList;
    }

    // 播放源
    private SparseArray<ArrayList<VideoItem>> fenyeList;
    private Context mContext;


    public PlayerGridEpisodeAdapter(Context context, int parentId, SparseArray<ArrayList<VideoItem>> datas) {
        this.fenyeList=datas;
        this.mContext=context;
        this.parentId=parentId;



    }

/*    public void setData(VideoDetailItem item){
        this.data=item;
    }*/

  /*  public void setParentData(SparseArray<VideoDetailItem> parentData){

        this.parentData=parentData;

    }*/
    /*public SparseArray<VideoDetailItem> getParentData(){
        return parentData;
    }*/
  /*  //public VideoDetailItem getData( ){
        return data;
    }*/
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
        public TextView mEpisoGridOrder;
        private ImageView mEpisoGridPlay;
        /////
        public TextView mEpisoOrder;
        private ImageView mEpisoLogo;
        private ImageView mEpisoPlay;
        private LinearLayout showList;
    }
    @Override
    public View getView(int cPosition, View convertView, ViewGroup parent) {
        VideodetailExpandItemHolder holder=null;

        if (convertView == null) {
            holder=new VideodetailExpandItemHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.videodetailactivity_fragment_expand_grid_item2, parent, false);
            // 电视剧 动漫
            holder.mEpisoGridOrder=(TextView)convertView.findViewById(R.id.video_detail_anim_bottom_showgrid_item_title);
            holder.mEpisoGridPlay=(ImageView)convertView.findViewById(R.id.video_detail_anim_bottom_showgrid_item_play);
            // 综艺
            holder.showList=(LinearLayout)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_ln);
            holder.mEpisoOrder=(TextView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_title);
            holder.mEpisoLogo=(ImageView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_logo);
            holder.mEpisoPlay=(ImageView)convertView.findViewById(R.id.video_detail_anim_bottom_showlist_item_playimg);
            convertView.setTag(holder);
        }else{
            holder=(VideodetailExpandItemHolder)convertView.getTag();

        }

        if(getItemViewType(cPosition)==ConstantUtils.MAIN_DATA_TYPE_1||getItemViewType(cPosition)==ConstantUtils.MAIN_DATA_TYPE_3){
            LogUtil.e("Expand", cPosition + "");
            holder.mEpisoGridOrder.setVisibility(View.VISIBLE);
            if(fenyeList.get(parentId).get(cPosition).isPlay()){
                LogUtil.e("Media","player parentId : "+parentId);
                LogUtil.e("Media","player cPosition : "+cPosition);
                holder.mEpisoGridPlay.setVisibility(View.VISIBLE);
                holder.mEpisoGridOrder.setTextColor(mContext.getResources().getColor(R.color.color_c5242b));
                holder.mEpisoGridOrder.setBackgroundColor(mContext.getResources().getColor(R.color.color_00000000));
            }else{
                holder.mEpisoGridPlay.setVisibility(View.GONE);
               // holder.mEpisoGridOrder.setTextColor(mContext.getResources().getColor(R.color.color_00000000));
                holder.mEpisoGridOrder.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            }
            LogUtil.e("Video size", "" + fenyeList.get(parentId).size());
            LogUtil.e("Video ", "" + fenyeList.get(parentId).get(cPosition).getOrder());
            holder.mEpisoGridOrder.setText(fenyeList.get(parentId).get(cPosition).getOrder());
        }else{
            holder.showList.setVisibility(View.VISIBLE);
            holder.mEpisoOrder.setVisibility(View.VISIBLE);
            holder.mEpisoLogo.setVisibility(View.VISIBLE);
            if(fenyeList.get(parentId).get(cPosition).isPlay()){
//                 && fenyeList.get(parentId).get(cPosition).getTitle
                LogUtil.e("Media","playerwls parentId : "+parentId);
                LogUtil.e("Media","playerwls cPosition : "+cPosition);
                holder.mEpisoPlay.setVisibility(View.VISIBLE);
                holder.mEpisoOrder.setTextColor(mContext.getResources().getColor(R.color.color_c5242b));
            } else {
                holder.mEpisoPlay.setVisibility(View.INVISIBLE);
                holder.mEpisoOrder.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            }
            LogUtil.e("Video ", "" + fenyeList.get(parentId).get(cPosition).getOrder());
            holder.mEpisoOrder.setText(fenyeList.get(parentId).get(cPosition).getTitle());
            ImageLoader.getInstance().displayImage(fenyeList.get(parentId).get(cPosition).getImage(), holder.mEpisoLogo);
        }
        return convertView;
    }
}
