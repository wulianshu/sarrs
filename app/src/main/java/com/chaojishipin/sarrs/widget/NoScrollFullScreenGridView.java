package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

public class NoScrollFullScreenGridView extends GridView {
    int dividerColor=getContext().getResources().getColor(R.color.color_434344);
    public NoScrollFullScreenGridView(Context context) {
        this(context, null);
    }
    public NoScrollFullScreenGridView(Context context, AttributeSet attrs) {
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
                }


        }


    }
}
