package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.StorageBean;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulinlin on 2016/1/27.
 */
public class StorageAdapter extends BaseAdapter {

    private Context mContext;
    public List<StorageBean>mList;

    public StorageAdapter(Context context ,List<StorageBean> list){
        this.mContext=context;
        this.mList=list;
    }

    @Override
    public int getCount() {
        return mList!=null?mList.size():0;
    }

    @Override
    public Object getItem(int i) {
        return mList!=null?mList.get(i):null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        StorageHolder holder;
        if (convertView == null) {
            holder = new StorageHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.modifyactivity_listview_item, viewGroup, false);
            holder.mTextView = (TextView) convertView.findViewById(R.id.mdetail_activity_listview_title);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.mdetail_activity_listview_img);
            convertView.setTag(holder);
        } else {
            holder = (StorageHolder) convertView.getTag();
        }

        if( mList.get(i).isClick()) {
            holder.mImageView.setVisibility(View.VISIBLE);
            LogUtil.e("isClick ", "click " + i);
        }else {
            holder.mImageView.setVisibility(View.GONE);
            LogUtil.e("isClick ", "unclick " + i);
        }
        holder.mTextView.setText(mList.get(i).getName());
        return convertView;
    }

    class StorageHolder{
        private TextView mTextView;
        private ImageView mImageView;


    }

}
