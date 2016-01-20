package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainMenuItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.DeleteRelativelayout;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.chaojishipin.sarrs.widget.SarrsMainMenuView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xll on 2015/6/17.
 */
public class MainActivityChannelAdapter2 extends MyBaseAdapter {

    public final static int TYPE_COUNT = 2;

    public final static String BIG = "1";

    public final static String SMALL = "4";

    public ArrayList<MainActivityAlbum> mAlbums;

    public ArrayList<MainMenuItem> getMenuStates() {
        return menuStates;
    }

    public ArrayList<MainMenuItem> menuStates=new ArrayList<>();

    public MainActivityChannelAdapter2(Context context) {
        super(context);
    }

    public void setMenuStates(ArrayList<MainMenuItem> states){
        this.menuStates=states;
    }

    class ViewHolder {
        // big
        TextView category;
        TextView posterTitle;
        TextView posterSubTitle1;
        TextView posterSubTitle2;
        TextView posterWatchNumber;
        EqualRatioImageView poster;
        RelativeLayout big_layout;
        ImageView poster_play_icon;
        ImageView more_icon;

        // small
        TextView smallCategory;
        TextView smallTitle;
        TextView smallSubTitle1;
        TextView smallSubTitle2;
        TextView smallWatchNumber;
        EqualRatioImageView smallPoster;
        RelativeLayout small_layout;
        ImageView small_play_icon;
        ImageView main_small_more_icon;
        View divider;

        public  DeleteRelativelayout menu_item;
    }

    @Override
    public View getView(int position, View convertView) {
        int posterType = 0;

        MainActivityAlbum mainActivityAlbum = mAlbums.get(position);
        if (mainActivityAlbum.getContentType().equals(BIG)) {
            posterType = ConstantUtils.MAIN_DATA_TYPE_1;

        } else if (mainActivityAlbum.getContentType().equals(SMALL)) {
            posterType = ConstantUtils.MAIN_DATA_TYPE_2;

        }
        ViewHolder holder = null;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mainactivity_fragment_listview_big_model2, null);

            // LogUtil.e("xll","main convertView Height "+convertView.getHeight());
            holder = new ViewHolder();
            holder.divider = (View) convertView.findViewById(R.id.divider);
            holder.category = (TextView) convertView.findViewById(R.id.main_frontview_tag);
            holder.posterTitle = (TextView) convertView.findViewById(R.id.main_frontview_poster_title);
            holder.posterSubTitle1 = (TextView) convertView.findViewById(R.id.main_frontview_poster_comment1);
            holder.posterSubTitle2 = (TextView) convertView.findViewById(R.id.main_frontview_poster_comment2);
            holder.poster = (EqualRatioImageView) convertView.findViewById(R.id.main_frontview_poster);
            holder.more_icon = (ImageView) convertView.findViewById(R.id.more_icon);
            holder.big_layout = (RelativeLayout) convertView.findViewById(R.id.big_layout);
            holder.poster_play_icon = (ImageView) convertView.findViewById(R.id.main_frontview_feed_playicon);
            holder.posterWatchNumber = (TextView) convertView.findViewById(R.id.main_frontview_feed_play_count);

