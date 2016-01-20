package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

public class NoScrollGridView extends GridView {
    int dividerColor=getContext().getResources().getColor(R.color.color_F3F3F3);
    public NoScrollGridView(Context context) {
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
    public NoScrollGridView(Context context, AttributeSet attrs) {
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
        View localView1 = getChildAt(0);
        if (localView1 != null) {
            int column = getWidth() / localView1.getWidth();
            int childCount = getChildCount();
            Paint localPaint;
            localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setStrokeWidth(Utils.dip2px(1));
            localPaint.setColor(dividerColor);
            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                // 先画横线
                if (i == 0) {
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft()+cellView.getWidth() * (childCount > column ? column : childCount), cellView.getTop(), localPaint);
                }
                if (childCount % column == 0 && ((i % column < column - 1))) {
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                } else if (childCount % column != 0 && (i % column < childCount % column-1)) {
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                } else {
                        canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                        canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                        canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);


                }

				/*if((i + 1) % column == 0){
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
				}else if((i + 1) > (childCount - (childCount % column))){
					canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
				}else{
					canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
					canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
				}*/
            }
            // 画出每一行不够column剩余空白grid
			/*if(childCount % column != 0){
				for(int j = 0 ;j < (column-childCount % column) ; j++){
					View lastView = getChildAt(childCount - 1);
					canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth()* j, lastView.getBottom(), localPaint);
				}
			}*/
        }


    }
}
