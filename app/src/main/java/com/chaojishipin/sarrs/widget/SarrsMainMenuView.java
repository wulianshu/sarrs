package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsSlideMenuItem;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xll on 2015/8/31.
 * 全屏播放下拉Menu
 */
public class SarrsMainMenuView extends FrameLayout implements View.OnClickListener {

    private LinearLayout mSlideContent;
    private ImageView mIndicator;
    private LinearLayout mContentLayout;
    private FrameLayout mParent;
    private Context mContext;

    public FrameLayout getmParent() {
        return mParent;
    }

    private onSlideMenuItemClick mOItem;

    public SarrsMainMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        this.mContext = context;
        mParent = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.sarrs_main_menuview, null);
        mContentLayout = (LinearLayout) mParent.findViewById(R.id.sarrs_main_menuview_content);
        if(menuCreator!=null){
            mItems=menuCreator.onCreate();
        }
        if (mItems != null && mItems.size() > 0) {
            for (int i = 0; i < mItems.size(); i++) {
                LinearLayout content1 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.sarrs_main_menuitem, null);
//                if(listviewItemHeight==0){
//                   LogUtil.e("xll","parent height is 0 ");
//                    return;
//                }
/*                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(listviewItemHeight/mItems.size()));

                content1.setLayoutParams(params);*/
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.height=0;
                params.width=LinearLayout.LayoutParams.MATCH_PARENT;
                params.weight=1;
                content1.setLayoutParams(params);
//              LogUtil.e("xll","Menu view height px"+listviewItemHeight/mItems.size());
                for (int j = 0; j < content1.getChildCount(); j++) {
                    if (content1.getChildAt(j) instanceof ImageView) {
                        ImageView img = (ImageView) content1.getChildAt(j);
                        img.setImageResource(mItems.get(i).getResId());
                    } else if (content1.getChildAt(j) instanceof TextView) {
                        TextView txt = (TextView) content1.getChildAt(j);
                        txt.setText(mItems.get(i).getTitle());
                    }

                }
               /* if (i == 0) {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_header);
                } else if (i == mItems.size() - 1) {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_footer);
                } else {
                    content1.setBackgroundResource(R.drawable.selector_slide_item_middle);
                }*/
                content1.setId(i);
                //TODO 在pulltoRefreshListView中会屏蔽掉
                content1.setOnClickListener(this);
                mContentLayout.addView(content1);
            }
            addView(mParent);
        }


    }

    public SarrsMainMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {
        if (mOItem != null) {
            mOItem.onItemClick(v.getId(),v,0,null);
            LogUtil.e("SarrsSlideMenuView", " click id " + v.getId());
        }

    }




    public interface onSlideMenuItemClick {

        void onItemClick(int position, View view,int parentId,ListAdapter adapter);

    }

    public void setOnSlideItemClick(onSlideMenuItemClick lis) {

        this.mOItem = lis;


    }
   public static int mode;
   public   void setMenuMode(int mode ){
       SarrsMainMenuView.mode=mode;

   }

    public static int listviewItemHeight;

    public  void setListViewItemHeight(int height){
        SarrsMainMenuView.listviewItemHeight=height;
    }

    List<SarrsSlideMenuItem> mItems;



    public List<SarrsSlideMenuItem> init(Context context) {
        if(SarrsMainMenuView.mode==ConstantUtils.SarrsMenuInitMode.MODE_DELETE){
            mItems = new ArrayList<>();
            SarrsSlideMenuItem item1 = new SarrsSlideMenuItem();
            item1.setResId(R.drawable.sarrs_pic_feedback_delete);
            item1.setTitle(context.getString(R.string.delete_up));
            mItems.add(item1);

        }else if(SarrsMainMenuView.mode== ConstantUtils.SarrsMenuInitMode.MODE_DELETE_SAVE_SHARE){
            mItems = new ArrayList<>();
            SarrsSlideMenuItem item1 = new SarrsSlideMenuItem();
            item1.setResId(R.drawable.sarrs_pic_feedback_delete);
            item1.setTitle(context.getString(R.string.sarrrs_str_delete));
            SarrsSlideMenuItem item2 = new SarrsSlideMenuItem();
            item2.setResId(R.drawable.sarrs_pic_mainloving_normal);
            item2.setTitle(context.getString(R.string.sarrs_slide_menu_save));
            SarrsSlideMenuItem item3 = new SarrsSlideMenuItem();
            item3.setResId(R.drawable.sarrs_pic_widget_slide_menu_share_normal_fs);
            item3.setTitle(context.getString(R.string.sarrs_slide_menu_share));
            mItems.add(item1);
            mItems.add(item2);
            mItems.add(item3);
        }



        return mItems;

    }

    interface SlideMenuCreator {

        List<SarrsSlideMenuItem> onCreate();

    }
    /**
     *  为以后动态添加 menu使用（暂不支持）
     * */
    private SlideMenuCreator menuCreator;

    public void setSlideMenuCreator(SlideMenuCreator mCreator) {
        this.menuCreator = mCreator;
    }

    public List<SarrsSlideMenuItem> getmItems() {
        return mItems;
    }
}