            holder.smallCategory = (TextView) convertView.findViewById(R.id.main_feed_small_poster_tag);
            holder.smallTitle = (TextView) convertView.findViewById(R.id.small_poster_title);
            holder.smallSubTitle1 = (TextView) convertView.findViewById(R.id.small_poster_comment1);
            holder.smallSubTitle2 = (TextView) convertView.findViewById(R.id.small_poster_comment2);
            holder.small_play_icon = (ImageView) convertView.findViewById(R.id.main_small_feed_playicon);
            holder.smallWatchNumber = (TextView) convertView.findViewById(R.id.main_small_feed_play_count);
            holder.smallPoster = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            holder.main_small_more_icon = (ImageView) convertView.findViewById(R.id.main_small_more_icon);
            holder.small_layout = (RelativeLayout) convertView.findViewById(R.id.small_layout);
//            holder.menu_item = (DeleteRelativelayout) convertView.findViewById(R.id.save_item);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
//        SarrsMainMenuView mainMenuView=(SarrsMainMenuView)holder.menu_item.getChildAt(0);
//        FrameLayout root=(FrameLayout)mainMenuView.getChildAt(0);
//        LinearLayout ln=(LinearLayout)root.getChildAt(0);
        if(menuStates.get(position).isDelete()){
            // img.setBackgroundColor(mContext.getResources().getColor(R.color.color_c5242b));
        }
//        if(menuStates.get(position).isSave()){
//            if(ln!=null&&ln.getChildAt(1)!=null){
//                LinearLayout layout=(LinearLayout)ln.getChildAt(1);
//                if(layout!=null&&layout.getChildAt(0)!=null){
//                    ImageView img=(ImageView)layout.getChildAt(0);
//                    LogUtil.e("xll", "main adapter save img exute " + position);
//                    img.setImageResource(R.drawable.sarrs_pic_mainloving_press);
//                }
//            }
//
//        }else{
//            if(ln!=null&&ln.getChildAt(1) != null){
//                LinearLayout layout=(LinearLayout)ln.getChildAt(1);
//                if(layout!=null&&layout.getChildAt(0)!=null){
//                    ImageView img=(ImageView)layout.getChildAt(0);
//                    LogUtil.e("xll", "main adapter save img exute " + position);
//                    img.setImageResource(R.drawable.sarrs_pic_mainloving_normal);
//                }
//            }
//
//
//
//        }
        if(menuStates.get(position).isSare()){
            // ln.getChildAt(2).setBackgroundColor(mContext.getResources().getColor(R.color.color_c5242b));
        }
        holder.more_icon.setOnClickListener(new OnMoreTagClick());
        holder.main_small_more_icon.setOnClickListener(new OnMoreTagClick());
        // holder.divider.setVisibility(View.VISIBLE);
        if (posterType == ConstantUtils.MAIN_DATA_TYPE_1) {
            holder.small_layout.setVisibility(View.GONE);
            holder.big_layout.setVisibility(View.VISIBLE);
            holder.poster_play_icon.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mainActivityAlbum.getLable())) {
                holder.category.setVisibility(View.GONE);
            } else {
                holder.category.setVisibility(View.VISIBLE);
                holder.category.setText(mainActivityAlbum.getLable() + "");
            }

            holder.posterTitle.setText(mainActivityAlbum.getTitle() + "");

            // 设置描述
            int width = Utils.getWidthPixels(mContext);
            int firstSize = (width - Utils.dip2px(38)) / Utils.dip2px(12);
            Log.d("lineSize", "width is " + width + "and size is " + firstSize);
            String desc = mainActivityAlbum.getDescription();
            if (desc.length() <= firstSize) {
                holder.posterSubTitle1.setText(desc);
                holder.posterSubTitle2.setVisibility(View.GONE);
            } else {
                holder.posterSubTitle2.setVisibility(View.VISIBLE);
                holder.posterSubTitle1.setText(desc.substring(0, firstSize));
                holder.posterSubTitle2.setText(desc.substring(firstSize));
            }
            holder.posterWatchNumber.setText(mainActivityAlbum.getPlay_count() + "");
            displayImage(mainActivityAlbum.getImgage(), holder.poster, R.drawable.sarrs_main_default);
        } else {
            holder.big_layout.setVisibility(View.GONE);
            holder.small_layout.setVisibility(View.VISIBLE);
            holder.small_play_icon.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mainActivityAlbum.getLable())) {
                holder.smallCategory.setVisibility(View.GONE);
            } else {
                holder.smallCategory.setText(mainActivityAlbum.getLable() + "");
            }
            holder.smallTitle.setText(mainActivityAlbum.getTitle() + "");
            // 设置描述
            int width = Utils.getWidthPixels(mContext);
            int firstSize = (width - Utils.dip2px(74)) / Utils.dip2px(12);
            Log.d("lineSize", "small width is " + width + "and size is " + firstSize);
            String desc = mainActivityAlbum.getDescription();
            if (desc.length() <= firstSize) {
                holder.smallSubTitle1.setText(desc);
                holder.smallSubTitle2.setVisibility(View.GONE);
            } else {
                holder.smallSubTitle2.setVisibility(View.VISIBLE);
                holder.smallSubTitle1.setText(desc.substring(0, firstSize));
                holder.smallSubTitle2.setText(desc.substring(firstSize));
            }

            holder.smallWatchNumber.setText(mainActivityAlbum.getPlay_count() + "");
            displayImage(mainActivityAlbum.getImgage(), holder.smallPoster, R.drawable.sarrs_main_default);
        }
        if (position == 0) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
//        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        convertView.measure(width,height);
        return convertView;

    }


    @Override
    public int getCount() {
        if (null != mAlbums) {
            return mAlbums.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mAlbums) {
            return mAlbums.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mAlbums && mAlbums.size() > 0) {
            MainActivityAlbum mainActivityAlbum = mAlbums.get(position);
            if (mainActivityAlbum.getContentType().equals(BIG)) {
                return ConstantUtils.MAIN_DATA_TYPE_1;
            } else if (mainActivityAlbum.getContentType().equals(SMALL)) {
                return ConstantUtils.MAIN_DATA_TYPE_2;
            }
        }
        return ConstantUtils.MAIN_DATA_TYPE_1;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    public ArrayList<MainActivityAlbum> getmAlbums() {
        return mAlbums;
    }

    public void setmAlbums(ArrayList<MainActivityAlbum> mAlbums) {
        this.mAlbums = mAlbums;
    }

    class OnMoreTagClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.more_icon:
                    LogUtil.e("hello ", "from big img btn");

                    break;
                case R.id.main_small_more_icon:
                    LogUtil.e("hello ", "from small img btn");
                    break;
            }

        }
    }

}
