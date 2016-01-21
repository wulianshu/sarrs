package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class SlidingMenuGVAdapter<LetvBaseBean>  extends CommonAdapter<LetvBaseBean> {
    // 上下文对象
    public SlidingMenuGVAdapter(Context context, SarrsArrayList mDatas) {
        super(context, mDatas);
    }

    class ViewHolder {
        RelativeLayout parentview;
        ImageView icon;
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SlidingMenuLeft slidingMenuLeft = (SlidingMenuLeft) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sliding_menu_gv_item, null);
            holder = new ViewHolder();
            holder.parentview = (RelativeLayout) convertView.findViewById(R.id.parentview);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        DisplayImageOptions options1 = null;
//        if() {
//            options1 = new DisplayImageOptions.Builder()
//                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.pic_download)
//                    .showImageForEmptyUri(R.drawable.sarrs_main_default)
//                    .showImageOnLoading(R.drawable.sarrs_main_default)
//                    .build();
//        }
        ImageLoader.getInstance().displayImage(slidingMenuLeft.getIcon(), holder.icon);
        holder.title.setText(slidingMenuLeft.getTitle());
        holder.title.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
//        holder.parentview.setOnTouchListener(new MyOnTouchLinserner(holder, slidingMenuLeft));
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        return convertView;
    }

    class MyOnTouchLinserner implements View.OnTouchListener{
        ViewHolder viewHolder;
        SlidingMenuLeft slidingMenuLeft;
        public MyOnTouchLinserner(ViewHolder viewHolder,SlidingMenuLeft slidingMenuLeft){
            this.viewHolder = viewHolder;
            this.slidingMenuLeft = slidingMenuLeft;
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_UP:
                    ImageLoader.getInstance().displayImage(slidingMenuLeft.getIcon(), viewHolder.icon);
                    viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
                    view.performClick();
                    break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    ImageLoader.getInstance().displayImage(slidingMenuLeft.getIcon_select(), viewHolder.icon);
                    viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_c5242b));
                    break;
                default:
                    ImageLoader.getInstance().displayImage(slidingMenuLeft.getIcon(), viewHolder.icon);
                    viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
                    break;
            }
            return true;
        }
    }

}