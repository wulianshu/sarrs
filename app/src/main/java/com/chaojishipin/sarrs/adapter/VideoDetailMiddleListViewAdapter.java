package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.listener.ExpandViewOnChildItemClick;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/6.
 *
 * @des 详情页中间部分一行数据listview 适配器
 */
public class VideoDetailMiddleListViewAdapter extends BaseAdapter {

    private Context context;
    private int position;// 分页对应点击position
    private int pn;// 分页

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSmallPageSize() {
        return smallPageSize;
    }

    public void setSmallPageSize(int smallPageSize) {
        this.smallPageSize = smallPageSize;
    }

    public int getTagPageCount() {
        return tagPageCount;
    }

    public void setTagPageCount(int tagPageCount) {
        this.tagPageCount = tagPageCount;
    }

    private int pageSize=10;
    private int smallPageSize=2;// 综艺分页pagesize
    private int tagPageCount=5;// 服务端返回折叠剧集一个分页（10集）最多容纳中间展示剧集（2集）倍数
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    private int pageNum;
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    public VideoDetailMiddleListViewAdapter(Context context, SparseArray<ArrayList<VideoItem>> mDatas,int pageTotalCount) {
        this.context = context;
        this.fenyeList = mDatas;
        this.pageTotalCount=pageTotalCount;
    }

    public SparseArray<ArrayList<VideoItem>> getFenyeList() {
        return fenyeList;
    }

    public void setFenyeList(SparseArray<ArrayList<VideoItem>> fenyeList) {
        this.fenyeList = fenyeList;
    }

    SparseArray<ArrayList<VideoItem>> fenyeList;
    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }
    public List<VideoItem> getData() {
        return fenyeList.get(pn);
    }

    @Override
    public int getCount() {
        LogUtil.e("xll","Grid getCount "+pageNum);
        LogUtil.e("xll","Grid pageCount "+pageTotalCount);
        if(fenyeList!=null&&fenyeList.size()>0&&fenyeList.indexOfKey(pn)>=0){
            if((pageNum%tagPageCount)*smallPageSize<=fenyeList.get(pn).size()){
                if(pageNum%tagPageCount==0&&pageNum==pageTotalCount*tagPageCount&&fenyeList.get(pn).size()%smallPageSize!=0)
                {
                    return fenyeList.get(pn).size()%smallPageSize;
                }else{
                    return smallPageSize;
                }


            }else{
                //  LogUtil.e("Grid","Grid " +fenyeList.gee(pn).size()%6);
                return fenyeList.get(pn).size()%smallPageSize;
            }

        }else{
            return 0;
        }
    }

    public int getPageTotalCount() {
        return pageTotalCount;
    }

    public void setPageTotalCount(int pageTotalCount) {
        this.pageTotalCount = pageTotalCount;
    }

    // 播放器需要提供 pn , position,items,boolean needNextpage ,
    private int pageTotalCount;
    // 没播放分页时候 更新播放按钮
    public void updateCurrentVideoInPage(int pn,int position,int ps,SparseArray<ArrayList<VideoItem>> items){
        this.pn= pn;
        LogUtil.e("Grid update","parentId"+pn);
        this.position=position;
        if(items==null||items.size()<=0){
            return;
        }
        for(int i=0;i<items.get(pn).size();i++){
            if(position==i){
                items.get(pn).get(i).setIsPlay(true);
            }else{
                items.get(pn).get(i).setIsPlay(false);
            }
        }
        this.fenyeList=items;
        this.notifyDataSetChanged();
    }


    public void setData(SparseArray<ArrayList<VideoItem>> data) {
        this.fenyeList = data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return fenyeList.get(pn).get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoDtailListViewItemHodler holder = null;
        if (convertView == null) {
            holder = new VideoDtailListViewItemHodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.videodetailactivity_frament_bottom_list_item, parent, false);
            holder.mOrderText = (TextView) convertView.findViewById(R.id.videodetail_fragmentbottom_list_item_title);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.videodetail_fragmentbottom_list_item_img);
            convertView.setTag(holder);
        } else {
            holder = (VideoDtailListViewItemHodler) convertView.getTag();

        }
        if(getPosition()/smallPageSize>0&&fenyeList.indexOfKey(pn)>=0){
            pageNum=getPosition()/smallPageSize+1+pn*tagPageCount;
        }else{
            pageNum=1;
        }
        int cu=(Math.max((pn - 1), 0) * pageSize + position + (pageNum - 1) * smallPageSize)%pageSize;

        if(fenyeList!=null&&fenyeList.size()>0&&fenyeList.indexOfKey(pn)>=0){
            if(cu<fenyeList.get(pn).size()){
                if(cu>=fenyeList.get(pn).size()){
                    cu=fenyeList.get(pn).size()-1;
                }
                VideoItem item =fenyeList.get(pn).get(cu);

                holder.mOrderText.setText(item.getTitle());
                if (item.isPlay()) {
                    holder.mImageView.setVisibility(View.VISIBLE);
                    holder.mOrderText.setTextColor(context.getResources().getColor(R.color.color_c5242b));
                } else {
                    holder.mImageView.setVisibility(View.INVISIBLE);
                    holder.mOrderText.setTextColor(context.getResources().getColor(R.color.color_666666));
                }
            }

        }

        return convertView;
    }


    class VideoDtailListViewItemHodler {
        public TextView mOrderText;
        public ImageView mImageView;


    }


}
