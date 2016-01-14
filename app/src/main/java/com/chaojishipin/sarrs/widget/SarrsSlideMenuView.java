package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsSlideMenuItem;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xll on 2015/8/31.
 * 全屏播放下拉Menu
 */
public class SarrsSlideMenuView extends FrameLayout implements View.OnClickListener {

    private LinearLayout mSlideContent;
    private ImageView mIndicator;
    private LinearLayout mContentLayout;
    private FrameLayout mParent;
    private Context mContext;
    private onSlideMenuItemClick mOItem;


    public SarrsSlideMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        this.mContext = context;
        mParent = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.sarrs_slide_menuview, null);
        mContentLayout = (LinearLayout) mParent.findViewById(R.id.sarrs_slide_menuview_content);

        if (mItems != null && mItems.size() > 0) {
            for (int i = 0; i < mItems.size(); i++) {
                LinearLayout content1 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.sarrs_slide_menuitem, null);
                for (int j = 0; j < content1.getChildCount(); j++) {
                    if (content1.getChildAt(j) instanceof ImageView) {
                        ImageView img = (ImageView) content1.getChildAt(j);
                        img.setImageResource(mItems.get(i).getResId());
                    } else if (content1.getChildAt(j) instanceof TextView) {
                        TextView txt = (TextView) content1.getChildAt(j);
                        txt.setText(mItems.get(i).getTitle());
                    }

                }
                if (i == 0) {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_header);
                } else if (i == mItems.size() - 1) {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_footer);
                } else {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_middle);
                }
                content1.setId(i);
                content1.setOnClickListener(this);
                mContentLayout.addView(content1);
            }
            addView(mParent);
        }


    }

    public SarrsSlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (mOItem != null) {
            mOItem.onItemClick(v.getId(),v);
            LogUtil.e("SarrsSlideMenuView", " click id " + v.getId());
        }

    }

    public interface onSlideMenuItemClick {

        void onItemClick(int position,View view);

    }

    public void setOnSlideItemClick(onSlideMenuItemClick lis) {

        this.mOItem = lis;


    }

    List<SarrsSlideMenuItem> mItems;

    public List<SarrsSlideMenuItem> init(Context context) {
        mItems = new ArrayList<>();
        SarrsSlideMenuItem item1 = new SarrsSlideMenuItem();
        item1.setResId(R.drawable.sarrs_pic_widget_slide_menu_download_normal_fs);
        item1.setTitle(context.getString(R.string.sarrs_slide_menu_download));
        SarrsSlideMenuItem item2 = new SarrsSlideMenuItem();
        item2.setResId(R.drawable.sarrs_pic_widget_slide_menu_save_normal_fs);
        item2.setTitle(context.getString(R.string.sarrs_slide_menu_save));
        SarrsSlideMenuItem item3 = new SarrsSlideMenuItem();
        item3.setResId(R.drawable.sarrs_pic_widget_slide_menu_share_normal_fs);
        item3.setTitle(context.getString(R.string.sarrs_slide_menu_share));
        mItems.add(item1);
        mItems.add(item2);
        mItems.add(item3);
        return mItems;

    }

    interface SlideMenuCreator {

        void onCreate();

    }

    private SlideMenuCreator menuCreator;

    public void setSlideMenuCreator(SlideMenuCreator mCreator) {
        this.menuCreator = mCreator;
    }

}
