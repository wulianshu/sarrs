package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.RankListDetail;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class RankListDetailListViewAdapter<LetvBaseBean> extends CommonAdapter<LetvBaseBean> {
    // 上下文对象
    public RankListDetailListViewAdapter(Context context, SarrsArrayList mDatas) {

        super(context, mDatas);
    }

    class ViewHolder {
        TextView tv_rank;
        TextView tv_title;
        TextView tv_play_count;
        TextView tv_description;
        EqualRatioImageView poster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.topicdetail_listview_item, null);
            holder.tv_play_count = (TextView) convertView.findViewById(R.id.main_small_feed_play_count);
            holder.tv_rank = (TextView) convertView.findViewById(R.id.tv_rank);
            holder.tv_title = (TextView) convertView.findViewById(R.id.small_poster_title);
            holder.poster = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            holder.tv_description = (TextView) convertView.findViewById(R.id.small_poster_subtitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RankListDetail rankListDetail = (RankListDetail) mDatas.get(position);
//      System.out.println("holder:" + holder + "rankListDetail.getPlay_count():" + rankListDetail.getPlay_count());
        holder.tv_play_count.setText(rankListDetail.getPlay_count() + "");
        holder.tv_rank.setVisibility(View.VISIBLE);
        holder.tv_rank.setText((position + 2) + "");
        holder.tv_title.setText(rankListDetail.getTitle());
        holder.tv_description.setText(rankListDetail.getDescription());
//      ImageLoader.getInstance().displayImage(rankListDetail.getImage(), holder.poster);
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(rankListDetail.getImage(),holder.poster, options1);
        return convertView;
    }
}