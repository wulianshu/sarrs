package com.chaojishipin.sarrs.adapter;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class SparseArrayAdapter<T> extends BaseAdapter {
	
	protected SparseArray<T> mList;
	protected Activity mContext;
	protected ListView mListView;
	
	

	public SparseArrayAdapter(SparseArray<T> mList, Activity mContext) {
		this.mList = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		if(mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public T getItem(int position) {
		return mList == null ? null : mList.valueAt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setList(SparseArray<T> list){
		this.mList = list;
		notifyDataSetChanged();
	}
	
	public SparseArray<T> getList(){
		return mList;
	}
	
	public ListView getListView(){
		return mListView;
	}
	
	public void setListView(ListView listView){
		mListView = listView;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
