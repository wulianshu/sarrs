package com.chaojishipin.sarrs.swipe;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

import java.util.List;

/**
 * @author xll
 * @date 2015 06 25
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener {

    //    private SwipeMenuListView mListView;
    private SwipeMenuLayout mLayout;
    private SwipeMenu mMenu;
    private OnSwipeItemClickListener onItemClickListener;
    private int position;
    LayoutParams parentParams;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int callBackPositon = 0;
    public SwipeMenuAdapter.IpositionListener ipo;

    public SwipeMenuView(SwipeMenu menu, int position) {
        super(menu.getContext());

        parentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        setOrientation(VERTICAL);
        setLayoutParams(parentParams);
        mMenu = menu;
        List<SwipeMenuItem> items = menu.getMenuItems();
        int id = 0;
        for (SwipeMenuItem item : items) {
            addItem(item, id++, position);
        }
    }

    public void setIPOListender(SwipeMenuAdapter.IpositionListener ipo) {
        this.ipo = ipo;
    }

    private void addItem(SwipeMenuItem item, int id, int position) {
        LinearLayout subLayout = new LinearLayout(getContext());
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.WRAP_CONTENT);
        // 设置顶部MenuItem距离顶部边距和每个item相同
    /*	if(ipo!=null){
            if(ipo.getPosition()==0){
				params.topMargin=0;
			}else{
				params.topMargin= Utils.dip2px(20);
			}
		}*/
        subLayout.setId(id);
        subLayout.setGravity(Gravity.CENTER);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);

        subLayout.setLayoutParams(params);
        subLayout.setBackgroundDrawable(item.getBackground());
        subLayout.setOnClickListener(this);

        if (item.getIcon() != null) {
            subLayout.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            subLayout.addView(createTitle(item));
        }
//        if (id == 0) {
//            if (position != 0)
//                addView(createMarginView20dp());
//        }
        addView(subLayout);
    }


    public View createMarginView20dp() {
        View v = new View(getContext());
        v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(20)));
        v.setBackgroundResource(R.color.color_F3F3F3);
        return v;
    }

    private ImageView createIcon(SwipeMenuItem item) {
        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(item.getIcon());
        return iv;
    }

    private TextView createTitle(SwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && mLayout.isOpen()) {
            onItemClickListener.onItemClick(this, mMenu, v.getId());
        }
    }

    public OnSwipeItemClickListener getOnSwipeItemClickListener() {
        return onItemClickListener;
    }

    public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(SwipeMenuLayout mLayout) {
        this.mLayout = mLayout;
    }

    public interface OnSwipeItemClickListener {
        void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
    }
}
