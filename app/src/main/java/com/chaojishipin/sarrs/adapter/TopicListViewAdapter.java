package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wulianshu on 2015/8/24.  SlidingMenuLeftAdapter<LetvBaseBean>
 */
public class TopicListViewAdapter<LetvBaseBean>  extends CommonAdapter<LetvBaseBean> {


    // 上下文对象
    public TopicListViewAdapter(Context context,SarrsArrayList mDatas) {

        super(context, mDatas);
    }

    class ViewHolder {
        TextView tv_play_count;
        TextView posterTitle;
        TextView posterSubTitle;
        EqualRatioImageView poster;
        View divider_bottom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_listview_item, null);
            holder = new ViewHolder();
            holder.posterTitle = (TextView) convertView.findViewById(R.id.main_frontview_poster_title);
            holder.posterSubTitle = (TextView) convertView.findViewById(R.id.main_frontview_poster_comment1);
            holder.poster = (EqualRatioImageView) convertView.findViewById(R.id.main_frontview_poster);
            holder.tv_play_count = (TextView) convertView.findViewById(R.id.tv_play_count);
            holder.divider_bottom = convertView.findViewById(R.id.divider_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Topic atopic = (Topic) mDatas.get(position);
        holder.posterTitle.setText(atopic.getTitle());
        holder.posterSubTitle.setText(atopic.getDescription() + "");
        holder.tv_play_count.setText(atopic.getPlay_count());
        holder.divider_bottom.setVisibility(View.VISIBLE);
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(atopic.getImage(), holder.poster, options1);
//      ImageLoader.getInstance().displayImage(atopic.getImage(), holder.poster);
        return convertView;
    }
}