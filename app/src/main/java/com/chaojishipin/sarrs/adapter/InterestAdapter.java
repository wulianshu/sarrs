package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.InterestEntity;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;

/**
 * Created by wangyemin on 2015/10/14.
 */
public class InterestAdapter<LetvBaseBean> extends CommonAdapter<LetvBaseBean> {
    public Boolean[] mSelectArr;

    public InterestAdapter(Context context, SarrsArrayList mDatas) {
        super(context, mDatas);
    }

    public void setmSelectArr(Boolean[] mSelectArr) {
        this.mSelectArr = mSelectArr;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.interest_grid_item, null);
            holder = new ViewHolder();
            holder.poster = (ImageView) view.findViewById(R.id.interest_item_poster);
            holder.selectBtn = (ImageView) view.findViewById(R.id.select_btn);
            holder.title = (TextView) view.findViewById(R.id.interest_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        InterestEntity item = (InterestEntity) mDatas.get(position);

        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        ImageLoader.getInstance().displayImage(item.getImage(), holder.poster,options1);
        holder.title.setText(item.getTitle());
        if (mSelectArr != null) {
//            Log.d("isPlaying", " pos is " + position + " and arr is " + Arrays.toString(mSelectArr));
            if (mSelectArr[position]) {
                holder.selectBtn.setVisibility(View.VISIBLE);
            } else {
                holder.selectBtn.setVisibility(View.GONE);
            }
        }
        return view;
    }

    class ViewHolder {
        ImageView poster;
        ImageView selectBtn;
        TextView title;
    }

    public void setSelectItem(int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageView selectBtn = viewHolder.selectBtn;
        if (mSelectArr != null) {
            if (mSelectArr[position]) {
                selectBtn.setVisibility(View.GONE);
                mSelectArr[position] = false;
            } else {
                selectBtn.setVisibility(View.VISIBLE);
                mSelectArr[position] = true;
            }
        }
    }
}
