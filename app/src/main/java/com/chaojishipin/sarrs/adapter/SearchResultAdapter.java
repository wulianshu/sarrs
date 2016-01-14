package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchResultDataList;
import com.chaojishipin.sarrs.bean.SearchResultInfos;
import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by daipei
 */
public class SearchResultAdapter extends BaseAdapter {

    public final static int TYPE_VIEW = 0;//正常界面

    public final static int TYPE_TIP = 1;//找到结果提示界面

    public final static int TYPE_NO_RESULT_TIP = 2;//没有找到提示界面

    public final static int TYPE_COUNT = 3;

    private Context mContext;

    private SearchResultInfos mData;

    private SarrsArrayList<SearchResultDataList> mDataList;


    public SearchResultAdapter(Context context, SearchResultInfos data) {
        super();
        mContext = context;
        mData = data;
        mDataList = getResultDataList(data);
    }

    private SarrsArrayList<SearchResultDataList> getResultDataList(SearchResultInfos data) {
        if (null != data) {
            mDataList = data.getItems();
        }
        return mDataList;
    }

    public void setData(SearchResultInfos data) {
        mDataList = getResultDataList(data);
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
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        SearchResultDataList dataList = (SearchResultDataList) getItem(position);

        int view_type;
        switch (dataList.getView_type()) {
            case TYPE_VIEW:
                view_type = TYPE_VIEW;
                break;
            case TYPE_TIP:
                view_type = TYPE_TIP;
                break;
            case TYPE_NO_RESULT_TIP:
                view_type = TYPE_NO_RESULT_TIP;
                break;
            default:
                view_type = -1;
                break;
        }
        return view_type;
    }

    ;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        CommonViewHolder commonViewHolder = null;
        if (TYPE_VIEW == type) {
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_result_layout_item, position);
            SearchResultDataList searchResultDataList = (SearchResultDataList) mDataList.get(position);
            final EqualRatioImageView poster_img = (EqualRatioImageView) commonViewHolder.getView(R.id.poster_img);
            //使用三级图片缓存加载ICON
//            ImageCacheManager.loadImage(searchResultDataList.getImage(), poster_img, null, null);
            DisplayImageOptions options1= new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                    .showImageForEmptyUri(R.drawable.sarrs_main_default)
                    .showImageOnLoading(R.drawable.sarrs_main_default)
                    .build();
            ImageLoader.getInstance().displayImage(searchResultDataList.getImage(), poster_img,options1);
//            , new ImageLoadingListener(){
//                @Override
//                public void onLoadingStarted(String s, View view) {
//                    poster_img.setImageResource(R.drawable.search_default_poster);
//                }
//                @Override
//                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                    poster_img.setImageResource(R.drawable.search_default_poster);
//                }
//                @Override
//                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//
//                }
//                @Override
//                public void onLoadingCancelled(String s, View view) {
//                    poster_img.setImageResource(R.drawable.search_default_poster);
//                }
//            }

//            ImageCacheManager.loadImage(searchResultDataList.getImage(), new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    if (response.getBitmap() != null) {
//                        poster_img.setImageBitmap(response.getBitmap());
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    poster_img.setImageResource(R.drawable.search_default_poster);
//                }
//            });

            TextView categoryName = (TextView) commonViewHolder.getView(R.id.searchactivity_result_layout_item_categoryName);
            categoryName.setText(searchResultDataList.getCategory_name());
            TextView name = (TextView) commonViewHolder.getView(R.id.searchactivity_result_layout_item_video_name);
            name.setText(searchResultDataList.getTitle());
            TextView play_conut_txt = (TextView) commonViewHolder.getView(R.id.play_conut_txt);
            play_conut_txt.setText(searchResultDataList.getPlay_count());
        } else if (TYPE_TIP == type) {
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_result_layout_head, position);
        }
        return commonViewHolder.getmConvertView();
    }


}
