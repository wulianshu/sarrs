package com.chaojishipin.sarrs.photo;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.ImageInfo;
import com.chaojishipin.sarrs.photo.util.CommonAdapter;
import com.chaojishipin.sarrs.photo.util.ImageLoader;
import com.chaojishipin.sarrs.photo.util.ViewHolder;


public class MyAdapter extends CommonAdapter<ImageInfo>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public MyAdapter(Context context, List<ImageInfo> mDatas, int itemLayoutId,
			String dirPath)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(ViewHolder helper, final ImageInfo item,int postion)
	{
		final ImageView mImageView = helper.getView(R.id.mdetail_activity_gridview_img);
		if(item.getType()==0){
			mImageView.setColorFilter(null);
			mImageView.setImageResource(R.drawable.sarrs_pic_no_default);
			mImageView.setBackgroundColor(mContext.getResources().getColor(R.color.color_00000000));
			ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPath + "/" + item.getUrl(), mImageView);
		}else{
			mImageView.setColorFilter(null);
			mImageView.setBackgroundColor(mContext.getResources().getColor(R.color.color_999999));
			mImageView.setImageResource(R.drawable.sarrs_pic_camera);
		}

	}
}
