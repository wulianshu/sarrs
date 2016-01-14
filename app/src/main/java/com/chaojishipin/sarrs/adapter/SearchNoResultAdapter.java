package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
//import com.chaojishipin.sarrs.bean.SarrsArrayList;
//import com.chaojishipin.sarrs.bean.SearchResultDataList;
//import com.chaojishipin.sarrs.bean.SearchResultInfos;
//import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * Created by daipei
 */
public class SearchNoResultAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<MainActivityAlbum> mDataList;


    public SearchNoResultAdapter(Context context, ArrayList<MainActivityAlbum> albums) {
        super();
        mContext = context;
        mDataList = albums;
    }

    public void setData(ArrayList<MainActivityAlbum> albums) {
        mDataList = albums;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != mDataList) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mDataList) {
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
        commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_result_layout_item, position);
        MainActivityAlbum searchResultDataList = (MainActivityAlbum) mDataList.get(position);
        EqualRatioImageView poster_img = (EqualRatioImageView) commonViewHolder.getView(R.id.poster_img);
        //使用三级图片缓存加载ICON
//        ImageCacheManager.loadImage(searchResultDataList.getImgage(), poster_img, null, null);
        DisplayImageOptions options1= new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                .showImageForEmptyUri(R.drawable.sarrs_main_default)
                .showImageOnLoading(R.drawable.sarrs_main_default)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(searchResultDataList.getImgage(),poster_img,options1);
//        ImageCacheManager.loadImage(searchResultDataList.getImgage(), new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                if (response.getBitmap() != null) {
//                    poster_img.setImageBitmap(response.getBitmap());
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                poster_img.setImageResource(R.drawable.search_default_poster);
//            }
//        });
        TextView categoryName = (TextView) commonViewHolder.getView(R.id.searchactivity_result_layout_item_categoryName);
        categoryName.setText(searchResultDataList.getLable());
        TextView name = (TextView) commonViewHolder.getView(R.id.searchactivity_result_layout_item_video_name);
        name.setText(searchResultDataList.getTitle());
        TextView play_conut_txt = (TextView) commonViewHolder.getView(R.id.play_conut_txt);
        play_conut_txt.setText(searchResultDataList.getPlay_count());

        return commonViewHolder.getmConvertView();
    }


}
