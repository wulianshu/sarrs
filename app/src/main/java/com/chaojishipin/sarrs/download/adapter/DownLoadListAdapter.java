package com.chaojishipin.sarrs.download.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadUtils;
import com.chaojishipin.sarrs.download.view.SquareLayoutForList;
import com.chaojishipin.sarrs.utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownLoadListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<Episode> episodes;
	private String site;
	
	// 用来控制item的选中状况
	private SparseArray<Boolean> isSelected;
	
	private String mdefaultClarity;
	// 用来控制item的清晰度是否存在
	private SparseArray<Boolean> isHasClarity;

	private class ItemHolder {
		TextView tv;
		TextView mPeriod;
		SquareLayoutForList sl;
	}

	public DownLoadListAdapter(Context mContext, ArrayList<Episode> episodes,String site) {
		super();
		this.mContext = mContext;
		this.episodes = episodes;
		this.inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
		this.isSelected = updateDownloadedFlagByDB(episodes,isSelected);
		this.site = site;
	}
	
	private void initData() {
		isSelected = new SparseArray<Boolean>();
		isHasClarity = new SparseArray<Boolean>();
		if (null != episodes && episodes.size() > 0) {
			for (int i = 0; i < episodes.size(); i++) {
				isSelected.put(i, false);
				isHasClarity.put(i, true);//默认存在
			}
		}
		
	}

	@Override
	public int getCount() {
		return episodes.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ItemHolder holder = new ItemHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.download_list_item, null);
			holder.tv = (TextView) convertView
					.findViewById(R.id.episode_list_item_text);
			holder.mPeriod = (TextView) convertView.findViewById(R.id.periods);
			holder.sl = (SquareLayoutForList) convertView.findViewById(R.id.layout);
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(episodes.get(position).getPorder())) {
			holder.mPeriod.setText(episodes.get(position).getPorder());
		}
		if (!TextUtils.isEmpty(episodes.get(position).getSubName())) {
			holder.tv.setText(episodes.get(position).getSubName());
		}
		setItemSelection(position,holder);
		return convertView;
	}

	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes=episodes;
		notifyDataSetChanged();
	}
	/**
	 * 清晰度切换，设置码流
	 */
	public void setPls(String defaultClarity) {
		mdefaultClarity = defaultClarity;
		notifyDataSetChanged();
	}
	// 设置item项的选中状态
			private void setItemSelection(int position, ItemHolder viewHolder) {
				if (null != isSelected && isSelected.size() > position) {
					if (isSelected.get(position)) {
						viewHolder.sl.setBackgroundResource(R.drawable.download_list_layout_bg_press);
//						viewHolder.tv.setTextColor(mContext.getResources().getColor(R.color.download_text_press));
					} else {
						viewHolder.sl.setBackgroundResource(R.drawable.download_list_layout_bg);
//						viewHolder.tv.setTextColor(mContext.getResources().getColor(R.color.download_text));
					}
				}
				//区分是否是乐视源
				if(!"letv".equals(site) && !"nets".equals(site) && !DownloadUtils.ISDOWNLOAD.equals(episodes.get(position).getIsdownload()) && !isSelected.get(position)){
					viewHolder.sl.setBackgroundResource(R.color.color_dedede);
				}else if("letv".equals(site)){
					String pls = episodes.get(position).getPls();
					if (!TextUtils.isEmpty(pls) && !isSelected.get(position)) {
						String[] types = pls.split(",");
						List<String> typeList = Arrays.asList(types);
						if(null!=mdefaultClarity && !typeList.contains(mdefaultClarity)){
							viewHolder.tv.setBackgroundResource(R.color.color_dedede);
							isHasClarity.put(position, false);
						}else if(typeList.contains(mdefaultClarity)){
							viewHolder.sl.setBackgroundResource(R.drawable.download_list_layout_bg);
							isHasClarity.put(position, true);
						}
					}
				}
//				if (null != mEpisodeList && mEpisodeList.size() > 0) {
//					SerialsItem serialsItem = mEpisodeList.get(position);
//					if (serialsItem.isDownloaded()) {
//						viewHolder.bigTitle.setTextColor(0xffc2c2c2);
//						viewHolder.smallTitle.setTextColor(0xffc2c2c2);
//						viewHolder.downloadSelectedIcon.setImageResource(R.drawable.download_added_icon);
//						viewHolder.downloadSelectedIcon.setVisibility(View.VISIBLE);
//					}
//				}
			}

		// 设置下载选中项
		public void setSelectedItem(int position, View view) {
			ItemHolder viewHolder = (ItemHolder) view.getTag();
			if (null != isSelected && isSelected.size() > position) {
				if (isSelected.get(position)) {
					
				} else {
					viewHolder.sl.setBackgroundResource(R.drawable.download_list_layout_bg_press);
//					viewHolder.tv.setTextColor(mContext.getResources().getColor(R.color.download_text_press));
					isSelected.put(position, true);
				}
			}
		}
		// 设置下载选中项
		public void setSelectedAllItem(int position) {
			if (null != isSelected && isSelected.size() > position) {
				if (!isSelected.get(position)) {
					isSelected.put(position, true);
				}
			}
		}		
		
		// 返回选中项是否已经在下载队列
			public boolean getIsSelected(int position) {
				boolean result = false;
				if (null != isSelected && isSelected.size() > position) {
					result = isSelected.get(position);
					
				}
				return result;
			}
			// 返回选中项是否存在对应的清晰度
			public boolean getIsHasClarity(int position) {
				boolean result = false;
				if (null != isHasClarity && isHasClarity.size() > position) {
					result = isHasClarity.get(position);
							
				}
				return result;
			}

		public SparseArray<Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(SparseArray<Boolean> isSelected) {
			this.isSelected = isSelected;
		}
		
		public void updateDownloadedFlag(){
			initData();
			updateDownloadedFlagByDB(episodes,isSelected);
		}

		// 根据下载数据库中的数据更新已下载标记
		public SparseArray<Boolean> updateDownloadedFlagByDB(ArrayList<Episode> episodeList,SparseArray<Boolean> isSelected) {
			ArrayList<DownloadJob> downloadList = DataUtils.getInstance().getAllDownloads();
			if (null != downloadList && downloadList.size() > 0) {
				DownloadEntity entity;
				String downloadSerialsId;
				for (int i = 0; i < downloadList.size(); i++) {
					entity = downloadList.get(i).getEntity();
					if (null != entity) {
						downloadSerialsId = entity.getId();
						if (null != episodeList && episodeList.size() > 0 && !TextUtils.isEmpty(downloadSerialsId)) {
							String serialId;
							for (int j = 0; j < episodeList.size(); j++) {
								serialId = episodeList.get(j).getSerialid();
								if (downloadSerialsId.equals(serialId)) {
									isSelected.put(j,true);
								}
							}
						}
					}
				}
			}
			return isSelected;
		}

}
