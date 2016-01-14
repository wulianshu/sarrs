package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchSuggestDataList;
import com.chaojishipin.sarrs.bean.SearchSuggestInfos;

/**
 * Created by daipei
 */
public class SearchSuggestAdapter extends BaseAdapter {

    public final static int TYPE_VIEW = 0;//正常界面

    public final static int TYPE_NULL = 1;//空白界面

//    public final static int TYPE_NO_RESULT_TIP = 2;//没有找到提示界面

    public final static int TYPE_COUNT = 3;

    private Context mContext;

    private SearchSuggestInfos mData;

    private SarrsArrayList<SearchSuggestDataList> mDataList;


    public SearchSuggestAdapter(Context context, SearchSuggestInfos data) {
        super();
        mContext = context;
        mData = data;
        mDataList = getResultDataList(data);
    }

    private SarrsArrayList<SearchSuggestDataList> getResultDataList(SearchSuggestInfos data){
        if (null != data){
            mDataList = data.getItems();
        }
        return mDataList;
    }

    public void setData(SearchSuggestInfos data){
        mDataList = getResultDataList(data);
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
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        SearchSuggestDataList dataList = (SearchSuggestDataList) getItem(position);

        int view_type;
        switch(dataList.getView_type()){
            case TYPE_VIEW:
                view_type = TYPE_VIEW;
                break;
            case TYPE_NULL:
                view_type = TYPE_NULL;
                break;
            default:
                view_type = -1;
                break;
        }
        return view_type;
    };



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        CommonViewHolder commonViewHolder = null;
        if (TYPE_VIEW == type) {
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_suggest_layout_item, position);
            SearchSuggestDataList searchSuggestDataList = (SearchSuggestDataList) mDataList.get(position);
//            EqualRatioImageView poster_img = (EqualRatioImageView) commonViewHolder.getView(R.id.poster_img);
//            //使用三级图片缓存加载ICON
//            ImageCacheManager.loadImage(searchResultDataList.getImages(), poster_img, null, null);
            TextView title = (TextView)commonViewHolder.getView(R.id.searchactivity_suggest_layout_item_title);
            title.setText(searchSuggestDataList.getTitle());

            LinearLayout play_layout = (LinearLayout)commonViewHolder.getView(R.id.searchactivity_suggest_play_layout);
            if(null != searchSuggestDataList.getSearchSuggest_playinfo()){
                play_layout.setVisibility(View.VISIBLE);
            }else{
                play_layout.setVisibility(View.GONE);
            }
        }
        else if (TYPE_NULL == type) {
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.searchactivity_suggest_layout_item_null, position);
        }
        return commonViewHolder.getmConvertView();
    }


}
