package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangshuo on 2015/6/6.
 */
public class CommonViewHolder {

    private final SparseArray<View> mViews;

    private View mConvertView;

    private CommonViewHolder(Context context,ViewGroup parent,int layoutId,int position ) {
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //将mConvertView与当前ViewHolder绑定
        mConvertView.setTag(this);
    }

    /**
     * 获取一个ViewHolder对象
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static CommonViewHolder get(Context context,View convertView,ViewGroup parent,
                                       int layoutId,int position) {
        if (null == convertView) {
            return new CommonViewHolder(context, parent, layoutId, position);
        }
        return (CommonViewHolder) convertView.getTag();
    };

    /**
     * 通过ID获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends  View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (null == view) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    };

    public View getmConvertView() {
        return mConvertView;
    }

}
