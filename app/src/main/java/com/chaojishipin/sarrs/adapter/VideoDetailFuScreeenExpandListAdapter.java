package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.listener.ExpandViewOnChildItemClick;
import com.chaojishipin.sarrs.listener.PlayerGridEpisoItemClick;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/6.
 * @des 播放器全屏剧集适配器
 */
public class VideoDetailFuScreeenExpandListAdapter extends BaseAdapter{

    public static final int STATE_LOADING = 0;
    public static final int STATE_EXPANDED = -1;
    private Context mContext;

    private List<String> tagList;
    private SparseArray<ArrayList<VideoItem>> fenyeList;
    private SparseIntArray mStateArray = new SparseIntArray();
    PlayerGridEpisodeAdapter episoGridAdapter;
    PlayerGridEpisodeAdapter episoListAdapter;
    public SparseArray<ArrayList<VideoItem>> getFenyeList() {
        return fenyeList;
    }

    public void setFenyeList(SparseArray<ArrayList<VideoItem>> datas) {
        this.fenyeList = datas;
    }



   // private SparseArray<VideoDetailItem> mCachedData = new SparseArray<VideoDetailItem>();
    public VideoDetailFuScreeenExpandListAdapter(List<String> tagList, Context context, SparseArray<ArrayList<VideoItem>> datas, String cid) {

        super();
        this.cid=cid;
        this.tagList = tagList;
        this.mContext=context;
        this.fenyeList=datas;

    }

