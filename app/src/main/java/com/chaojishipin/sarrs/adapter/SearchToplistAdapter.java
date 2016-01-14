package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchSuggestDataList;

import java.util.ArrayList;

/**
 * Created by vicky on 15/11/18.
 */
public class SearchToplistAdapter extends BaseAdapter {
    private ArrayList<String> mDataList;
    private Context mContext;

    public SearchToplistAdapter(Context context, ArrayList<String> list) {
        super();
        mContext = context;
        mDataList = list;
    }

    public void setData(ArrayList<String> list){
        mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null!=mDataList){
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null!=mDataList){
            return mDataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder commonViewHolder = null;
        commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_history_layout_item, position);
        String name = (String) mDataList.get(position);
        TextView title = (TextView)commonViewHolder.getView(R.id.history_name);
        title.setText(name);
        return commonViewHolder.getmConvertView();
    }
}
