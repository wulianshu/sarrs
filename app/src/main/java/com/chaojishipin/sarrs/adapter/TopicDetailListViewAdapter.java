package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.TopicDetail;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wulianshu on 2015/8/25.
 */
public class TopicDetailListViewAdapter<LetvBaseBean>  extends CommonAdapter<LetvBaseBean>  {
   public TopicDetailListViewAdapter(Context context,SarrsArrayList mDatas){
       super(context,mDatas);
   }
    class ViewHolder {
        TextView topicTitle;
        TextView play_count;
        TextView tv_subtitle;
        EqualRatioImageView poster;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.topicdetail_listview_item, null);
            holder = new ViewHolder();
            holder.topicTitle = (TextView) view.findViewById(R.id.small_poster_title);
            holder.play_count = (TextView) view.findViewById(R.id.main_small_feed_play_count);
            holder.poster = (EqualRatioImageView) view.findViewById(R.id.main_feed_small_poster);
            holder.tv_subtitle = (TextView) view.findViewById(R.id.small_poster_subtitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TopicDetail atopic = (TopicDetail) mDatas.get(i);
        holder.topicTitle.setText(atopic.getTitle());
        holder.play_count.setText(atopic.getPlay_count() + "");
        holder.tv_subtitle.setText(atopic.getDescription());
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(atopic.getImage(), holder.poster, options1);
//      ImageLoader.getInstance().displayImage(atopic.getImage(), holder.poster);
        return view;
    }
}
