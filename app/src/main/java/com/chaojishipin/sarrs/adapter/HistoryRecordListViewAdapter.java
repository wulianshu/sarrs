package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

//import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
//import com.chaojishipin.sarrs.bean.Topic;
//import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.widget.DeleteRelativelayout;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wulianshu on 2015/8/24.  SlidingMenuLeftAdapter<LetvBaseBean>
 */
public class HistoryRecordListViewAdapter<LetvBaseBean>  extends CommonAdapter<LetvBaseBean> {

    public int selectcount;

    private boolean editable=false;

    public boolean isEditable() {
        return editable;
    }

    public List<Boolean> isshowtimelist ;

    SimpleDateFormat format ;
    String currentdate ;
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    // 上下文对象
    public HistoryRecordListViewAdapter(Context context, SarrsArrayList mDatas) {

        super(context, mDatas);
        String sformat = "yyyy"+mContext.getResources().getString(R.string.year)+"MM"+
                mContext.getResources().getString(R.string.month)+"dd"+
                mContext.getResources().getString(R.string.day);
        format = new SimpleDateFormat(sformat);

        currentdate = format.format(new Date());
    }

   public class ViewHolder {
       public ToggleButton toggleButton;
       public  TextView categoryname;
       public TextView title;
       public TextView lasttimeview;
       public EqualRatioImageView image;
       public TextView save_date;
       public View save_line;
       public DeleteRelativelayout save_item;
       public RelativeLayout time_tag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.historyrecord_lv_item_layout, null);
            holder = new ViewHolder();
            holder.categoryname = (TextView) convertView.findViewById(R.id.tv_categoryname);
            holder.toggleButton = (ToggleButton) convertView.findViewById(R.id.download_radiobtton);
            holder.title = (TextView) convertView.findViewById(R.id.small_poster_title);
            holder.lasttimeview = (TextView) convertView.findViewById(R.id.tv_lasttime_view);
            holder.image = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            holder.save_date = (TextView) convertView.findViewById(R.id.save_date);
            holder.save_line = convertView.findViewById(R.id.save_line);
            holder.save_item = (DeleteRelativelayout) convertView.findViewById(R.id.save_item);
            holder.time_tag = (RelativeLayout) convertView.findViewById(R.id.time_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            if (holder.save_item.getLeft() != 0)
//            holder.save_item.layout(0, holder.save_item.getTop(), holder.save_item.getRight(), holder.save_item.getBottom());
//            holder.save_item.smoothCloseMenu();
        }
        HistoryRecord record = (HistoryRecord) mDatas.get(position);
        holder.title.setText(record.getTitle());
        String splaytime = record.getPlay_time();
        System.out.println("splaytime:"+splaytime);
        int itime = 0;
        if(splaytime!=null && !"".equals(splaytime.trim())) {
            itime = Integer.parseInt(splaytime);
        }
        int minute = itime/60;
        int second = itime % 60;
        String stime ="";
        LogUtil.e("HistoryRecordListViewAdapter",record.getDurationTime()/1000+":"+itime);
        if(record.getDurationTime()!=0 && Math.abs(record.getDurationTime()/1000-itime) <=3){
            stime = mContext.getString(R.string.VIEW_OVER);
        }else if(itime>=60){
            stime =mContext.getString(R.string.LASTTIMEVIEW)+ minute+mContext.getString(R.string.minute)+second+mContext.getString(R.string.second);
        }else{
            stime = mContext.getString(R.string.VIEW_NO_ONE_MINUTE);
        }
        if(editable){
            holder.toggleButton.setVisibility(View.VISIBLE);
            if(record.isCheck()){
                holder.toggleButton.setBackgroundResource(R.drawable.radiobutton_red_bg);
            }else{
                holder.toggleButton.setBackgroundResource(R.drawable.radiobutton_white_bg);
            }
        }else{
            holder.toggleButton.setVisibility(View.GONE);
        }

        if(isshowtimelist.get(position)){
            String sdate = format.format(new Date(Long.parseLong(record.getTimestamp())));
            if(currentdate.equals(sdate)){
                sdate = mContext.getResources().getString(R.string.today);
            }
            holder.save_date.setText(sdate);
            holder.time_tag.setVisibility(View.VISIBLE);
//            holder.save_line.setVisibility(View.VISIBLE);
//            holder.save_date.setVisibility(View.VISIBLE);
        }else{
//            holder.save_line.setVisibility(View.GONE);
//            holder.save_date.setVisibility(View.GONE);
            holder.time_tag.setVisibility(View.GONE);
        }
        holder.lasttimeview.setText(stime);
        holder.categoryname.setText(record.getCategory_name());

//        final ViewHolder finalHolder = holder;
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(record.getImage(), holder.image, options1);

//        ImageLoader.getInstance().displayImage(record.getImage(),holder.image);
//        ImageCacheManager.loadImage(record.getImage(), new com.android.volley.toolbox.ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
//                if (response.getBitmap() != null) {
//                    finalHolder.image.setImageBitmap(response.getBitmap());
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                   finalHolder.image.setImageResource(R.drawable.search_default_poster);
//            }
//        });
        return convertView;
    }
}