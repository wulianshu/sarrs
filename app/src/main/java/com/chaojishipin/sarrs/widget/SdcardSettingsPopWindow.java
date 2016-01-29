package com.chaojishipin.sarrs.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.StorageAdapter;
import com.chaojishipin.sarrs.utils.SPUtil;

import de.greenrobot.event.EventBus;


/**
 * Created by xll on 2016/1/27.
 * 网络状态
 */
public class SdcardSettingsPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {
	private View conentView;
	private NoScrollListView mListView;
	private StorageAdapter mAdapter;
	private Context mContext;

	public SdcardSettingsPopWindow(final Activity context,StorageAdapter datper) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.sdcard_setting_dialog, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		this.setContentView(conentView);
		this.setWidth(w / 2 + 50);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable dw = new ColorDrawable(0000000000);
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.setting_animate);
		mListView=(NoScrollListView)conentView.findViewById(R.id.sdcard_list);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(datper);
		this.mAdapter=datper;
		this.mContext=context;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if(mAdapter.mList!=null){
			if(mAdapter.mList.get(i).isEnable()){
				for(int j=0;j<mAdapter.mList.size();j++){
					mAdapter.mList.get(j).setIsClick(false);
				}
				mAdapter.mList.get(i).setIsClick(true);
				mAdapter.notifyDataSetChanged();
				this.dismiss();
				EventBus.getDefault().post(mAdapter.mList.get(i));
				Toast.makeText(mContext," "+mAdapter.mList.get(i).getName(),Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(mContext,mContext.getString(R.string.setting_notice_no_support),Toast.LENGTH_SHORT).show();

			}

		}
	}





	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
		} else {
			this.dismiss();
		}
	}




}
