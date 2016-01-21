package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

public class NoScrollGridViewNodivider extends GridView {
    int dividerColor = getContext().getResources().getColor(R.color.color_F3F3F3);

    public NoScrollGridViewNodivider(Context context) {
        this(context, null);
    }

  /*  public NoScrollGridView(Context context, AttributeSet attrs,int  defStyle){
        super(context, attrs,defStyle);*/
    /* TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NoScrollGridView, defStyle, 0);
     int n = a.getIndexCount();
     for (int i = 0; i < n; i++)
     {
         int attr = a.getIndex(i);
         switch (attr)
         {
             case R.styleable.divider_color:
                 dividerColor= a.getColor(attr, getContext().getResources().getColor(R.color.color_F3F3F3));
                 break;
         }
     }
     a.recycle();
 }*/
    public NoScrollGridViewNodivider(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
        setVerticalScrollBarEnabled(false);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
