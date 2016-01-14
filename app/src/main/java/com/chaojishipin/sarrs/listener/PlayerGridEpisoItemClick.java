package com.chaojishipin.sarrs.listener;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.PlayerGridEpisodeAdapter;
import com.chaojishipin.sarrs.adapter.VideoDetailChildrenEpisodeAdapter;
import com.chaojishipin.sarrs.adapter.VideoDetailFuScreeenExpandListAdapter;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PlayerGridEpisoItemClick implements AdapterView.OnItemClickListener {
    public static  int parentId;
    public static int oldposition;
    // 记录点击currentPosition
    public static String currentEpiso ;

    //private VideoDetailItem items;

    public static int pageForMiddle;


    public SparseArray<ArrayList<VideoItem>> getFenyeList() {
        return fenyeList;
    }

    public void setFenyeList(SparseArray<ArrayList<VideoItem>> fenyeList) {
        this.fenyeList = fenyeList;
    }
    private SparseArray<ArrayList<VideoItem>> fenyeList;
   public  static List<VideoItem> oldItems;
    public PlayerGridEpisoItemClick(int parentId, SparseArray<ArrayList<VideoItem>> data){

        this.parentId=parentId;

        this.fenyeList=data;
    }
    private PlayerGridEpisodeAdapter childAdapter;
     public void setAdapter(PlayerGridEpisodeAdapter adapter){
         this.childAdapter=adapter;
     }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        switch (parent.getId()){
            case R.id.video_detail_expand_grid:
                LogUtil.e("Grid", "expand pn" + parentId);
                /*if(position/6>0){
                    pageForMiddle=position/6+1+parentId*10;
                }else{
                    pageForMiddle=1;
                }*/
                   oldposition=position;
                   oldItems=fenyeList.get(parentId);
                    currentEpiso=fenyeList.get(parentId).get(position).getOrder();
                for(int i=0;i< fenyeList.get(parentId).size();i++){
                    if(i==position){
                        fenyeList.get(parentId).get(i).setIsPlay(true);
                    }else{
                        fenyeList.get(parentId).get(i).setIsPlay(false);
                    }
                }
                if(childAdapter!=null){
                childAdapter.setFenyeList(fenyeList);
                childAdapter.notifyDataSetChanged();
            }
                PlayData p=new PlayData(fenyeList,parentId,position, ConstantUtils.PLAYER_FROM_DETAIL_ITEM);
                EventBus.getDefault().post(p);
                LogUtil.e("POST ", "expand position" + position);
                LogUtil.e("POST "," expand key : "+parentId);
                // 执行更新中间布局逻辑





                break;
            case R.id.video_detail_expand_list:
                LogUtil.e("child", "click grid" + position);
                LogUtil.e("child", "click grid" + position);
                oldposition=position;
                oldItems=fenyeList.get(parentId);
                currentEpiso=fenyeList.get(parentId).get(position).getOrder();
                for(int i=0;i< fenyeList.get(parentId).size();i++){
                    if(i==position){
                        fenyeList.get(parentId).get(i).setIsPlay(true);
                    }else{
                        fenyeList.get(parentId).get(i).setIsPlay(false);
                    }
                }
                if(childAdapter!=null){
                    childAdapter.setFenyeList(fenyeList);
                    childAdapter.notifyDataSetChanged();
                }
                PlayData p2=new PlayData(fenyeList,parentId,position, ConstantUtils.PLAYER_FROM_DETAIL_ITEM);
                EventBus.getDefault().post(p2);
                break;
        }
    }
}