package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class RankListListViewAdapter <LetvBaseBean>  extends CommonAdapter<LetvBaseBean> {


    // 上下文对象
    public RankListListViewAdapter(Context context,SarrsArrayList mDatas) {

        super(context, mDatas);
    }

    class ViewHolder {

        TextView posterTitle;
        TextView rank1;
        TextView rank2;
        TextView rank3;
        EqualRatioImageView poster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ranklist_listview_item, null);
            holder = new ViewHolder();
            holder.posterTitle = (TextView) convertView.findViewById(R.id.small_poster_title);
            holder.rank1 = (TextView) convertView.findViewById(R.id.rank1);
            holder.rank2 = (TextView) convertView.findViewById(R.id.rank2);
            holder.rank3 = (TextView) convertView.findViewById(R.id.rank3);
            holder.poster = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RankList rankList = (RankList) mDatas.get(position);
        holder.posterTitle.setText(rankList.getTitle());
        String ranks = rankList.getTitleitems();
        JSONArray jsonArray=null;
        try {
            jsonArray = new JSONArray(ranks);
        if(jsonArray.length() >=3) {
            holder.rank1.setText(jsonArray.get(0).toString());
            holder.rank2.setText(jsonArray.get(1).toString());
            holder.rank3.setText(jsonArray.get(2).toString());
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(rankList.getImage(), holder.poster,options1);
        return convertView;
    }
}