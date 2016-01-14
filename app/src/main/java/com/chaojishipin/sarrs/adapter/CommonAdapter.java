package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuo on 2015/6/6.
 */
public abstract class CommonAdapter <T> extends BaseAdapter{

    protected LayoutInflater mInflater;
    protected Context mContext;

    protected ArrayList<LetvBaseBean> mDatas;

    public CommonAdapter (Context context,ArrayList<LetvBaseBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        if (null != mDatas) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mDatas) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setmDatas(ArrayList<LetvBaseBean> mDatas) {
        this.mDatas = mDatas;
    }

}