    public void setTagList(List<String> tagList){
        this.tagList = tagList;
        mStateArray.clear();
        fenyeList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (tagList==null)?0:tagList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return tagList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    private int mPosition;
    private int pn;
    // 根据返回分页数据更新三角图标

    /**
     *  @param  tagIndex 点击全屏的tag 索引
     *  @param  tagIndex2 点击半屏的分页tag索引 从0开始
     *
     *
     * */


    public void updateCurrentVideoInPage(int pn,int position,int tagIndex,int tagIndex2,SparseArray<ArrayList<VideoItem>> sps){



        this.fenyeList=sps;
        this.mPosition=position;
        this.pn=pn;
         if(fenyeList==null||fenyeList.size()<=0){
             return;
         }
        if(fenyeList.indexOfKey(pn)<0){
            return;
        }
        if((fenyeList.get(pn).size()-1<position)){
            return;
        }
        if(position<0){
            return;
        }
        if(tagIndex<0){
            return;
        }
      /*  if(tagIndex!=pn){

        }*/

        // 重置状态
        for(int i=0;i<fenyeList.size();i++) {
            int key=fenyeList.keyAt(i);
            for (int j = 0; j < fenyeList.get(key).size(); j++) {

                fenyeList.get(key).get(j).setIsPlay(false);
            }
        }
        // 花三角
        fenyeList.get(pn).get(position).setIsPlay(true);
        // 根据底部点击同时展开
        autoexpandPosition(tagIndex);

        // 点击tag分页与当前分页不同则展开最新分页
       /* if(pn!=tagIndex){
            autoexpandPosition(tagIndex);
        }else{
            autoexpandPosition(pn);
        }*/
        // 自动展开tag
            PlayerGridEpisoItemClick.oldItems=fenyeList.get(position);
            PlayerGridEpisoItemClick.oldposition=position;
            PlayerGridEpisoItemClick.parentId=pn;




    }



  // 判断剧集在分页所在list中
    public boolean isContainsEpiso(List<VideoItem> items,String mCurrentPlayingOrder){

        for(VideoItem item: items){

            if( item.getOrder().equalsIgnoreCase(mCurrentPlayingOrder)){
                return true;
            }
        }
        return false;
    }

    // reset play state
    public void resetPlayState(List<VideoItem> items){
        for(VideoItem item: items){
            item.setIsPlay(false);
        }
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    private String cid;


  /*// 点击分页更新剧集状态
    public void updatePlayState(List<VideoItem>expandOldItems ,List<VideoItem>expandNewItems ,int oldposition,int newposition,int oldpn,int newpn) {

        if(expandOldItems==null){
            return;
        }

        if (oldpn == newpn) {
            //do nothing
        } else {
            expandOldItems.get(oldpn).setIsPlay(false);
            expandNewItems.get(newpn).setIsPlay(false);

        }

    }*/




    // onTagClick
    private OnTagClickListener mOnTagListener;
    private OnCollaspListener mOnCollasp;
    private OnExpandListener mExpand;
    public void setOnTagClickListener(OnTagClickListener tagClickListener){
        this.mOnTagListener=tagClickListener;
    }
    public void setOnCollaspListener(OnCollaspListener onCollaspListener){
        this.mOnCollasp=onCollaspListener;
    }

    public void setOnExpandListener(OnExpandListener expandListener){
       this.mExpand=expandListener;
    }



    @Override
    public View getView( final int position, View convertView, ViewGroup parent) {
         EpisodesTagHolder holder;


        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.videodetailactivity_fragment_expand_item2, parent, false);
            holder = new EpisodesTagHolder();
            holder.tagContainer = convertView.findViewById(R.id.video_detail_expand_tagcontainer);
            holder.tag = (TextView) convertView.findViewById(R.id.video_detail_expand_tag);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.video_detail_expand_progressbar);
            holder.episodesGrid = (GridView) convertView.findViewById(R.id.video_detail_expand_grid);
            holder.episodesList=(ListView)convertView.findViewById(R.id.video_detail_expand_list);
            holder.expandeIndicator = (ImageView) convertView.findViewById(R.id.video_detail_expand_indicator);
            convertView.setTag(holder);
        }else{
            holder = (EpisodesTagHolder) convertView.getTag();
        }
        if(tagList==null||tagList.size()==0){
            holder.tag.setVisibility(View.GONE);
        }else{
            holder.tag.setText(tagList.get(position));
        }
        boolean isInLoadingState = getInStatePosition(STATE_LOADING)== position;
        holder.progressBar.setVisibility(isInLoadingState ? View.VISIBLE : View.GONE);
        // 如果是处在展开状态 则展开gridView，并显示数据
        if(getInStatePosition(STATE_EXPANDED) == position){
            LogUtil.e("TEST ","postion " +position);
            LogUtil.e("TEST ","cid " +cid);
            holder.progressBar.setVisibility(View.GONE);

            ArrayList<VideoItem> cachedData = fenyeList.get(position);
            if(cachedData != null){
                holder.expandeIndicator.setImageResource(R.drawable.sarrs_pic_videodetail_arrow_btn_up);
                holder.tagContainer.setSelected(true);
                if(cid.equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_1))||cid.equalsIgnoreCase(String.valueOf(ConstantUtils.MAIN_DATA_TYPE_3))){
                    // 依据分页更新剧集点击样式
                    holder.episodesGrid.setVisibility(View.VISIBLE);
                    // 跨页展开剧集图标更新
                    PlayerGridEpisoItemClick gridClick=  new PlayerGridEpisoItemClick(position,getFenyeList());
                  //  updatePlayState(PlayerGridEpisoItemClick.oldItems, cachedData, PlayerGridEpisoItemClick.oldposition, mPosition,PlayerGridEpisoItemClick.parentId,pn);
                        episoGridAdapter= new PlayerGridEpisodeAdapter(mContext,position,getFenyeList());
                        holder.episodesGrid.setOnItemClickListener(gridClick);

                        gridClick.setAdapter(episoGridAdapter);
                        holder.episodesGrid.setAdapter(episoGridAdapter);
                        gridList.add(episoGridAdapter);

                }else{
                    // 依据分页更新剧集点击样式
                    holder.episodesList.setVisibility(View.VISIBLE);
                    PlayerGridEpisoItemClick listClick=  new PlayerGridEpisoItemClick(position,getFenyeList());
                    LogUtil.e("TEST", "updatexxx" + getFenyeList().get(pn).get(mPosition).isPlay()+"");
                  //  updatePlayState(PlayerGridEpisoItemClick.oldItems,cachedData,PlayerGridEpisoItemClick.oldposition, mPosition,PlayerGridEpisoItemClick.parentId,pn);
                    holder.episodesList.setFocusable(false);
                        episoListAdapter= new PlayerGridEpisodeAdapter(mContext,position,getFenyeList());
                        holder.episodesList.setAdapter(episoListAdapter);
                        listClick.setAdapter(episoListAdapter);
                        gridList.add(episoListAdapter);
                        holder.episodesList.setOnItemClickListener(listClick);
                    LogUtil.e("NEXT PAGE 1", "" + (episoListAdapter.getFenyeList().get(position).get(0)).isPlay());
                    LogUtil.e("NEXT PAGE 2", "" + ((VideoItem) episoListAdapter.getItem(0)).isPlay());

                }
                if(getCount() == 1){
                    holder.tagContainer.setVisibility(View.GONE);
                }
            }else{
                holder.expandeIndicator.setImageResource(R.drawable.sarrs_pic_videodetail_arrow_btn_down);
                holder.tagContainer.setSelected(false);
                holder.episodesGrid.setVisibility(View.GONE);
                holder.episodesList.setVisibility(View.GONE);
                LogUtil.e("Cache","is null");

            }
        }else{
            holder.expandeIndicator.setImageResource(R.drawable.sarrs_pic_videodetail_arrow_btn_down);
            holder.tagContainer.setSelected(false);
            holder.episodesGrid.setVisibility(View.GONE);
            holder.episodesList.setVisibility(View.GONE);
        }

        return convertView;
    }

    public  interface OnTagClickListener extends OnItemClickListener{

    }


    public  interface OnCollaspListener extends OnItemClickListener{




    }
    public  interface OnExpandListener extends  OnItemClickListener{

    }




    public static class EpisodesTagHolder{
        public View tagContainer;
        public TextView tag;
        public ProgressBar progressBar;
        public GridView episodesGrid;
        public ImageView expandeIndicator;
        public ListView episodesList;
    }

    public int getInStatePosition(int state){
        return mStateArray.get(state, -1);
    }

    public void setPositionState(int position, int state) {
        mStateArray.append(state, position);
    }

    public void resetState(int state){
        setPositionState(-1, state);
    }
    public ArrayList<VideoItem> getCachedData(int position){
        return fenyeList.get(position);
    }
    // 展开的剧集gridview & listViwe adapter
    public void collaspPosition(int position){
        resetState(STATE_EXPANDED);
        gridList.get(position).notifyDataSetChanged();
        notifyDataSetChanged();
    }
    List<PlayerGridEpisodeAdapter> gridList=new ArrayList<PlayerGridEpisodeAdapter>();
    public PlayerGridEpisodeAdapter getMiddleGridAdapter(int position){
            if(gridList.size()==0){
                   return null;
               }else{
                   return  gridList.get(position);
               }
    }


    public void expandPosition(int pn,SparseArray<ArrayList<VideoItem>>sp ){
        setPositionState(pn, STATE_EXPANDED);
        resetState(STATE_LOADING);
        setFenyeList(sp);
         int size=  sp.size();
        for(int i=0;i<size;i++){
           LogUtil.e("KEY",""+sp.keyAt(i));
        }
        notifyDataSetChanged();
    }


    public void autoexpandPosition(int pn){
        setPositionState(pn, STATE_EXPANDED);
        resetState(STATE_LOADING);
        //setFenyeList(datas);
        notifyDataSetChanged();
    }



    public void setPositionInLoading(int position){
        if(getInStatePosition(STATE_LOADING) == position){
            return;
        }else if(getInStatePosition(STATE_EXPANDED) == position){
            resetState(STATE_EXPANDED);
        }
        setPositionState(position, STATE_LOADING);
        notifyDataSetChanged();
    }

}
